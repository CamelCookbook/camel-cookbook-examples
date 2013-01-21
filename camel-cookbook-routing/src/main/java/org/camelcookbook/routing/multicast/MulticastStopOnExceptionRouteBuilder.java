package org.camelcookbook.routing.multicast;

import org.apache.camel.builder.RouteBuilder;

/**
 * Example shows multicast stopping on exception.
 */
public class MulticastStopOnExceptionRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .onException(Exception.class)
                .log("caught ${exception}")
                .to("mock:exceptionHandler")
                .handled(true)
            .end()
            .multicast().stopOnException()
                .to("direct:first")
                .to("direct:second")
            .endParent()
            .to("mock:afterMulticast");

        from("direct:first")
            .to("mock:first")
            .throwException(new IllegalStateException("something went horribly wrong"));

        from("direct:second")
                .to("mock:second");
    }
}
