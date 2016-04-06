package com.ameliant.training.day1;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class TemplateRouteAOPTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:out")
    MockEndpoint mockOut;

    @Override
    protected RoutesBuilder[] createRouteBuilders() throws Exception {
        TemplateRoute route1 = new TemplateRoute();
        route1.setStartUri("direct:in");
        route1.setEndUri("direct:mid");
        route1.setOffset(100);
        route1.setPrefix("route1");

        TemplateRoute route2 = new TemplateRoute();
        route2.setStartUri("direct:mid");
        route2.setEndUri("mock:out");
        route2.setOffset(0);
        route2.setPrefix("route2");

        return new RouteBuilder[] {
                route1,
                route2};
    }

    @Override
    public boolean isUseAdviceWith() {
        return true; // need to start context manually
    }

    @Test
    public void testRoute() throws Exception {
        context.getRouteDefinition("route1.transform").adviceWith(
                context, new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveById("route1.greeting").after()
                                .to("mock:afterFirstGreeting");
                    }
                }
        );
        context.start();

        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("Hello Hello Oslo");

        MockEndpoint mockAfterFirst = getMockEndpoint("mock:afterFirstGreeting");
        mockAfterFirst.message(0).body().isEqualTo("Hello Oslo");

        template.sendBody("direct:in", "Oslo");

        assertMockEndpointsSatisfied();
    }

}
