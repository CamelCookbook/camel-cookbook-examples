package com.ameliant.training.day1;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class RecipientListRouteTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:out")
    MockEndpoint mockOut;

    @EndpointInject(uri = "mock:swedish")
    MockEndpoint mockSwedish;

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RecipientListRoute();
    }

    @Test
    public void testRoute() throws InterruptedException {
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("Hello Oslo!");

        template.sendBody("direct:in", "Oslo");

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRoute_swedish() throws InterruptedException {
        mockSwedish.setExpectedMessageCount(1);
        mockSwedish.message(0).body().isEqualTo("Hello Stockholm (Bork bork bork)");

        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("Hello Hello Stockholm (Bork bork bork)");

        template.sendBodyAndHeader("direct:in", "Stockholm", "locale", "se_SE");

        assertMockEndpointsSatisfied();
    }
}
