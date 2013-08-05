package org.camelcookbook.examples.transactions.jmsrequestreplytransaction;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Demonstrates the correct use of transactions with JMS when you need to perform a request-reply.
 */
public class JmsRequestReplyTransactionSpringTest extends CamelSpringTestSupport {

    public static final int MAX_WAIT_TIME = 1000;

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/jmsRequestReplyTransaction-context.xml");
    }

    @Test
    public void testTransactedNoExceptionThrown() throws InterruptedException {
        String message = "this message is OK";

        // the request should be received by the
        MockEndpoint mockBackEndReply = getMockEndpoint("mock:backEndReply");
        mockBackEndReply.setExpectedMessageCount(1);

        String backendReply = "Backend processed: this message is OK";
        mockBackEndReply.message(0).body().isEqualTo(backendReply);

        template.sendBody("jms:instructions", message);

        assertMockEndpointsSatisfied();

        assertEquals(backendReply, consumer.receiveBody("jms:afterException", MAX_WAIT_TIME, String.class));
    }

    @Test
    public void testTransactedExceptionThrown() throws InterruptedException {
        String message = "this message will explode";

        // the back-end executed; it's status will be unaffected by the rollback
        MockEndpoint mockBackEndReply = getMockEndpoint("mock:backEndReply");
        mockBackEndReply.setExpectedMessageCount(1);

        String backendReply = "Backend processed: this message will explode";
        mockBackEndReply.message(0).body().isEqualTo(backendReply);

        template.sendBody("jms:instructions", message);

        // when transacted, ActiveMQ receives a failed signal when the exception is thrown
        // the message is placed into a dead letter queue
        assertEquals(message, consumer.receiveBody("jms:ActiveMQ.DLQ", MAX_WAIT_TIME, String.class));

        // no message is sent after the exception is thrown
        assertNull(consumer.receiveBody("jms:afterException", MAX_WAIT_TIME, String.class));
    }

}
