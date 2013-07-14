package org.camelcookbook.parallelprocessing.asyncprocessor;

import org.apache.camel.ExchangePattern;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Test class that exercises parallel threading using the threads DSL.
 * @author jkorab
 */
public class AsyncProcessorSpringTest extends CamelSpringTestSupport {
    final int messageCount = 10;

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/META-INF/spring/asyncProcessor-context.xml");
    }

    @Test
    public void testAsyncProcessing() throws InterruptedException {
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.setExpectedMessageCount(messageCount);
        mockOut.setResultWaitTime(5000);

        for (int i = 0; i < messageCount; i++) {
            template.sendBody("seda:in", ExchangePattern.InOnly, "Message[" + i + "]");
        }

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSyncProcessing() throws InterruptedException {
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.setExpectedMessageCount(messageCount);
        mockOut.setResultWaitTime(5000);

        for (int i = 0; i < messageCount; i++) {
            template.sendBody("direct:sync", ExchangePattern.InOnly, "Message[" + i + "]");
        }

        assertMockEndpointsSatisfied();
    }
}
