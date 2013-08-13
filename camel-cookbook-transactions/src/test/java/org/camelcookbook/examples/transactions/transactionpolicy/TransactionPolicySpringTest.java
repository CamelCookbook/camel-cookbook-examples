package org.camelcookbook.examples.transactions.transactionpolicy;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.camelcookbook.examples.transactions.dao.AuditLogDao;
import org.camelcookbook.examples.transactions.dao.MessageDao;
import org.camelcookbook.examples.transactions.utils.ExceptionThrowingProcessor;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Demonstrates the use of policies to scope transactions.
 */
public class TransactionPolicySpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/transactionPolicy-context.xml");
    }

    @Test
    public void testFailureMock1() throws InterruptedException {
        AuditLogDao auditLogDao = getMandatoryBean(AuditLogDao.class, "auditLogDao");
        MessageDao messageDao = getMandatoryBean(MessageDao.class, "messageDao");

        String message = "ping";
        assertEquals(0, auditLogDao.getAuditCount(message));

        MockEndpoint mockOut1 = getMockEndpoint("mock:out1");
        mockOut1.whenAnyExchangeReceived(new ExceptionThrowingProcessor());
        mockOut1.message(0).body().isEqualTo(message);

        MockEndpoint mockOut2 = getMockEndpoint("mock:out2");
        mockOut2.setExpectedMessageCount(0);

        try {
            template.sendBody("direct:policies", message);
            fail();
        } catch (Exception e) {
            assertEquals("boom!", ExceptionUtils.getRootCause(e).getMessage());
        }

        assertMockEndpointsSatisfied();
        assertEquals(0, auditLogDao.getAuditCount(message));
        assertEquals(0, messageDao.getMessageCount(message));
    }


    @Test
    public void testFailureMock2() throws InterruptedException {
        AuditLogDao auditLogDao = getMandatoryBean(AuditLogDao.class, "auditLogDao");
        MessageDao messageDao = getMandatoryBean(MessageDao.class, "messageDao");

        String message = "ping";
        assertEquals(0, auditLogDao.getAuditCount(message));

        MockEndpoint mockOut1 = getMockEndpoint("mock:out1");
        mockOut1.setExpectedMessageCount(1);
        mockOut1.message(0).body().isEqualTo(message);

        MockEndpoint mockOut2 = getMockEndpoint("mock:out2");
        mockOut2.whenAnyExchangeReceived(new ExceptionThrowingProcessor());

        try {
        template.sendBody("direct:policies", message);
            fail();
        } catch (Exception e) {
            assertEquals("boom!", ExceptionUtils.getRootCause(e).getMessage());
        }

        assertMockEndpointsSatisfied();
        assertEquals(1, auditLogDao.getAuditCount(message));
        assertEquals(0, messageDao.getMessageCount(message));
    }

    @Test
    public void testSuccess() throws InterruptedException {
        AuditLogDao auditLogDao = getMandatoryBean(AuditLogDao.class, "auditLogDao");
        MessageDao messageDao = getMandatoryBean(MessageDao.class, "messageDao");

        String message = "ping";
        assertEquals(0, auditLogDao.getAuditCount(message));

        MockEndpoint mockOut1 = getMockEndpoint("mock:out1");
        mockOut1.setExpectedMessageCount(1);
        mockOut1.message(0).body().isEqualTo(message);

        MockEndpoint mockOut2 = getMockEndpoint("mock:out2");
        mockOut2.setExpectedMessageCount(1);
        mockOut2.message(0).body().isEqualTo(message);

        template.sendBody("direct:policies", message);

        assertMockEndpointsSatisfied();
        assertEquals(1, auditLogDao.getAuditCount(message));
        assertEquals(1, messageDao.getMessageCount(message));
    }

}
