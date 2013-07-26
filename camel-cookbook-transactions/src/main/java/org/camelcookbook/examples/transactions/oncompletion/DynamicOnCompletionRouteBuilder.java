package org.camelcookbook.examples.transactions.oncompletion;

import org.apache.camel.builder.RouteBuilder;

/**
 * Route that demonstrates the use of
 */
public class DynamicOnCompletionRouteBuilder extends RouteBuilder {

    private final ExchangeCountingProcessor exchangeCountingProcessor;

    public DynamicOnCompletionRouteBuilder(ExchangeCountingProcessor exchangeCountingProcessor) {
        this.exchangeCountingProcessor = exchangeCountingProcessor;
    }

    @Override
    public void configure() throws Exception {
        from("direct:in")
            .process(exchangeCountingProcessor)
            .choice()
                .when(simple("${body} contains 'explode'"))
                    .throwException(new IllegalArgumentException("Exchange caused explosion"))
                .otherwise()
                    .to("mock:completed")
            .endChoice();
    }

}
