package com.ameliant.training.day2;

import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class OnExceptionHandledRouteTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:out")
    MockEndpoint mockOut;

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new OnExceptionHandledRoute();
    }

    @Ignore
    @Test
    public void testRoute_handled() throws InterruptedException {
        mockOut.setExpectedMessageCount(0);
        String response =
                template.requestBody("direct:in", "Rubbish", String.class);

        Assert.assertEquals("Oops", response);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRoute_continued() throws InterruptedException {
        mockOut.setExpectedMessageCount(1);
        String response =
                template.requestBody("direct:in", "Rubbish", String.class);

        Assert.assertEquals("Oops", response);
        assertMockEndpointsSatisfied();
    }
}
