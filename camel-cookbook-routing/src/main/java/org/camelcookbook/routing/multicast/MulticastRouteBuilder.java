package org.camelcookbook.routing.multicast;

import org.apache.camel.builder.RouteBuilder;

/**
 * Simple multicast example.
 */
public class MulticastRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
                .multicast()
                    .to("mock:first")
                    .to("mock:second")
                    .to("mock:third")
                .end();
    }
}
