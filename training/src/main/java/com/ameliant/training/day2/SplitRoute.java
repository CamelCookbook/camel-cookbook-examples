package com.ameliant.training.day2;

import org.apache.camel.builder.RouteBuilder;

public class SplitRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:in")
            .split(body().tokenize(","))//.parallelProcessing()
                    .aggregationStrategy(new ConcatAggregationStrategy())
                .to("mock:split")
            .end()
            .to("mock:out");
    }

}
