package org.camelcookbook.splitjoin.splitparallel;

import org.apache.camel.builder.RouteBuilder;

/**
* @author jkorab
*/
class SplitParallelProcessingExceptionHandlingRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .split(body()).parallelProcessing().stopOnException()
                .log("Processing message[${property.CamelSplitIndex}]")
                .to("direct:failOn20th")
            .end()
            .to("mock:out");

        from("direct:failOn20th")
            .choice()
                .when(simple("${property.CamelSplitIndex} == 20"))
                    .throwException(new IllegalStateException("boom"))
                .otherwise()
                    .to("mock:split")
            .endChoice();
    }
}
