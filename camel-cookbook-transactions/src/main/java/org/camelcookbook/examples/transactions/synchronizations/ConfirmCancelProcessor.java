package org.camelcookbook.examples.transactions.synchronizations;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spi.Synchronization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processor that starts a mock remote operation, then commits or cancels it depending on whether the Exchange
 * was successfully processed through the rest of the route.
 */
public class ConfirmCancelProcessor implements Processor {
    private final Logger log = LoggerFactory.getLogger(ConfirmCancelProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("Starting two-phase operation");

        final ProducerTemplate producerTemplate =
                exchange.getContext().createProducerTemplate();
        producerTemplate.send("mock:start", exchange);

        exchange.addOnCompletion(new Synchronization() {

            @Override
            public void onComplete(Exchange exchange) {
                log.info("Completed - confirming");
                producerTemplate.send("mock:confirm", exchange);
            }

            @Override
            public void onFailure(Exchange exchange) {
                log.info("Failed - cancelling");
                producerTemplate.send("mock:cancel", exchange);
            }
        });
    }

}
