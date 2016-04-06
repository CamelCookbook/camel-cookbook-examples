package com.ameliant.training.day1;

import org.apache.camel.builder.RouteBuilder;

public class SimpleRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:in")
            .transform(simple("Hello ${body}"))
            .to("mock:out");
    }

}
