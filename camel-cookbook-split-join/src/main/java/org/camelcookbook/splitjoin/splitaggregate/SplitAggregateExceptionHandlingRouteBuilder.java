package org.camelcookbook.splitjoin.splitaggregate;

import org.apache.camel.builder.RouteBuilder;

/**
 * @author jkorab
 */
public class SplitAggregateExceptionHandlingRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .split(body(), new ExceptionHandlingSetAggregationStrategy())
                .inOut("direct:someBackEnd")
            .end()
            .to("mock:out");

        from("direct:someBackEnd")
            .choice()
                .when(simple("${header.CamelSplitIndex} == 1"))
                    .throwException(new IllegalStateException())
                .otherwise()
                    .transform(simple("Processed: ${body}"))
            .endChoice();
    }
}
