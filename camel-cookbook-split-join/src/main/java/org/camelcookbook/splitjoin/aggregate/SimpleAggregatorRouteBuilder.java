package org.camelcookbook.splitjoin.aggregate;

import org.apache.camel.builder.RouteBuilder;

/**
* @author jkorab
*/
class SimpleAggregatorRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .log("${threadName} - ${body}")
            .aggregate(new SetAggregationStrategy())
                    .header("group").completionSize(5)
                .log("${threadName} - out")
                .to("mock:out")
            .end();
    }
}
