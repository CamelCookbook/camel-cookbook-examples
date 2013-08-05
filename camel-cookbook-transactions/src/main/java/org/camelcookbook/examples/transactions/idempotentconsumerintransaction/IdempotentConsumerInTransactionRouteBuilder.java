package org.camelcookbook.examples.transactions.idempotentconsumerintransaction;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.IdempotentRepository;

/**
 * @author jkorab
 */
public class IdempotentConsumerInTransactionRouteBuilder extends RouteBuilder {

    private final IdempotentRepository idempotentRepository;

    public IdempotentConsumerInTransactionRouteBuilder(IdempotentRepository idempotentRepository) {
        this.idempotentRepository = idempotentRepository;
    }

    @Override
    public void configure() throws Exception {
        from("direct:transacted")
            .transacted()
            .log("Processing message: ${body}")
            .setHeader("message", body())
            .to("sql:insert into audit_log (message) values (:#message)")
            .enrich("direct:invokeWs")
            .choice()
                .when(simple("${header[message]} contains 'explode'"))
                    .throwException(new IllegalArgumentException("Exchange caused explosion"))
            .endChoice()
            .to("mock:out");

        from("direct:invokeWs")
            .log("Received message ${header[messageId]}")
            .idempotentConsumer(header("messageId"), idempotentRepository)
                .log("Invoking WS")
                .to("mock:ws")
            .end()
            .log("Completing")
            .to("mock:calledWs");
    }
}
