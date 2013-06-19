package org.camelcookbook.parallelprocessing.endpointconsumers;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Test class that exercises parallel threads in Seda.
 * @author jkorab
 */
public class EndpointConsumersSedaSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/META-INF/spring/endpointConsumersSeda-context.xml");
    }

    @Test
    public void testParallelConsumption() throws InterruptedException {
        final int messageCount = 100;
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.setExpectedMessageCount(messageCount);
        mockOut.setResultWaitTime(5000);

        for (int i = 0; i < messageCount; i++) {
            template.sendBody("seda:in", "Message[" + i + "]");
        }

        assertMockEndpointsSatisfied();
    }

}
