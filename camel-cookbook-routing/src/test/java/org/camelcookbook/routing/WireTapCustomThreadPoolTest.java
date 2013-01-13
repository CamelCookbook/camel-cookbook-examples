package org.camelcookbook.routing;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class WireTapCustomThreadPoolTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new WireTapCustomThreadPoolRouteBuilder();
    }

    @Produce(uri = "direct:in")
    protected ProducerTemplate template;

    @EndpointInject(uri = "mock:tapped")
    private MockEndpoint tapped;

    @EndpointInject(uri = "mock:out")
    private MockEndpoint out;

    @Test
    public void testMessageWireTappedInOrderBySameThread() throws InterruptedException {
        String messageBody = "Message to be tapped";
        final int messagesToSend = 3;
        tapped.setExpectedMessageCount(messagesToSend);
        tapped.expectsAscending().header("messageCount");
        out.setExpectedMessageCount(messagesToSend);
        out.expectsAscending().header("messageCount");

        for (int messageCount = 0; messageCount < messagesToSend; messageCount++) {
            template.sendBodyAndHeader(messageBody, "messageCount", messageCount);
        }

        // check that the endpoints both received the same message
        tapped.assertIsSatisfied();
        out.assertIsSatisfied();

        List<Exchange> exchanges = tapped.getExchanges();
        String firstExchangeThreadName = null;
        for (Exchange exchange : exchanges) {
            Message in = exchange.getIn();
            if (firstExchangeThreadName == null) {
                firstExchangeThreadName = in.getHeader("threadName", String.class);
            }
            Assert.assertEquals(firstExchangeThreadName, in.getHeader("threadName", String.class));
        }
    }


}
