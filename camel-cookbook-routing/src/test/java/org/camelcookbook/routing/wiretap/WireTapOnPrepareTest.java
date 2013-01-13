package org.camelcookbook.routing.wiretap;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class WireTapOnPrepareTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new WireTapOnPrepareRouteBuilder();
    }

    @Produce(uri = "direct:in")
    protected ProducerTemplate template;

    @EndpointInject(uri = "mock:tapped")
    private MockEndpoint tapped;

    @EndpointInject(uri = "mock:out")
    private MockEndpoint out;

    @Test
    public void testMessageRoutedToWireTapMarked() throws InterruptedException {
        String messageBody = "Message to be tapped";
        tapped.setExpectedMessageCount(1);
        tapped.message(0).body().isEqualTo(messageBody);

        out.setExpectedMessageCount(1);
        out.message(0).body().isEqualTo(messageBody);

        // TODO investigate - this is the inverse of what I would have expected
        tapped.message(0).header("processorAction").isNull();
        out.message(0).header("processorAction").isNotNull();

        template.sendBody(messageBody);

        // check that the endpoints both received the same message
        tapped.assertIsSatisfied();
        out.assertIsSatisfied();
    }
}
