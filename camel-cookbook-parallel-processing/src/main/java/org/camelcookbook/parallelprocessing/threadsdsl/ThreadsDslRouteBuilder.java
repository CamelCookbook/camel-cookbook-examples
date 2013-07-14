package org.camelcookbook.parallelprocessing.threadsdsl;

import org.apache.camel.builder.RouteBuilder;

/**
 * Route that demonstrates using the Threads DSL to process messages from a single threaded endpoint concurrently.
 * @author jkorab
 */
public class ThreadsDslRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .log("Received ${body}:${threadName}")
            .threads()
            .delay(200)
            .log("Processing ${body}:${threadName}")
            .to("mock:out");
    }
}
