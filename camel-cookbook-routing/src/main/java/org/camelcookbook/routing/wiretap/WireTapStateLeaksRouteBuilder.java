package org.camelcookbook.routing.wiretap;

import org.apache.camel.builder.RouteBuilder;
import org.camelcookbook.routing.model.CheeseRipener;

/**
* Route showing wiretap state leakage.
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
