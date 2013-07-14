package org.camelcookbook.parallelprocessing.threadsdsl;

import org.apache.camel.builder.RouteBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Route that demonstrates using the Threads DSL to process messages from a single threaded endpoint concurrently.
 * @author jkorab
 */
public class ThreadsDslInOutRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .log("Received ${body}:${threadName}")
            .threads()
            .delay(200)
            .log("Processing ${body}:${threadName}")
            .to("mock:out")
            .transform(constant("Processed"));
    }
}
