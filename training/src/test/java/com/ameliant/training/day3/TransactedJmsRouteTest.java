package com.ameliant.training.day3;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.joda.time.DateTime;
import org.junit.Test;

public class TransactedJmsRouteTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:out")
    MockEndpoint mockOut;

    @EndpointInject(uri = "mock:dlq")
    MockEndpoint mockDlq;

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext context = new DefaultCamelContext();

        ActiveMQComponent activeMQComponent = new ActiveMQComponent();
        activeMQComponent.setBrokerURL(
                "vm://myEmbeddedBroker?broker.persistent=false");

        context.addComponent("jms", activeMQComponent);
        return context;
    }

    @Override
    protected RoutesBuilder[] createRouteBuilders() throws Exception {
        return new RouteBuilder[] {
            new TransactedJmsRoute(), // the route under test
            new RouteBuilder() { // test harness
                @Override
                public void configure() throws Exception {
                    from("direct:in").routeId("harness.in")
                        .to("jms:in");

                    from("jms:out").routeId("harness.out")
                        .to("mock:out");

                    from("jms:ActiveMQ.DLQ").routeId("harness.dlq")
                            .to("mock:dlq");
                }
            }
        };
    }

    @Test
    public void testRoute() throws InterruptedException {
        mockOut.setExpectedMessageCount(1);
        mockOut.expectedBodiesReceived("Hello Oslo");

        template.sendBody("direct:in", "Oslo");

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testRoute_exception() throws InterruptedException {
        mockDlq.setExpectedMessageCount(1);
        mockDlq.expectedBodiesReceived("Boom");

        template.sendBody("direct:in", "Boom");

        assertMockEndpointsSatisfied();
    }
}
