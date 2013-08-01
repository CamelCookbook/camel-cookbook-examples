package org.camelcookbook.examples.transactions.databasetransaction;

import org.apache.camel.builder.RouteBuilder;

/**
 * @author jkorab
 */
public class DatabaseTransactionRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:transacted")
            .transacted()
            .log("Processing message: ${body}")
            .setHeader("message", body())
            .to("sql:insert into audit_log (message) values (:#message)")
            .choice()
                .when(simple("${header[message]} contains 'explode'"))
                    .throwException(new IllegalArgumentException("Exchange caused explosion"))
            .endChoice()
            .to("mock:out");
    }
}
