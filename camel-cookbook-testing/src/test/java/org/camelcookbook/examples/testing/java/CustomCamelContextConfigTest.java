package org.camelcookbook.examples.testing.java;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Test;

/**
 * @author jkorab
 */
public class CustomCamelContextConfigTest extends CamelTestSupport {
    
    @Override
    public CamelContext createCamelContext() {
        CamelContext context = new DefaultCamelContext();
        ActiveMQComponent activeMQComponent = new ActiveMQComponent();
        activeMQComponent.setBrokerURL("vm:localhost?broker.persistent=false");
        context.addComponent("activemq", activeMQComponent);
        return context;
    }

    @Override
    public RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:in")
                    .to("activemq:orders");

                from("activemq:orders")
                    .to("mock:out");
            }
        };
    }

    @Test
    public void testMessagesFlowOverQueue() throws InterruptedException {
        MockEndpoint out = getMandatoryEndpoint("mock:out", MockEndpoint.class);
        out.setExpectedMessageCount(1);

        template.sendBody("direct:in", "hello");

        assertMockEndpointsSatisfied();
        out.expectedBodiesReceived("hello");
    }

}
