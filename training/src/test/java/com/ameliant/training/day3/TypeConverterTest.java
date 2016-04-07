package com.ameliant.training.day3;

import com.ameliant.training.day2.SplitRoute;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.joda.time.DateTime;
import org.junit.Test;

public class TypeConverterTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:out")
    MockEndpoint mockOut;

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:in")
                    .convertBodyTo(DateTime.class)
                    .to("mock:out");
            }
        };
    }

    @Test
    public void testRoute() throws InterruptedException {
        mockOut.setExpectedMessageCount(1);
        mockOut.expectedBodiesReceived(
                new DateTime(2016, 04, 07, 0, 0));

        template.sendBody("direct:in", "20160407");

        assertMockEndpointsSatisfied();
    }

}
