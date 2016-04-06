package com.ameliant.training.day2;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.Synchronization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnCompletionRoute extends RouteBuilder {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void configure() throws Exception {
        from("direct:in").routeId("OnCompletionRoute")
            .onException(IllegalStateException.class)
                //.maximumRedeliveries(3).redeliveryDelay(1000)
                .log("Kept failing! ${exception.message}")
            .end()
            .onCompletion().onCompleteOnly()
                .log("All done!")
            .end()
            .onCompletion().onFailureOnly()
                .log("Cleaning up...")
            .end()
            .log("Received ${body}")
            .process((Exchange exchange) -> {
                // dynamic
                exchange.addOnCompletion(new Synchronization() {
                    @Override
                    public void onComplete(Exchange exchange) {
                        log.info("onComplete");
                    }

                    @Override
                    public void onFailure(Exchange exchange) {
                        log.warn("onFailure");
                    }
                });
            })
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
