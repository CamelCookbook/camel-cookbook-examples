package org.camelcookbook.structuringroutes.direct;

import org.apache.camel.builder.RouteBuilder;


public class DirectRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:A")
            .transform(simple("A1[ ${body} ]"))
            .to("direct:B")
            .transform(simple("A2[ ${body} ]"))
            .to("mock:endA");

        from("direct:B")
            .transform(simple("B[ ${body} ]"))
            .to("mock:endB");
    }
}
