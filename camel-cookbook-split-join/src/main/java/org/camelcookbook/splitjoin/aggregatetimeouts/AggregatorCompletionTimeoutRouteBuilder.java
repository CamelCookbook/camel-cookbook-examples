package org.camelcookbook.splitjoin.aggregatetimeouts;

import org.apache.camel.builder.RouteBuilder;
import org.camelcookbook.splitjoin.aggregate.SetAggregationStrategy;

/**
* @author jkorab
*/
class AggregatorCompletionTimeoutRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .log("${threadName} - ${body}")
            .aggregate(header("group"), new SetAggregationStrategy())
                    .completionSize(10).completionTimeout(1000)
                .log("${threadName} - out")
                .delay(500)
                .to("mock:out")
            .end();
    }
}
