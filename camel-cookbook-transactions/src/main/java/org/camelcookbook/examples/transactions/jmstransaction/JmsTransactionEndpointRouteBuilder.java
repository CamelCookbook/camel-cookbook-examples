package org.camelcookbook.examples.transactions.jmstransaction;

import org.apache.camel.builder.RouteBuilder;

/**
 * Demonstrates the use of local transactions initiated via the transacted attribute on a consumer endpoint.
 */
public class JmsTransactionEndpointRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("jms:inbound?transacted=true")
            .log("Processing message: ${body}")
            // this send is transacted, so the message will not be sent until processing has completed
            .to("jms:outbound")
            .to("mock:out");
    }
}
