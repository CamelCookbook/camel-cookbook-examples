package com.ameliant.training.day2;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class OnExceptionRouteTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:out")
    MockEndpoint mockOut;

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new OnExceptionRoute();
    }

    @Test
    public void testRoute() throws InterruptedException {
        mockOut.setExpectedMessageCount(1);
        template.sendBody("direct:in", "Rubbish");
        assertMockEndpointsSatisfied();
    }

}
