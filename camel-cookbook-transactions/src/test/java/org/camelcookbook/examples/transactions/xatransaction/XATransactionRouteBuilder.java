package org.camelcookbook.examples.transactions.xatransaction;

import org.apache.camel.builder.RouteBuilder;

/**
 * Demonstrates the use of an XA transaction manager with a JMS component and database.
 */
public class XATransactionRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {
        from("jms:inbound?transacted=true")
            .transacted("PROPAGATION_REQUIRED")
            .log("Processing message: ${body}")
            .setHeader("message", body())
            .to("sql:insert into audit_log (message) values (:#message)")
            .to("jms:outbound") // this send is transacted, so the message should not be sent
            .to("mock:out");
    }
}
