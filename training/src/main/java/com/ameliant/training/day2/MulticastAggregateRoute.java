package com.ameliant.training.day2;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import java.util.Random;

public class MulticastAggregateRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:in").routeId("MulticastRoute")
                .startupOrder(40)
            .log("Before: ${exchangeId}")
            .enrich("direct:doMulticast",
                    (Exchange _old, Exchange _new) -> {return _old;}
            )
            .to("mock:out");

        from("direct:doMulticast").startupOrder(35)
            .multicast().aggregationStrategy(new ConcatAggregationStrategy())
                    .parallelProcessing()
                .to("direct:one")
                .to("direct:two")
                .to("direct:three")
            .end()
            .log("After: ${exchangeId}");

        from("direct:one").startupOrder(30)
            .transform(simple("One: ${body}"))
            .to("direct:delay")
            .log("${body} [${exchangeId}]");

        from("direct:two").startupOrder(20)
            .transform(simple("Two: ${body}"))
            .to("direct:delay")
            .log("${body} [${exchangeId}]");

        from("direct:three").startupOrder(10)
            .transform(simple("Three: ${body}"))
            .to("direct:delay")
            .log("${body} [${exchangeId}]");

        from("direct:delay").startupOrder(5)
            .process((Exchange exchange) -> { // Processor
                int randomDelay = new Random().nextInt(1000);
                exchange.getIn().setHeader("delayBy", randomDelay);
            })
            .delay(header("delayBy"));
    }

}
