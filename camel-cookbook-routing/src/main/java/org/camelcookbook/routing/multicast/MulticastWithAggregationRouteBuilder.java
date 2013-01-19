package org.camelcookbook.routing.multicast;

import org.apache.camel.builder.RouteBuilder;

/**
 * Simple multicast example with parallel processing.
 */
public class MulticastWithAggregationRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .multicast().aggregationStrategy(new ConcatenatingAggregationStrategy())
                .to("direct:first")
                .to("direct:second")
            .end()
            .transform(body()); // copy the In message to the Out message; this will become the route response

        from("direct:first")
            .transform(constant("first response"));

        from("direct:second")
            .transform(constant("second response"));
    }

}
