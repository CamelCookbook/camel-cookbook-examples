package org.camelcookbook.examples.transactions.idempotentconsumer;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;

/**
* @author jkorab
*/
class IdempotentConsumerSkipDuplicateRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:in")
            .log("Received message ${header[messageId]}")
            .idempotentConsumer(header("messageId"), new MemoryIdempotentRepository()).skipDuplicate(false)
                .choice()
                    .when(property(Exchange.DUPLICATE_MESSAGE))
                        .log("Duplicate")
                        .to("mock:duplicate")
                    .otherwise()
                        .to("mock:ws")
                .endChoice()
            .end()
            .log("Completing")
            .to("mock:out");
    }
}
