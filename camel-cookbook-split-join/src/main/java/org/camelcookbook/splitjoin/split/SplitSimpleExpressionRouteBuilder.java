package org.camelcookbook.splitjoin.split;

import org.apache.camel.builder.RouteBuilder;

/**
* @author jkorab
*/
class SplitSimpleExpressionRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .split(simple("${body.wrapped}"))
            .to("mock:out");
    }
}
