package org.camelcookbook.examples.transactions.rollback;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.camelcookbook.examples.transactions.dao.AuditLogDao;
import org.camelcookbook.examples.transactions.utils.EmbeddedDataSourceFactory;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Demonstrates the behavior of marking a transaction for rollback, while continuing processing.
 */
public class RollbackMarkRollbackOnlyTest extends CamelTestSupport {

    private AuditLogDao auditLogDao;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RollbackMarkRollbackOnlyRouteBuilder();
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        SimpleRegistry registry = new SimpleRegistry();
        DataSource auditDataSource = EmbeddedDataSourceFactory.getDataSource("sql/schema.sql");

        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(auditDataSource);
        registry.put("transactionManager", transactionManager);

        SpringTransactionPolicy propagationRequired = new SpringTransactionPolicy();
        propagationRequired.setTransactionManager(transactionManager);
        propagationRequired.setPropagationBehaviorName("PROPAGATION_REQUIRED");
        registry.put("PROPAGATION_REQUIRED", propagationRequired);

        auditLogDao = new AuditLogDao(auditDataSource);

        CamelContext camelContext = new DefaultCamelContext(registry);
        SqlComponent sqlComponent = new SqlComponent();
        sqlComponent.setDataSource(auditDataSource);
        camelContext.addComponent("sql", sqlComponent);
        return camelContext;
    }

    @Test
    public void testTransactedRollback() throws InterruptedException {
        String message = "this message will explode";
        assertEquals(0, auditLogDao.getAuditCount(message));

        // the message does not proceed further down the route after the rollback statement
        MockEndpoint mockCompleted = getMockEndpoint("mock:out");
        mockCompleted.setExpectedMessageCount(0);

        // no exception is thrown despite the transaction rolling back
        template.sendBody("direct:transacted", message);

        assertMockEndpointsSatisfied();
        assertEquals(0, auditLogDao.getAuditCount(message)); // the insert was rolled back
    }
}
