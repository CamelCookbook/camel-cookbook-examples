package org.camelcookbook.examples.transactions.transactionpolicy;

import org.apache.camel.builder.RouteBuilder;

/**
 * Demonstrates the use of nested transaction policies.
 */
public class TransactionPolicyNestedRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:policies")
            .setHeader("message", simple("${body}"))
            .policy("PROPAGATION_REQUIRED")
                .to("sql:insert into audit_log (message) values (:#message)")
                .to("mock:out1")
                .to("direct:nestedPolicy")
            .end();

        from("direct:nestedPolicy")
            .policy("PROPAGATION_NOT_SUPPORTED")
                .to("sql:insert into messages (message) values (:#message)")
                .to("mock:out2")
            .end();

    }
}
