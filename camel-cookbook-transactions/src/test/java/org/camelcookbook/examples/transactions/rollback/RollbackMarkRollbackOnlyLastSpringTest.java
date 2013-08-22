package org.camelcookbook.examples.transactions.rollback;

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
 * Demonstrates the behavior of marking the last transaction for rollback.
 */
public class RollbackMarkRollbackOnlyLastSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/rollbackMarkRollbackOnlyLast-context.xml");
    }

    @Test
    public void testFailure() throws InterruptedException {
        AuditLogDao auditLogDao = getMandatoryBean(AuditLogDao.class, "auditLogDao");
        MessageDao messageDao = getMandatoryBean(MessageDao.class, "messageDao");

        String message = "this message will explode";
        assertEquals(0, auditLogDao.getAuditCount(message));

        // the outer route will continue to run as though nothing happened
        MockEndpoint mockOut1 = getMockEndpoint("mock:out1");
        mockOut1.setExpectedMessageCount(1);
        mockOut1.message(0).body().isEqualTo(message);

        // processing will not have reached the mock endpoint in the sub-route
        MockEndpoint mockOut2 = getMockEndpoint("mock:out2");
        mockOut2.setExpectedMessageCount(0);

        template.sendBody("direct:policies", message);

        assertMockEndpointsSatisfied();
        assertEquals(0, auditLogDao.getAuditCount(message));
        assertEquals(1, messageDao.getMessageCount(message));
    }

}
