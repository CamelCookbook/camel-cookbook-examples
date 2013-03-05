package org.camelcookbook.structuringroutes.direct;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class DirectLoopTest extends CamelTestSupport {
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:in")
                    .setHeader("loopCount").constant(0)
                    .to("direct:loop")
                    .to("mock:out");

                from("direct:loop")
                    .log("Loop: ${header[loopCount]}")
                    .choice()
                        .when(simple("${header[loopCount]} < 10"))
                            .process(new Processor() {
                                @Override
                                public void process(Exchange exchange) throws Exception {
                                    Message in = exchange.getIn();
                                    in.setHeader("loopCount", in.getHeader("loopCount", Integer.class) + 1);
                                }
                            })
                            .to("direct:loop")
                        .otherwise()
                            .log("Exiting loop")
                    .end();
            }
        };
    }

    @Produce(uri = "direct:in")
    ProducerTemplate in;

    @EndpointInject(uri = "mock:out")
    MockEndpoint out;


    @Test
    public void testInOutMessage() throws Exception {
        String message = "hello";

        out.setExpectedMessageCount(1);
        out.message(0).header("loopCount").isEqualTo(10);

        in.sendBody(message);
        assertMockEndpointsSatisfied();
    }

}
