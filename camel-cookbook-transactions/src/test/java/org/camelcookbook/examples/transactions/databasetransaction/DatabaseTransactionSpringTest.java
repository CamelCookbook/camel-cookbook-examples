package org.camelcookbook.examples.transactions.databasetransaction;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.camelcookbook.examples.transactions.dao.AuditLogDao;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Demonstrates the use of onCompletion blocks.
 */
public class DatabaseTransactionSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/databaseTransaction-context.xml");
    }

    @Test
    public void testNonTransactedSuccess() throws InterruptedException {
        AuditLogDao auditLogDao = getMandatoryBean(AuditLogDao.class, "auditLogDao");
        String message = "sample message";
        assertEquals(0, auditLogDao.getAuditCount(message));

        MockEndpoint mockCompleted = getMockEndpoint("mock:out");
        mockCompleted.setExpectedMessageCount(1);

        template.sendBody("direct:nonTransacted", message);

        assertMockEndpointsSatisfied();
        assertEquals(1, auditLogDao.getAuditCount(message));
    }

    @Test
    public void testNonTransactedExceptionThrown() throws InterruptedException {
        AuditLogDao auditLogDao = getMandatoryBean(AuditLogDao.class, "auditLogDao");
        String message = "this message will explode";
        assertEquals(0, auditLogDao.getAuditCount(message));

        MockEndpoint mockCompleted = getMockEndpoint("mock:out");
        mockCompleted.setExpectedMessageCount(0);

        try {
            template.sendBody("direct:nonTransacted", message);
            fail();
        } catch (CamelExecutionException cee) {
            assertEquals("Exchange caused explosion", ExceptionUtils.getRootCause(cee).getMessage());
        }

        assertMockEndpointsSatisfied();
        assertEquals(1, auditLogDao.getAuditCount(message)); // the insert was not rolled back
    }

    @Test
    public void testTransactedExceptionThrown() throws InterruptedException {
        AuditLogDao auditLogDao = getMandatoryBean(AuditLogDao.class, "auditLogDao");
        String message = "this message will explode";
        assertEquals(0, auditLogDao.getAuditCount(message));

        MockEndpoint mockCompleted = getMockEndpoint("mock:out");
        mockCompleted.setExpectedMessageCount(0);

        try {
            template.sendBody("direct:transacted", message);
            fail();
        } catch (CamelExecutionException cee) {
            assertEquals("Exchange caused explosion", ExceptionUtils.getRootCause(cee).getMessage());
        }

        assertMockEndpointsSatisfied();
        assertEquals(0, auditLogDao.getAuditCount(message)); // the insert was rolled back
    }
}
