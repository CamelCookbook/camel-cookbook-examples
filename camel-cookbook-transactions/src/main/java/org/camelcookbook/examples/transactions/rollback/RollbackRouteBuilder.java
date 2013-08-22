package org.camelcookbook.examples.transactions.rollback;

import org.apache.camel.builder.RouteBuilder;

/**
 * Demostrates the use of the rollback statement to throw an Exception, that will roll back the transaction
 */
public class RollbackRouteBuilder extends RouteBuilder {
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
                    .rollback()
                .otherwise()
                    .log("Message processed successfully")
            .to("mock:out");

    }
}
