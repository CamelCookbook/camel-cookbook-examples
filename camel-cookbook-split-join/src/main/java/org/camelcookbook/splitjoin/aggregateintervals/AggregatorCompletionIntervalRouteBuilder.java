package org.camelcookbook.splitjoin.aggregateintervals;

import org.apache.camel.builder.RouteBuilder;
import org.camelcookbook.splitjoin.aggregate.SetAggregationStrategy;

/**
* @author jkorab
*/
class AggregatorCompletionIntervalRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .log("${threadName} - ${body}")
            .aggregate(header("group"), new SetAggregationStrategy())
                    .completionSize(10).completionInterval(400)
                .log("${threadName} - out")
                .delay(500)
                .to("mock:out")
            .end();
    }
}
