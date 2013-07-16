package org.camelcookbook.parallelprocessing.threadpools;

import org.apache.camel.builder.RouteBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Route that demonstrates using the Threads DSL to process messages using a custom thread pool defined inline.
 * @author jkorab
 */
public class CustomThreadPoolInlineRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool(new ThreadFactory() {
            private final AtomicInteger threadCounter = new AtomicInteger();
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("CustomThreadPool[" + threadCounter.incrementAndGet() + "]");
                return thread;
            }
        });

        from("direct:in")
            .log("Received ${body}:${threadName}")
            .threads().executorService(executorService)
            .log("Processing ${body}:${threadName}")
            .transform(simple("${threadName}"))
            .to("mock:out");
    }
}
