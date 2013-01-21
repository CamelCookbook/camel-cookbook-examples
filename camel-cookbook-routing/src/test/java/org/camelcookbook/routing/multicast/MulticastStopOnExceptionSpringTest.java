package org.camelcookbook.routing.multicast;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MulticastStopOnExceptionSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("spring/multicast-stopOnException-context.xml");
    }

    @Produce(uri = "direct:in")
    protected ProducerTemplate template;

    @EndpointInject(uri = "mock:first")
    private MockEndpoint mockFirst;

    @EndpointInject(uri = "mock:second")
    private MockEndpoint mockSecond;

    @EndpointInject(uri = "mock:afterMulticast")
    private MockEndpoint afterMulticast;

    @EndpointInject(uri = "mock:exceptionHandler")
    private MockEndpoint exceptionHandler;

    @Test
    public void testMessageRoutedToMulticastEndpoints() throws InterruptedException {
        String messageBody = "Message to be multicast";

        mockFirst.setExpectedMessageCount(1);
        mockFirst.message(0).equals(messageBody);

        mockSecond.setExpectedMessageCount(0);

        afterMulticast.setExpectedMessageCount(0);

        exceptionHandler.setExpectedMessageCount(1);

        template.sendBody(messageBody);

        assertMockEndpointsSatisfied();
    }

}
