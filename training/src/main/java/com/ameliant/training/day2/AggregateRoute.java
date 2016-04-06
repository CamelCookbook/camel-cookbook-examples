package com.ameliant.training.day2;

import org.apache.camel.builder.RouteBuilder;

public class AggregateRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:in")
            .aggregate(constant(true), // aggregate everything into same bucket "true"
                    new ConcatAggregationStrategy())
                    .completionSize(5)
                    .completionTimeout(1000)
                .to("mock:out")
            .end();
    }

}
