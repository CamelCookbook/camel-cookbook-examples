package org.camelcookbook.routing.wiretap;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

/**
 * Processor that performs a deep copy of a {@link Cheese} message.
 */
public class CheeseCloningProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();
        Cheese cheese = in.getBody(Cheese.class);
        if (cheese != null) {
            in.setBody(cheese.clone());
        }
    }
}
