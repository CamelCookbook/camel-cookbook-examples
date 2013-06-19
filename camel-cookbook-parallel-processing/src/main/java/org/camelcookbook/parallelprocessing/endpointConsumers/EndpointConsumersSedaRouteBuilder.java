package org.camelcookbook.parallelprocessing.endpointConsumers;

import org.apache.camel.builder.RouteBuilder;

/**
 * Route that demonstrates increasing the number of consumers on a SEDA endpoint.
 * @author jkorab
 */
public class EndpointConsumersSedaRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("seda:in?concurrentConsumers=10")
            .delay(200)
            .log("Processing ${body}:${threadName}")
            .to("mock:out");
    }
}
