package org.camelcookbook.splitjoin.aggregateparallel;

import org.apache.camel.builder.RouteBuilder;
import org.camelcookbook.splitjoin.aggregate.SetAggregationStrategy;

/**
 * @author jkorab
 */
class AggregatorCompletionTimeoutParallelRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .log("${threadName} - ${body}")
            .aggregate(header("group"), new SetAggregationStrategy())
                    .completionSize(10).completionTimeout(1000).parallelProcessing()
                .log("${threadName} - out")
                .delay(500)
                .to("mock:out")
            .end();
    }
}
