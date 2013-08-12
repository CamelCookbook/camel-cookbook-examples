package org.camelcookbook.examples.transactions.transactionpolicy;

import org.apache.camel.builder.RouteBuilder;

/**
 * Demonstrates the use of policies to scope transactions
 */
public class TransactionPolicyRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:policies")
            .setHeader("message", body())
            .policy("PROPAGATION_REQUIRED")
                .to("sql:insert into audit_log (message) values (:#message)")
                .to("mock:out1")
            .end()
            .policy("PROPAGATION_REQUIRED")
                .to("sql:insert into messages (message) values (:#message)")
                .to("mock:out2")
            .end();
    }
}
