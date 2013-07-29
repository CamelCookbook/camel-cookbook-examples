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

        from("direct:onCompletion")
            .onCompletion()
                .log("onCompletion triggered: ${threadName}")
                .to("mock:completed")
            .end()
            .log("Processing message: ${threadName}");

        from("direct:noOnCompletion")
            .log("Original thread: ${threadName}")
            .choice()
                .when(simple("${body} contains 'explode'"))
                    .throwException(new IllegalArgumentException("Exchange caused explosion"))
            .endChoice();

        from("direct:onCompletionFailure")
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
            .log("chained")
            .onCompletion().onCompleteOnly()
                .log("onCompleteOnly thread: ${threadName}")
                .to("mock:completed")
            .end()
            .to("direct:onCompletionFailure"); // calls out to route with onCompletion set


        from("direct:onCompletionChoice")
            .onCompletion()
                .to("direct:processCompletion")
            .end()
            .log("Original thread: ${threadName}")
            .choice()
                .when(simple("${body} contains 'explode'"))
                .throwException(new IllegalArgumentException("Exchange caused explosion"))
            .endChoice();

        from("direct:processCompletion")
            .log("onCompletion thread: ${threadName}")
            .choice()
                .when(simple("${exception} == null"))
                    .to("mock:completed")
                .otherwise()
                    .to("mock:failed")
            .endChoice();

    }

}
