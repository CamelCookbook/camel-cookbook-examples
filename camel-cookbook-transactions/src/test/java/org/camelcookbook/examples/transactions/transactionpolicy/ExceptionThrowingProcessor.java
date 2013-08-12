package org.camelcookbook.examples.transactions.transactionpolicy;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
* Processor that throws an exception whenever called.
*/
class ExceptionThrowingProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        throw new IllegalStateException("boom!");
    }
}
