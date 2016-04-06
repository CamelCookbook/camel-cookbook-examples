package com.ameliant.training.day2;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import java.util.function.Predicate;

public class ErrorHandlerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:in").routeId("ErrorHandlerRoute")
            .errorHandler(defaultErrorHandler()
                    .maximumRedeliveries(2)
                    .redeliveryDelay(1000))
            //.throwException(new IllegalStateException("boom"));
            .log("Received ${body}")
            .process((Exchange exchange) -> {
                throw new IllegalStateException("Boom! " + exchange.getIn().getBody(String.class));
            });

    }


}
