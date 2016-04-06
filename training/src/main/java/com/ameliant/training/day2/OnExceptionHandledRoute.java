package com.ameliant.training.day2;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class OnExceptionHandledRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // global
        onException(IllegalStateException.class)
            .maximumRedeliveries(3).redeliveryDelay(1000)
            .log("Kept failing! ${exception.message}")
            .transform(constant("Oops"))
            //.handled(true)
            .continued(true)
        .end();

        from("direct:in").routeId("OnExceptionRoute")
            .log("Received ${body}")
            .to("direct:callExternal")
            .to("mock:out");

        from("direct:callExternal")
            .process((Exchange exchange) -> {
                throw new IllegalStateException(
                        "Boom! " + exchange.getIn().getBody(String.class));
            });

    }


}
