package org.camelcookbook.routing.wiretap;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.ThreadPoolBuilder;

import java.util.concurrent.ExecutorService;

/**
 * Using a custom thread pool with a wiretap.
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
