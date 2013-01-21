package org.camelcookbook.routing.multicast;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class MulticastStopOnExceptionTest extends CamelTestSupport {

    public static final String MESSAGE_BODY = "Message to be multicast";

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new MulticastStopOnExceptionRouteBuilder();
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
        mockFirst.setExpectedMessageCount(1);
        mockFirst.message(0).equals(MESSAGE_BODY);

        mockSecond.setExpectedMessageCount(0);

        afterMulticast.setExpectedMessageCount(1);
        afterMulticast.message(0).equals(MESSAGE_BODY);

        exceptionHandler.setExpectedMessageCount(1);

        template.sendBody(MESSAGE_BODY);

        assertMockEndpointsSatisfied();
    }

}
