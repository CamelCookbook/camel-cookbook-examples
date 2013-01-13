package org.camelcookbook.routing;

import org.apache.camel.builder.RouteBuilder;

/**
* @author jakub
*/
class WireTapStateLeaksRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:in")
                .log("Cheese is ${body.age} months old")
                .wireTap("direct:processInBackground")
                .delay(constant(1000))
                .to("mock:out");

        from("direct:processInBackground")
                .bean(CheeseRipener.class, "ripen")
                .to("mock:tapped");
    }
}
