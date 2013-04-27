package org.camelcookbook.examples.testing.exchange;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

/**
 * A complicated processor that uses the Camel Exchange API.
 */
public class ComplicatedProcessor implements Processor {

    public static final String PROCESSOR_TEXT = "SOMETHING";

    @Override
    public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();
        String action = in.getHeader("action", String.class);
        if ((action == null) || (action.isEmpty())) {
            in.setHeader("actionTaken", false);
        } else {
            in.setHeader("actionTaken", true);
            String body = in.getBody(String.class);
            if (action.equals("append")) {
                in.setBody(body + " " + PROCESSOR_TEXT);
            } else if (action.equals("prepend")) {
                in.setBody(PROCESSOR_TEXT + " " + body);
            } else {
                throw new IllegalArgumentException(
                        "Unrecognized action requested: [" + action + "]");
            }
        }
    }
}
