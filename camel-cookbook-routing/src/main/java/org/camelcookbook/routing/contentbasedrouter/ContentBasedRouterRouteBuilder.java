package org.camelcookbook.routing.contentbasedrouter;

import org.apache.camel.builder.RouteBuilder;

public class ContentBasedRouterRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:start").
                choice().
                when().simple("${body} contains 'Camel'").
                to("mock:camel").
                log("Camel ${body}").
                otherwise().
                to("mock:other").
                log("Other ${body}").
                end().
                log("Message ${body}");

    }
}
