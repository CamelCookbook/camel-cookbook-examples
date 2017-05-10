/*
 * Copyright (C) Scott Cranton, Jakub Korab, and Christian Posta
 * https://github.com/CamelCookbook
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camelcookbook.transactions.xatransaction;

import com.arjuna.ats.jta.TransactionManager;
import com.arjuna.ats.jta.UserTransaction;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.jms.pool.XaPooledConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.camelcookbook.transactions.dao.AuditLogDao;
import org.camelcookbook.transactions.utils.EmbeddedActiveMQBroker;
import org.camelcookbook.transactions.utils.EmbeddedDataSourceFactory;
import org.camelcookbook.transactions.utils.ExceptionThrowingProcessor;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.jta.JtaTransactionManager;

import java.util.Properties;

/**
 * Demonstrates the use of an XA transaction manager with a JMS component and database.
 */
public class XATransactionTest extends CamelTestSupport {

    public static final int MAX_WAIT_TIME = 1000;

    private AuditLogDao auditLogDao;

    @Rule
    public EmbeddedActiveMQBroker broker = new EmbeddedActiveMQBroker("embeddedBroker");
    private javax.transaction.TransactionManager arjunaTransactionManager;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new XATransactionRouteBuilder();
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {

        // JMS setup
        ActiveMQXAConnectionFactory xaConnectionFactory =
            new ActiveMQXAConnectionFactory();
        xaConnectionFactory.setBrokerURL(broker.getTcpConnectorUri());



        // JDBC setup
        DriverManagerDataSource h2ArjunaDataSource = new DriverManagerDataSource();
        h2ArjunaDataSource.setDriverClassName("com.arjuna.ats.jdbc.TransactionalDriver");
        h2ArjunaDataSource.setUrl("jdbc:arjuna:");
        Properties prop = new Properties();
        prop.setProperty("DYNAMIC_CLASS", "org.camelcookbook.transactions.xatransaction.H2DataSource");
        prop.setProperty("user", "sa");
        prop.setProperty("password", "");
        h2ArjunaDataSource.setConnectionProperties(prop);


        // Narayana/Arjuna setup
        arjunaTransactionManager = TransactionManager.transactionManager();
        arjunaTransactionManager.setTransactionTimeout(300);

        javax.transaction.UserTransaction arjunaUserTransaction = UserTransaction.userTransaction();
        arjunaUserTransaction.setTransactionTimeout(300);

        // Spring tx manager set up
        JtaTransactionManager jtaTransactionManager = new JtaTransactionManager();
        jtaTransactionManager.setTransactionManager(arjunaTransactionManager);
        jtaTransactionManager.setUserTransaction(arjunaUserTransaction);

        SpringTransactionPolicy propagationRequired = new SpringTransactionPolicy();
        propagationRequired.setTransactionManager(jtaTransactionManager);
        propagationRequired.setPropagationBehaviorName("PROPAGATION_REQUIRED");

        // set up audit dao for non XA tx
        log.info("building the audit database once and for all...");
        JdbcDataSource nonTxDataSource = EmbeddedDataSourceFactory.getJdbcDataSource("sql/schema.sql");
        auditLogDao = new AuditLogDao(nonTxDataSource);

        // build the camel context
        SimpleRegistry registry = new SimpleRegistry();
        registry.put("PROPAGATION_REQUIRED", propagationRequired);
        CamelContext camelContext = new DefaultCamelContext(registry);

        {
            SqlComponent sqlComponent = new SqlComponent();
            sqlComponent.setDataSource(h2ArjunaDataSource);
            camelContext.addComponent("sql", sqlComponent);
        }
        {
            // transactional JMS component
            XaPooledConnectionFactory pooledXaConnFactory = new XaPooledConnectionFactory();
            pooledXaConnFactory.setTransactionManager(arjunaTransactionManager);
            pooledXaConnFactory.setConnectionFactory(xaConnectionFactory);

            ActiveMQComponent activeMQComponent = new ActiveMQComponent();
            activeMQComponent.setConnectionFactory(pooledXaConnFactory);
            activeMQComponent.setTransactionManager(jtaTransactionManager);
            camelContext.addComponent("jms", activeMQComponent);
        }
        {
            // non-transactional JMS component setup for test purposes
            ActiveMQConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory();
            connectionFactory.setBrokerURL(broker.getTcpConnectorUri());

            ActiveMQComponent activeMQComponent = new ActiveMQComponent();
            activeMQComponent.setConnectionFactory(connectionFactory);
            activeMQComponent.setTransactionManager(jtaTransactionManager);
            camelContext.addComponent("nonTxJms", activeMQComponent);
        }
        return camelContext;
    }



    @Test
    public void testTransactedRolledBack() throws InterruptedException {
        String message = "this message will explode";
        assertEquals(0, auditLogDao.getAuditCount(message));

        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.whenAnyExchangeReceived(new ExceptionThrowingProcessor());

        // even though the route throws an exception, we don't have to deal with it here as we
        // don't send the message to the route directly, but to the broker, which acts as a middleman.
        template.sendBody("nonTxJms:inbound", message);

        // the consumption of the message is transacted, so a message should end up in the DLQ
        assertEquals(message, consumer.receiveBody("jms:ActiveMQ.DLQ", MAX_WAIT_TIME, String.class));

        // the send operation is performed while a database transaction is going on, so it is rolled back
        // on exception
        assertNull(consumer.receiveBody("jms:outbound", MAX_WAIT_TIME, String.class));

        assertEquals(0, auditLogDao.getAuditCount(message)); // the insert is rolled back
    }
}
