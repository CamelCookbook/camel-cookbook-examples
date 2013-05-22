package org.camelcookbook.splitjoin.split;

import org.apache.camel.builder.RouteBuilder;

/**
* @author jkorab
*/
class SplitNaturalRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .split(body())
                .to("mock:split")
            .end()
            .to("mock:out");
    }
}
