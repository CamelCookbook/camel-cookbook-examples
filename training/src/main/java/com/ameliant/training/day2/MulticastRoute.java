package com.ameliant.training.day2;

import org.apache.camel.builder.RouteBuilder;

public class MulticastRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:in").routeId("MulticastRoute")
                .startupOrder(40)
            .log("Before: ${exchangeId}")
            .multicast().parallelProcessing()
                .to("direct:one")
                .to("direct:two")
                .to("direct:three")
            .end()
            .log("After: ${exchangeId}")
            .to("mock:out");

        from("direct:one").startupOrder(30)
            .transform(simple("One: ${body}"))
            .log("${body} [${exchangeId}]");

        from("direct:two").startupOrder(20)
            .transform(simple("Two: ${body}"))
            .log("${body} [${exchangeId}]");

        from("direct:three").startupOrder(10)
            .transform(simple("Three: ${body}"))
            .log("${body} [${exchangeId}]");
    }

}
