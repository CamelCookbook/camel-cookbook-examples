package org.camelcookbook.splitjoin.splitreaggregate;

import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.camelcookbook.splitjoin.aggregate.SetAggregationStrategy;

/**
 * @author jkorab
 */
public class SplitReaggregateRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .split(xpath("//book"))
                .setHeader("category", xpath("string(/book/@category)").stringResult())
                .transform(xpath("string(/book/@title)").stringResult())
                .to("direct:groupByCategory")
            .end();

        from("direct:groupByCategory")
            .aggregate(header("category"), new SetAggregationStrategy()).completionTimeout(500)
            .to("mock:out");
    }
}
