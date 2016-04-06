package com.ameliant.training.day1;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SimpleRouteWithSupportTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:out")
    MockEndpoint mockOut;

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new SimpleRoute();
    }

    @Test
    public void testRoute() throws InterruptedException {
        //MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("Hello Oslo");

        template.sendBody("direct:in", "Oslo");

        assertMockEndpointsSatisfied();
    }

}
