package org.camelcookbook.routing.wiretap;

import org.apache.camel.builder.RouteBuilder;

/**
 * Simplest possible example of the wiretap EIP.
 */
public class WireTapRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:in")
                .wireTap("mock:tapped")
                .to("mock:out");
    }

}
