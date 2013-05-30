package org.camelcookbook.splitjoin.aggregateparallel;

import org.apache.camel.builder.RouteBuilder;
import org.camelcookbook.splitjoin.aggregate.SetAggregationStrategy;

/**
 * @author jkorab
 */
class AggregateParallelProcessingRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .aggregate(header("group"), new SetAggregationStrategy())
                    .completionSize(10).completionTimeout(400)
                    .parallelProcessing()
                .log("${threadName} - procesessing output")
                .delay(500)
                .to("mock:out")
            .end();
    }
}
