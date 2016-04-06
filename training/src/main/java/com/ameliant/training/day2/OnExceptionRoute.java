package com.ameliant.training.day2;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class OnExceptionRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:in").routeId("OnExceptionRoute")
            .onException(IllegalStateException.class)
                .maximumRedeliveries(3).redeliveryDelay(1000)
                .log("Kept failing! ${exception.message}")
            .end()
            .log("Received ${body}")
            .process((Exchange exchange) -> {
                Integer redeliveryCounter =
                        exchange.getIn().getHeader(Exchange.REDELIVERY_COUNTER, Integer.class);
                if (redeliveryCounter == null || redeliveryCounter < 2) {
                    throw new IllegalStateException(
                            "Boom! " + exchange.getIn().getBody(String.class));
                }
            })
            .to("mock:out");

    }


}
