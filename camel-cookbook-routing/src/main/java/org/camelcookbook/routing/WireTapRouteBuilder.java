package org.camelcookbook.routing;

import org.apache.camel.builder.RouteBuilder;

/**
 * Simplest possible example of the wireTap EIP.
 */
public class WireTapRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:in")
                .wireTap("mock:tapped")
                .to("mock:out");
    }

}
