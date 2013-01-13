package org.camelcookbook.routing.multicast;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

public class MulticastSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("spring/multicast-context.xml");
    }

    @Produce(uri = "direct:in")
    protected ProducerTemplate template;

    @EndpointInject(uri = "mock:first")
    private MockEndpoint first;

    @EndpointInject(uri = "mock:second")
    private MockEndpoint second;

    @EndpointInject(uri = "mock:third")
    private MockEndpoint third;

    @Test
    public void testMessageRoutedToMulticastEndpoints() throws InterruptedException {
        String messageBody = "Message to be multicast";
        first.setExpectedMessageCount(1);
        first.message(0).equals(messageBody);
        second.setExpectedMessageCount(1);
        second.message(0).equals(messageBody);
        third.setExpectedMessageCount(1);
        third.message(0).equals(messageBody);

        template.sendBody(messageBody);

        assertMockEndpointsSatisfied(5, TimeUnit.SECONDS);
    }

}
