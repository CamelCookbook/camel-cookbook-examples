package org.camelcookbook.routing.wiretap;

import org.apache.camel.builder.RouteBuilder;
import org.camelcookbook.routing.model.CheeseCloningProcessor;
import org.camelcookbook.routing.model.CheeseRipener;

/**
 * Route showing wiretap without state leakage.
 */
public class WireTapStateNoLeaksRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:in")
                .log("Cheese is ${body.age} months old")
                .wireTap("direct:processInBackground")
                    .onPrepare(new CheeseCloningProcessor())
                .delay(constant(1000))
                .to("mock:out");

        from("direct:processInBackground")
                .bean(CheeseRipener.class, "ripen")
                .to("mock:tapped");
    }
}
