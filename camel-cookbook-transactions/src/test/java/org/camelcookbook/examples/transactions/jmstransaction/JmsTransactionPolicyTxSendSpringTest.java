package org.camelcookbook.examples.transactions.jmstransaction;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.camelcookbook.examples.transactions.utils.ExceptionThrowingProcessor;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Demonstrates the use of a transaction policy over a JMS component.
 */
public class JmsTransactionPolicyTxSendSpringTest extends CamelSpringTestSupport {

    public static final int MAX_WAIT_TIME = 1000;

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/jmsTransactionPolicyTxSend-context.xml");
    }

    @Test
    public void testTransactedExceptionThrown() throws InterruptedException {
        String message = "this message will explode";

        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.whenAnyExchangeReceived(new ExceptionThrowingProcessor());

        // even though the route throws an exception, we don't have to deal with it here as we
        // don't send the message to the route directly, but to the broker, which acts as a middleman.
        template.sendBody("jmsNonTx:inbound", message);

        // the receive operation itself should not be transacted, so nothing ends up on the DLQ
        assertNull(consumer.receiveBody("jmsNonTx:ActiveMQ.DLQ", MAX_WAIT_TIME, String.class));

        // because the sending was performed in a transaction, it is rolled back
        assertNull(consumer.receiveBody("jmsNonTx:outbound", MAX_WAIT_TIME, String.class));
    }
}
