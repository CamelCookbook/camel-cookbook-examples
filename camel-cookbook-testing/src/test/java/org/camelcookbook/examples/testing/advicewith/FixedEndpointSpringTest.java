package org.camelcookbook.examples.testing.advicewith;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Test class that uses
 */
public class FixedEndpointSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/META-INF/spring/fixedEndpoints-context.xml");
    }

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Test
    public void testOverriddenEndpoints() throws Exception {
        context.getRouteDefinition("modifyPayloadBetweenQueues")
                .adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                replaceFromWith("direct:in");

                interceptSendToEndpoint("activemq:out")
                        .skipSendToOriginalEndpoint()
                        .to("mock:out");
            }
        });
        context.start();

        MockEndpoint out = getMockEndpoint("mock:out");

        out.setExpectedMessageCount(1);
        out.message(0).body().isEqualTo("Modified: Cheese");

        template.sendBody("direct:in", "Cheese");

        out.assertIsSatisfied();
    }
}
