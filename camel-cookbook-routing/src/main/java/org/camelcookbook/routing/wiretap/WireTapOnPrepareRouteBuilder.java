package org.camelcookbook.routing.wiretap;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

/**
 * Using the <code>onPrepare</code> statement to modify the tapped message during the send.
 */
public class WireTapOnPrepareRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        Processor markProcessed = new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader("processorAction", "triggered");
            }
        };

        from("direct:in")
                .wireTap("mock:tapped").onPrepare(markProcessed)
                .to("mock:out");
    }

}
