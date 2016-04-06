package com.ameliant.training.day2;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class SplitAggregateRouteTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:out")
    MockEndpoint mockOut;

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new SplitAggregateRoute();
    }

    @Test
    public void testRoute() throws InterruptedException {
        mockOut.setExpectedMessageCount(4);
        mockOut.expectedBodiesReceivedInAnyOrder(
                "1,9,23,7,19", "31,17",
                "4,16,12,24,34", "38,6,14,8");

        template.sendBody("direct:in", "1,4,9,23,16,12");
        template.sendBody("direct:in", "24,7,19,31,34");
        template.sendBody("direct:in", "38,6,14,17,8");

        assertMockEndpointsSatisfied();
    }

}
