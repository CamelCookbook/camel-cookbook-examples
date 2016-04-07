package com.ameliant.training.day3;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;

/**
 * @author jkorab
 */
public class TransactedJmsRoute extends RouteBuilder{
    @Override
    public void configure() throws Exception {

        from("jms:in?transacted=true")
            .process((Exchange exchange) -> {
                Message in = exchange.getIn();
                String body = in.getBody(String.class);
                if (body.equals("Boom")) {
                    throw new IllegalStateException("Bad message");
                } else {
                    in.setBody("Hello " + body);
                }
            })
            .to("jms:out");
            //.throwException(new IllegalStateException("Bleh"));
    }
}
