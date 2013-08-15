package org.camelcookbook.examples.transactions.jmstransaction;

import org.apache.camel.builder.RouteBuilder;

/**
 * RouteBuilder that demonstrates JMS transactions using the transacted DSL method.
 */
public class JmsTransactionRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("jms:inbound")
            .transacted()
            .log("Processing message ${body}")
            .to("jms:outbound")
            .to("mock:out");
    }
}
