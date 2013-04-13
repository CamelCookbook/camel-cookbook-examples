package org.camelcookbook.examples.testing.java;

import org.apache.camel.builder.RouteBuilder;

/**
 * Route builder that prepends the body of the exchange that is passed to it.
 */
public class SimpleTransformRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .transform(simple("Modified: ${body}"))
            .to("mock:out");
    }
}
