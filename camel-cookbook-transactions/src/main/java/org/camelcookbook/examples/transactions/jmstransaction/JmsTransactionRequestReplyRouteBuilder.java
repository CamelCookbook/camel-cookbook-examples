package org.camelcookbook.examples.transactions.jmstransaction;

import org.apache.camel.builder.RouteBuilder;

/**
 * Demonstrates the correct use of transactions with JMS when you need to perform a request-reply.
 */
public class JmsTransactionRequestReplyRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("jms:inbound").startupOrder(3)
            .transacted("PROPAGATION_REQUIRED")
            .log("Processing message ${body}")
            .to("jms:auditQueue") // this send is transacted
            .inOut("direct:invokeBackendService")
            .to("mock:out");

        from("direct:invokeBackendService").startupOrder(2)
            .transacted("PROPAGATION_NOT_SUPPORTED")
            .to("jms:backendService"); // this send is not

        // fake back-end service that processes requests
        from("jms:backendService").startupOrder(1)
            .transform(simple("Backend processed: ${body}"))
            .to("mock:backEndReply");
    }
}
