package org.camelcookbook.routing.multicast;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 * Simple multicast example with parallel processing.
 */
public class MulticastWithAggregationOfRequestRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        AggregationStrategy concatenationStrategy = new ConcatenatingAggregationStrategy();

        from("direct:in")
            .enrich("direct:performMulticast", concatenationStrategy)
            .transform(body()); // copy the In message to the Out message; this will become the route response

        from("direct:performMulticast")
            .multicast().aggregationStrategy(concatenationStrategy)
                .to("direct:first")
                .to("direct:second")
            .end();

        from("direct:first")
            .transform(constant("first response"));

        from("direct:second")
            .transform(constant("second response"));
    }

}
