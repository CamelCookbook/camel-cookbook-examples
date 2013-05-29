package org.camelcookbook.splitjoin.splitparallel;

import org.apache.camel.builder.RouteBuilder;

/**
* @author jkorab
*/
class SplitParallelProcessingTimeoutRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .split(body()).parallelProcessing().timeout(1000)
                .log("Processing message[${property.CamelSplitIndex}]")
                .to("direct:delay20th")
            .end()
            .to("mock:out");

        from("direct:delay20th")
            .choice()
                .when(simple("${property.CamelSplitIndex} == 20"))
                    .to("direct:longDelay")
                .otherwise()
                    .to("mock:split")
            .endChoice();

        from("direct:longDelay")
            .delay(1500)
            .to("mock:delayed");
    }
}
