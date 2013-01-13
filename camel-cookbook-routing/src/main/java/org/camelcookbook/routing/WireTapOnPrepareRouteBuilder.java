package org.camelcookbook.routing;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

/**
 * Simplest possible example of the wireTap EIP.
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
