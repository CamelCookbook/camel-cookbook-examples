package org.camelcookbook.splitjoin.aggregateparallel;

import org.apache.camel.builder.RouteBuilder;
import org.camelcookbook.splitjoin.aggregate.SetAggregationStrategy;

import java.util.concurrent.Executors;

/**
 * @author jkorab
 */
class AggregateExecutorServiceRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .aggregate(header("group"), new SetAggregationStrategy())
                    .completionSize(10).completionTimeout(400)
                    .executorService(Executors.newFixedThreadPool(20))
                .log("${threadName} - procesessing output")
                .delay(500)
                .to("mock:out")
            .end();
    }
}
