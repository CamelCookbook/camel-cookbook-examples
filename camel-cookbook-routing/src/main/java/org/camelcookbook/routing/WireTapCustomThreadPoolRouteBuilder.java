package org.camelcookbook.routing;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.ThreadPoolBuilder;

import java.util.concurrent.ExecutorService;

/**
 * Simplest possible example of the wireTap EIP.
 */
public class WireTapCustomThreadPoolRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        ThreadPoolBuilder builder = new ThreadPoolBuilder(getContext());
        ExecutorService oneThreadOnly = builder.poolSize(1).maxPoolSize(1)
                .maxQueueSize(100).build("JustMeDoingTheTapping");

        from("direct:in")
                .wireTap("direct:tapped").executorService(oneThreadOnly)
                .to("mock:out");

        from("direct:tapped")
                .setHeader("threadName").simple("${threadName}")
                .to("mock:tapped");
    }

}
