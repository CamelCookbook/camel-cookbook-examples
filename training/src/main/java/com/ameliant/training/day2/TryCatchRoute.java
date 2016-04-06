package com.ameliant.training.day2;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class TryCatchRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:in").routeId("TryCatchRoute")
            .log("Received ${body}")
            .doTry()
                .process((Exchange exchange) -> {
                    throw new IllegalStateException("Boom! " + exchange.getIn().getBody(String.class));
                })
            .doCatch(IllegalStateException.class)
                .log("ISE: ${exception.message}")
            .end()
            .to("mock:out");

    }


}
