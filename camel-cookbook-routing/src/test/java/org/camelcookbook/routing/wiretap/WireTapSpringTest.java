package org.camelcookbook.routing.wiretap;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class WireTapSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("spring/wireTap-context.xml");
    }

    @Produce(uri = "direct:in")
    protected ProducerTemplate template;

    @EndpointInject(uri = "mock:tapped")
    private MockEndpoint tapped;

    @EndpointInject(uri = "mock:out")
    private MockEndpoint out;

    @Test
    public void testMessageRoutedToWireTapEndpoint() throws InterruptedException {
        String messageBody = "Message to be tapped";
        tapped.setExpectedMessageCount(1);
        out.setExpectedMessageCount(1);

        template.sendBody(messageBody);

        // check that the endpoints both received the same message
        tapped.assertIsSatisfied();
        out.assertIsSatisfied();

        tapped.expectedBodyReceived().equals(messageBody);
        out.expectedBodyReceived().equals(messageBody);
    }

}
