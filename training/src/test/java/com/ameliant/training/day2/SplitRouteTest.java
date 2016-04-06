package com.ameliant.training.day2;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class SplitRouteTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:out")
    MockEndpoint mockOut;

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new SplitRoute();
    }

    @Test
    public void testRoute() throws InterruptedException {
        mockOut.setExpectedMessageCount(3);
        mockOut.message(0).body().isEqualTo("one");
        mockOut.message(1).body().isEqualTo("two");
        mockOut.message(2).body().isEqualTo("three");

        template.sendBody("direct:in", "one,two,three");

        assertMockEndpointsSatisfied();
    }

}
