package com.ameliant.training.day2;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class WireTapRouteTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:out")
    MockEndpoint mockOut;

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new WireTapRoute();
    }

    @Test
    public void testRoute() throws InterruptedException {
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("Oslo");

        template.sendBody("direct:in", "Oslo");

        assertMockEndpointsSatisfied();
    }

}
