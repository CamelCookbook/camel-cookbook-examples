package org.camelcookbook.parallelprocessing.threadpoolprofiles;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.ThreadPoolProfileBuilder;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.spi.ThreadPoolProfile;

/**
 * Route that demonstrates using the Threads DSL to process messages using a custom thread pool defined in the
 * Camel registry.
 * @author jkorab
 */
public class CustomThreadPoolProfileRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        ThreadPoolProfile customThreadPoolProfile =
                new ThreadPoolProfileBuilder("customThreadPoolProfile").poolSize(5).maxQueueSize(100).build();
        ModelCamelContext context = getContext();
        context.getExecutorServiceManager().registerThreadPoolProfile(customThreadPoolProfile);

        from("direct:in")
            .log("Received ${body}:${threadName}")
            .threads().executorServiceRef("customThreadPoolProfile")
            .log("Processing ${body}:${threadName}")
            .transform(simple("${threadName}"))
            .to("mock:out");
    }
}
