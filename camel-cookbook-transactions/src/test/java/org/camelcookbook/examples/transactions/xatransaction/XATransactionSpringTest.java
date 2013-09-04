package org.camelcookbook.examples.transactions.xatransaction;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.camelcookbook.examples.transactions.dao.AuditLogDao;
import org.camelcookbook.examples.transactions.utils.ExceptionThrowingProcessor;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Demonstrates the use of an XA transaction manager with a JMS component and database.
 */
public class XATransactionSpringTest extends CamelSpringTestSupport {

    public static final int MAX_WAIT_TIME = 1000;

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/xaTransaction-context.xml");
    }

    @Test
    public void testTransactedRolledBack() throws InterruptedException {
        AuditLogDao auditLogDao = getMandatoryBean(AuditLogDao.class, "auditLogDao");

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
