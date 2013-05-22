package org.camelcookbook.splitjoin.split;

import org.apache.camel.builder.RouteBuilder;

/**
* @author jkorab
*/
class SplitMultiLineRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .split(body().tokenize("\n"))
            .to("mock:out");
    }
}
