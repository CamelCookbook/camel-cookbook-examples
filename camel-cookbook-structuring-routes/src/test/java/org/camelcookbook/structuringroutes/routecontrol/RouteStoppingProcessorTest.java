package org.camelcookbook.structuringroutes.routecontrol;

import org.apache.camel.ServiceStatus;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Demonstrates the manual shutting down of a route.
 */
public class RouteStoppingProcessorTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:in").id("mainRoute")
                    .log("Stopping route")
                    .process(new RouteStoppingProcessor())
                    .log("Signalled to stop route")
                    .to("mock:out");
            }
        };
    }

    @Test
    public void testRouteShutdown() throws InterruptedException {
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.setExpectedMessageCount(1);

        template.sendBody("direct:in", "mainRoute");

        assertMockEndpointsSatisfied();
        Thread.sleep(100);
        ServiceStatus mainRouteStatus = context.getRouteStatus("mainRoute");
        assertTrue(mainRouteStatus.isStopped());
    }
}
