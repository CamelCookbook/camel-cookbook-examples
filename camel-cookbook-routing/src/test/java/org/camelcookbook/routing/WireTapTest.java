package org.camelcookbook.routing;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class WireTapTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new WireTapRouteBuilder();
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
