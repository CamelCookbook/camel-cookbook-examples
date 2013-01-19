package org.camelcookbook.routing.multicast;

import org.apache.camel.builder.RouteBuilder;

/**
 * Simple multicast example with parallel processing.
 */
public class MulticastParallelProcessingRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
                .multicast().parallelProcessing()
                    .to("direct:first")
                    .to("direct:second")
                .end()
                .setHeader("threadName").simple("${threadName}")
                .to("mock:afterMulticast")
                .transform(constant("response"));

        from("direct:first")
                .setHeader("firstModifies").constant("apple")
                .setHeader("threadName").simple("${threadName}")
                .to("mock:first");

        from("direct:second")
                .setHeader("secondModifies").constant("banana")
                .setHeader("threadName").simple("${threadName}")
                .to("mock:second");
    }
}
