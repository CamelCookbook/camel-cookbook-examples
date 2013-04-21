package org.camelcookbook.examples.testing.mockreply;

import org.apache.camel.builder.RouteBuilder;


/**
 * Simple route that expects a response from a mock endpoint.
 */
public class MockReplyRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:in")
            .inOut("mock:replying")
            .to("mock:out");
    }

}
