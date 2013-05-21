package org.camelcookbook.splitjoin.aggregate;

import org.apache.camel.builder.RouteBuilder;

/**
* @author jkorab
*/
class DynamicCompletionSizeAggregatorRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .log("${threadName} - ${body}")
            .aggregate(header("group"), new SetAggregationStrategy())
                    .completionSize(header("batchSize"))
                .log("${threadName} - out")
                .to("mock:out")
            .end();
    }
}
