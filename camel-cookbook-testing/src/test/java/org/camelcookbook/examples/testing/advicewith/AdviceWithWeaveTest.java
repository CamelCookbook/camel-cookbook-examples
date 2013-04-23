package org.camelcookbook.examples.testing.advicewith;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * @author jkorab
 */
public class AdviceWithWeaveTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:in").id("slowRoute")
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                Thread.sleep(10000);
                                Message in = exchange.getIn();
                                in.setBody("Slow reply to: " + in.getBody());
                            }
                        }).id("reallySlowProcessor")
                        .to("mock:out");
            }
        };
    }

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Test
    public void testSubstitutionOfSlowProcessor() throws Exception {
        context.getRouteDefinition("slowRoute")
                .adviceWith(context, new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        weaveById("reallySlowProcessor").replace()
                                .transform().simple("Fast reply to: ${body}");
                    }
                });
        context.start();

        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.message(0).body().equals("Fast reply to: request");

        ProducerTemplate in = context.createProducerTemplate();
        in.sendBody("direct:in", "request");

        assertMockEndpointsSatisfied();
    }
}
