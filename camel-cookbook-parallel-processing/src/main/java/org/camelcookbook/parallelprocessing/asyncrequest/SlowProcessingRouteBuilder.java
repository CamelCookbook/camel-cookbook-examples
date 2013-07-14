package org.camelcookbook.parallelprocessing.asyncrequest;

import org.apache.camel.builder.RouteBuilder;

/**
 * @author jkorab
 */
public class SlowProcessingRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:processInOut")
            .log("Received ${body}")
            .delay(1000)
            .log("Processing ${body}")
            .transform(simple("Processed ${body}"));
    }
}
