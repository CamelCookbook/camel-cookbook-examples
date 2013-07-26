package org.camelcookbook.examples.transactions.oncompletion;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spi.Synchronization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Processor that counts how many exchanges have been successfully processed through a route.
 */
public class ExchangeCountingProcessor implements Processor {

    private final Logger log = LoggerFactory.getLogger(ExchangeCountingProcessor.class);

    private final AtomicInteger exchangesSeen = new AtomicInteger();
    private final AtomicInteger exchangesCompleted = new AtomicInteger();
    private final AtomicInteger exchangesFailed = new AtomicInteger();


    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("Seen {} exchanges", exchangesSeen.incrementAndGet());
        exchange.addOnCompletion(new Synchronization() {

            @Override
            public void onComplete(Exchange exchange) {
                log.info("Completed. Total completed: {}", exchangesCompleted.incrementAndGet());
            }

            @Override
            public void onFailure(Exchange exchange) {
                log.info("Failed. Total failed: {}", exchangesFailed.incrementAndGet());
            }
        });
    }

    public int getExchangesSeen() {
        return exchangesSeen.intValue();
    }

    public int getExchangesCompleted() {
        return exchangesCompleted.intValue();
    }

    public int getExchangesFailed() {
        return exchangesFailed.intValue();
    }
}
