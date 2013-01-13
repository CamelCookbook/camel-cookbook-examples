package org.camelcookbook.routing.multicast;

import org.apache.camel.builder.RouteBuilder;

/**
 * Example shows shallow copying of model in multicast.
 */
public class MulticastShallowCopyRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .multicast()
                .to("direct:first")
                .to("direct:second")
            .end()
            .to("mock:afterMulticast");

        from("direct:first")
            .setHeader("modifiedBy").constant("first")
            .setHeader("threadName").simple("${threadName}")
            .to("mock:first");

        from("direct:second")
            .setHeader("modifiedBy").constant("second")
            .setHeader("threadName").simple("${threadName}")
            .to("mock:second");
    }
}
