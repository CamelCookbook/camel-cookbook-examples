package com.ameliant.training.day2;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.Arrays;

public class AggregateRouteTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:out")
    MockEndpoint mockOut;

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new AggregateRoute();
    }

    @Test
    public void testRoute() throws InterruptedException {
        mockOut.setExpectedMessageCount(1);
        mockOut.expectedBodiesReceived("1,2,3,4,5");

        template.sendBody("direct:in", "1");
        template.sendBody("direct:in", "2");
        template.sendBody("direct:in", "3");
        template.sendBody("direct:in", "4");
        template.sendBody("direct:in", "5");

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRoute_timeout() throws InterruptedException {
        mockOut.setExpectedMessageCount(1);
        mockOut.expectedBodiesReceived("1,2,3,4");

        template.sendBody("direct:in", "1");
        template.sendBody("direct:in", "2");
        template.sendBody("direct:in", "3");
        template.sendBody("direct:in", "4");

        assertMockEndpointsSatisfied();
    }
}
