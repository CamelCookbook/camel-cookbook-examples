package org.camelcookbook.examples.transactions.idempotentconsumerintransaction;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.processor.idempotent.jdbc.JdbcMessageIdRepository;
import org.apache.camel.spi.IdempotentRepository;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.camelcookbook.examples.transactions.dao.AuditLogDao;
import org.camelcookbook.examples.transactions.utils.EmbeddedDataSourceFactory;
import org.junit.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

/**
 * Demonstrates the use of onCompletion blocks.
 */
public class IdempotentConsumerInTransactionTest extends CamelTestSupport {

    private AuditLogDao auditLogDao;
    private DataSource auditDataSource;
    private IdempotentRepository idempotentRepository;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new IdempotentConsumerInTransactionRouteBuilder(idempotentRepository);
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        SimpleRegistry registry = new SimpleRegistry();
        auditDataSource = EmbeddedDataSourceFactory.getDataSource("sql/schema.sql");
        registry.put("auditDataSource", auditDataSource);

        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(auditDataSource);
        registry.put("transactionManager", transactionManager);

        SpringTransactionPolicy propagationRequired = new SpringTransactionPolicy();
        propagationRequired.setTransactionManager(transactionManager);
        propagationRequired.setPropagationBehaviorName("PROPAGATION_REQUIRED");
        registry.put("PROPAGATION_REQUIRED", propagationRequired);

        auditLogDao = new AuditLogDao(auditDataSource);

        TransactionTemplate transactionTemplate = new TransactionTemplate();
        transactionTemplate.setTransactionManager(transactionManager);
        transactionTemplate.setPropagationBehaviorName("PROPAGATION_REQUIRES_NEW");

        idempotentRepository = new JdbcMessageIdRepository(auditDataSource, transactionTemplate, "ws");

        CamelContext camelContext = new DefaultCamelContext(registry);
        SqlComponent sqlComponent = new SqlComponent();
        sqlComponent.setDataSource(auditDataSource);
        camelContext.addComponent("sql", sqlComponent);
        return camelContext;
    }

    @Test
    public void testTransactedExceptionThrown() throws InterruptedException {
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

        // even though the transaction rolled back, the repository should still contain an entry for this messageId
        assertTrue(idempotentRepository.contains("foo"));
    }

    @Test
    public void testTransactedExceptionNotThrown() throws InterruptedException {
        String message = "this message will be OK";
        assertEquals(0, auditLogDao.getAuditCount(message));

        MockEndpoint mockCompleted = getMockEndpoint("mock:out");
        mockCompleted.setExpectedMessageCount(1);

        template.sendBodyAndHeader("direct:transacted", message, "messageId", "foo");

        assertMockEndpointsSatisfied();
        assertEquals(1, auditLogDao.getAuditCount(message)); // the insert was successful

        // even though the transaction rolled back, the repository should still contain an entry for this messageId
        assertTrue(idempotentRepository.contains("foo"));
    }

    @Test
    public void testWebserviceExceptionRollsBackTransactionAndIdempotentRepository() throws InterruptedException {
        String message = "this message will be OK";
        assertEquals(0, auditLogDao.getAuditCount(message));

        MockEndpoint mockCompleted = getMockEndpoint("mock:out");
        mockCompleted.setExpectedMessageCount(0);

        MockEndpoint mockWs = getMockEndpoint("mock:ws");
        mockWs.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                throw new IllegalStateException("ws is down");
            }
        });

        try {
            template.sendBodyAndHeader("direct:transacted", message, "messageId", "foo");
            fail();
        } catch (CamelExecutionException cee) {
            assertEquals("ws is down", ExceptionUtils.getRootCause(cee).getMessage());
        }

        assertMockEndpointsSatisfied();
        assertEquals(0, auditLogDao.getAuditCount(message)); // the insert was successful

        // the repository has not seen this messageId
        assertTrue(!idempotentRepository.contains("foo"));
    }

}
