package org.camelcookbook.splitjoin.splitaggregate;

import org.apache.camel.builder.RouteBuilder;
import org.camelcookbook.splitjoin.aggregate.SetAggregationStrategy;

/**
 * @author jkorab
 */
public class SplitAggregateRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .split(body(), new SetAggregationStrategy())
                .inOut("direct:someBackEnd")
            .end()
            .to("mock:out");

        from("direct:someBackEnd")
            .transform(simple("Processed: ${body}"));
    }
}
