package org.camelcookbook.examples.transactions.rollback;

import org.apache.camel.builder.RouteBuilder;

/**
 * Demonstrates the behavior of marking the last transaction for rollback.
 */
public class RollbackMarkRollbackOnlyLastRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:policies")
            .setHeader("message", simple("${body}"))
            .policy("PROPAGATION_REQUIRES_NEW")
                .to("sql:insert into messages (message) values (:#message)")
                .to("direct:nestedPolicy")
                .to("mock:out1")
            .end();

        from("direct:nestedPolicy")
            .policy("PROPAGATION_REQUIRES_NEW-2")
                .to("sql:insert into audit_log (message) values (:#message)")
                .choice()
                    .when(simple("${body} contains 'explode'"))
                        .log("Message cannot be processed further - rolling back insert")
                        .markRollbackOnlyLast()
                    .otherwise()
                        .log("Message processed successfully")
                .endChoice()
                .to("mock:out2")
            .end();
    }
}
