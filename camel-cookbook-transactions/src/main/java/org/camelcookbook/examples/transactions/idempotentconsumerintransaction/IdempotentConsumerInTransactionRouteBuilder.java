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
            .transacted("PROPAGATION_REQUIRED")
            .setHeader("message", body())
            .to("sql:insert into audit_log (message) values (:#message)")
            .enrich("direct:invokeWs")
            .to("mock:out");

        from("direct:invokeWs")
            .idempotentConsumer(header("messageId"), idempotentRepository)
                .to("mock:ws")
            .end();
    }
}
