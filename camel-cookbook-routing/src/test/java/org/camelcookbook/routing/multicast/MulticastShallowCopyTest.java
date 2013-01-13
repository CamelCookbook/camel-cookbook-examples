package org.camelcookbook.routing.multicast;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class MulticastShallowCopyTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new MulticastShallowCopyRouteBuilder();
    }

    @Produce(uri = "direct:in")
    protected ProducerTemplate template;

    @EndpointInject(uri = "mock:first")
    private MockEndpoint first;

    @EndpointInject(uri = "mock:second")
    private MockEndpoint second;

    @EndpointInject(uri = "mock:afterMulticast")
    private MockEndpoint afterMulticast;

    @Test
    public void testMessageRoutedToMulticastEndpoints() throws InterruptedException {
        String messageBody = "Message to be multicast";
        first.setExpectedMessageCount(1);
        first.message(0).equals(messageBody);
        first.message(0).header("modifiedBy").equals("first");
        second.setExpectedMessageCount(1);
        second.message(0).equals(messageBody);
        second.message(0).header("modifiedBy").equals("second");

        afterMulticast.setExpectedMessageCount(1);
        afterMulticast.message(0).equals(messageBody);
        afterMulticast.message(0).header("modifiedBy").isNull();

        template.sendBody(messageBody);

        assertMockEndpointsSatisfied();
        String firstThreadName = first.getExchanges().get(0).getIn().getHeader("threadName", String.class);
        String secondThreadName = second.getExchanges().get(0).getIn().getHeader("threadName", String.class);
        assertEquals(firstThreadName, secondThreadName);
    }

}
