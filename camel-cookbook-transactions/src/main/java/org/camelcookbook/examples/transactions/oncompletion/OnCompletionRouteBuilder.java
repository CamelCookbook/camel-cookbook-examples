package org.camelcookbook.examples.transactions.oncompletion;

import org.apache.camel.builder.RouteBuilder;

/**
 * Route that demonstrates the use of
 */
public class OnCompletionRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        onCompletion()
            .log("global onCompletion thread: ${threadName}")
            .to("mock:global");

        from("direct:noOnCompletion")
            .log("Original thread: ${threadName}")
            .choice()
                .when(simple("${body} contains 'explode'"))
                    .throwException(new IllegalArgumentException("Exchange caused explosion"))
            .endChoice();

        from("direct:onCompletion")
            .onCompletion().onFailureOnly()
                .log("onFailureOnly thread: ${threadName}")
                .to("mock:failed")
            .end()
            .log("Original thread: ${threadName}")
            .choice()
                .when(simple("${body} contains 'explode'"))
                    .throwException(new IllegalArgumentException("Exchange caused explosion"))
            .endChoice();

        from("direct:chained")
            .onCompletion().onCompleteOnly()
                .log("onCompleteOnly thread: ${threadName}")
                .to("mock:completed")
            .end()
            .to("direct:onCompletion"); // calls out to route with onCompletion set
    }

}
