package org.camelcookbook.splitjoin.splitparallel;

import org.apache.camel.builder.RouteBuilder;

import java.util.concurrent.Executors;

/**
* @author jkorab
*/
class SplitParallelProcessingRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .split(body()).parallelProcessing().executorService(Executors.newSingleThreadExecutor())
                .log("Processing message[${property.CamelSplitIndex}]")
                .to("mock:split")
            .end()
            .to("mock:out");
    }
}
