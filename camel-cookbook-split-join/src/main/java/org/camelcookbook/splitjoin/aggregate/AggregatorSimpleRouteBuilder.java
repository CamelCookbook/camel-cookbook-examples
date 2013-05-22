package org.camelcookbook.splitjoin.aggregate;

import org.apache.camel.builder.RouteBuilder;

/**
* @author jkorab
*/
class AggregatorSimpleRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .log("${threadName} - ${body}")
            .aggregate(header("group"), new SetAggregationStrategy()).completionSize(5)
                .log("${threadName} - out")
                .to("mock:out")
            .end();
    }
}
