package org.camelcookbook.examples.transactions.idempotentconsumer;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;

/**
* @author jkorab
*/
class IdempotentConsumerRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:in")
            .log("Received message ${header[messageId]}")
            .idempotentConsumer(header("messageId"), new MemoryIdempotentRepository())
                .log("Invoking WS")
                .to("mock:ws")
            .end()
            .log("Completing")
            .to("mock:out");
    }
}
