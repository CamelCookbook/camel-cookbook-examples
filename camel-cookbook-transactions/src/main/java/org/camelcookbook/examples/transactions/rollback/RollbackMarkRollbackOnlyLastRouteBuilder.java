package org.camelcookbook.examples.transactions.rollback;

import org.apache.camel.builder.RouteBuilder;

/**
 * Demonstrates the behavior of marking the last transaction for rollback.
 */
public class RollbackMarkRollbackOnlyLastRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:route1").id("route1")
            .setHeader("message", simple("${body}"))
            .policy("PROPAGATION_REQUIRES_NEW").id("tx1")
                .to("sql:insert into messages (message) values (:#message)")
                .to("direct:route2")
                .to("mock:out1")
            .end();

        from("direct:route2").id("route2")
            .policy("PROPAGATION_REQUIRES_NEW-2").id("tx2")
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
