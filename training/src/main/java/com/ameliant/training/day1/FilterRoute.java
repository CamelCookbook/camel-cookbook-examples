package com.ameliant.training.day1;

import org.apache.camel.builder.RouteBuilder;

public class FilterRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:in").routeId("FilterRoute")
            .filter(simple("${header[locale]} == 'se_SE'"))
                .transform(simple("${body} (Bork bork bork)"))
            .end()
            .transform(simple("Hello ${body}"))
            .to("mock:out");
    }

}
