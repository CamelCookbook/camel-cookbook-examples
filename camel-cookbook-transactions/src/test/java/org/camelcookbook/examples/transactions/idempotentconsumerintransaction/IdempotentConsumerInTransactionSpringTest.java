package org.camelcookbook.examples.transactions.idempotentconsumerintransaction;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.IdempotentRepository;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.camelcookbook.examples.transactions.dao.AuditLogDao;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Demonstrates the use of onCompletion blocks.
 */
public class IdempotentConsumerInTransactionSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/idempotentConsumerInTransaction-context.xml");
    }

    @Test
    public void testTransactedExceptionThrown() throws InterruptedException {
        AuditLogDao auditLogDao = getMandatoryBean(AuditLogDao.class, "auditLogDao");
        String message = "this message will explode";
        assertEquals(0, auditLogDao.getAuditCount(message));

        MockEndpoint mockCompleted = getMockEndpoint("mock:out");
        mockCompleted.setExpectedMessageCount(0);

        try {
            template.sendBodyAndHeader("direct:transacted", message, "messageId", "foo");
            fail();
        } catch (CamelExecutionException cee) {
            assertEquals("Exchange caused explosion", ExceptionUtils.getRootCause(cee).getMessage());
        }

        assertMockEndpointsSatisfied();
        assertEquals(0, auditLogDao.getAuditCount(message)); // the insert was rolled back
        IdempotentRepository idempotentRepository = getMandatoryBean(IdempotentRepository.class, "jdbcIdempotentRepository");

        // even though the transaction rolled back, the repository should still contain an entry for this messageId
        assertTrue(idempotentRepository.contains("foo"));
    }
}
