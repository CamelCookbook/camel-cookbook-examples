package org.camelcookbook.parallelprocessing.threadpools;

import org.apache.camel.builder.RouteBuilder;

/**
 * Route that demonstrates using the Threads DSL to process messages using a custom thread pool defined in the
 * Camel registry.
 * @author jkorab
 */
public class CustomThreadPoolRefRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .log("Received ${body}:${threadName}")
            .threads().executorServiceRef("customThreadPool")
            .log("Processing ${body}:${threadName}")
            .transform(simple("${threadName}"))
            .to("mock:out");
    }
}
