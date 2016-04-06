package com.ameliant.training.day2;

import org.apache.camel.builder.RouteBuilder;

import java.util.function.Predicate;

public class SplitAggregateRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:in").startupOrder(20)
            .split(body().tokenize(","))
                .to("direct:aggregate")
            .end();

        Predicate<String> isEven = (String s) -> {
            return Integer.valueOf(s) % 2 == 0;
        };

        from("direct:aggregate").startupOrder(10)
            .aggregate(method(isEven),
                    new ConcatAggregationStrategy())
                .completionSize(5)
                .completionTimeout(1000)
                .to("mock:out")
            .end();

    }


}
