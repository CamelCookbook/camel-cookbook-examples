package org.camelcookbook.examples.transactions.utils;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
* Processor that throws an exception whenever called.
*/
public class ExceptionThrowingProcessor implements Processor {
    private final String message;

    public ExceptionThrowingProcessor() {
        this("boom!");
    }

    public ExceptionThrowingProcessor(String message) {
        this.message = message;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        throw new IllegalStateException(message);
    }
}
