package org.camelcookbook.parallelprocessing.asyncprocessor;

import org.apache.camel.builder.RouteBuilder;

/**
 * Demonstrates the use of an {@link org.apache.camel.AsyncProcessor}
 */
public class AsyncProcessorRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("seda:in?concurrentConsumers=5")
            .to("direct:in")
            .log("Processed by:${threadName}");

        from("direct:in")
            .log("Processing ${body}:${threadName}")
            .process(new SlowOperationProcessor())
            .log("Completed ${body}:${threadName}")
            .to("mock:out");

        from("direct:sync?synchronous=true")
            .log("Processing ${body}:${threadName}")
            .process(new SlowOperationProcessor())
            .log("Completed ${body}:${threadName}")
            .to("mock:out");
    }
}
