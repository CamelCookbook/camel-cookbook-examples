package org.camelcookbook.splitjoin.parallel;

import org.apache.camel.builder.RouteBuilder;

/**
* @author jkorab
*/
class SplitParallelProcessingTimeoutRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .split(body()).parallelProcessing().timeout(5000)
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
            .delay(5000)
            .to("mock:delayed");
    }
}
