package org.camelcookbook.examples.transactions.jmstransaction;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Demonstrates the use of local transacted behavior defined on a JMS endpoint.
 */
public class JmsTransactionEndpointSpringTest extends CamelSpringTestSupport {

    public static final int MAX_WAIT_TIME = 1000;

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/jmsTransactionEndpoint-context.xml");
    }

    @Test
    public void testTransactedExceptionThrown() throws InterruptedException {
        String message = "this message will explode";

        // even though the route throws an exception, we don't have to deal with it here as we
        // don't send the message to the route directly, but to the broker, which acts as a middleman.
        template.sendBody("jms:instructions", message);

        // when transacted, ActiveMQ receives a failed signal when the exception is thrown
        // the message is placed into a dead letter queue
        assertEquals(message, consumer.receiveBody("jms:ActiveMQ.DLQ", MAX_WAIT_TIME, String.class));

        // the sending operation is performed in the same transaction, so it is rolled back
        assertNull(consumer.receiveBody("jms:beforeException", MAX_WAIT_TIME, String.class));

        // no message is ever sent after the exception is thrown
        assertNull(consumer.receiveBody("jms:afterException", MAX_WAIT_TIME, String.class));
    }
}
