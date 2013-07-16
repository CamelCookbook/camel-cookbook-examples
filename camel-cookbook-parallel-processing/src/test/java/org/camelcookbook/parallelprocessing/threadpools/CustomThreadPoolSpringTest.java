package org.camelcookbook.parallelprocessing.threadpools;

import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.SynchronizationAdapter;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Test class that exercises a custom thread pool using the threads DSL.
 * @author jkorab
 */
public class CustomThreadPoolSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/META-INF/spring/threadPools-context.xml");
    }

    @Test
    public void testProcessedByCustomThreadPool() throws InterruptedException {
        final int messageCount = 50;
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.setExpectedMessageCount(messageCount);
        mockOut.setResultWaitTime(6000);

        for (int i = 0; i < messageCount; i++) {
            template.asyncSendBody("direct:in", "Message[" + i + "]");
        }

        assertMockEndpointsSatisfied();
        List<Exchange> receivedExchanges = mockOut.getReceivedExchanges();
        for (Exchange exchange: receivedExchanges) {
            assertTrue(exchange.getIn().getBody(String.class).endsWith("CustomThreadPool"));
        }
    }

}
