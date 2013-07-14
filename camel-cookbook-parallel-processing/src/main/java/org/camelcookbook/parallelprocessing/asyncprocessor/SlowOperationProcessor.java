package org.camelcookbook.parallelprocessing.asyncprocessor;

import org.apache.camel.AsyncCallback;
import org.apache.camel.AsyncProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jkorab
 */
public class SlowOperationProcessor implements AsyncProcessor {
    private final Logger log = LoggerFactory.getLogger(SlowOperationProcessor.class);
    private final ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor();

    @Override
    public boolean process(final Exchange exchange, final AsyncCallback asyncCallback) {
        final boolean completesSynchronously = false;
        final Processor processor = this;
        backgroundExecutor.submit(new Runnable() {
            @Override
            public void run() {
                log.info("Running operation asynchronously");
                try {
                    processor.process(exchange);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                // the current thread will continue to process the exchange
                // through the remainder of the route
                asyncCallback.done(completesSynchronously);
            }
        });
        return completesSynchronously;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        try {
            log.info("Doing something slowly");
            Thread.sleep(200); // this runs slowly
            log.info("...done");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
