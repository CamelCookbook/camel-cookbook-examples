package org.camelcookbook.splitjoin.split;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

/**
* @author jkorab
*/
class SplitExceptionHandlingStopOnExceptionRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .split(simple("${body}")).stopOnException()
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        if (exchange.getProperty("CamelSplitIndex").equals(1)) {
                            throw new IllegalStateException("boom");
                        }
                    }
                })
                .to("mock:split")
            .end()
            .to("mock:out");
    }
}
