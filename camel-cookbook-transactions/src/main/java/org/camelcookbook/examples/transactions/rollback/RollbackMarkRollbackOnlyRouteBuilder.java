package org.camelcookbook.examples.transactions.rollback;

import org.apache.camel.builder.RouteBuilder;

/**
 * Demostrates the use of the markRollbackOnly statement roll back the transaction without throwing a transaction.
 */
public class RollbackMarkRollbackOnlyRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:transacted")
            .transacted()
            .log("Processing message: ${body}")
            .setHeader("message", body())
            .to("sql:insert into audit_log (message) values (:#message)")
            .choice()
                .when(simple("${body} contains 'explode'"))
                    .log("Message cannot be processed further - rolling back insert")
                    .markRollbackOnly()
                .otherwise()
                    .log("Message processed successfully")
            .endChoice()
            .to("mock:out");

    }
}
