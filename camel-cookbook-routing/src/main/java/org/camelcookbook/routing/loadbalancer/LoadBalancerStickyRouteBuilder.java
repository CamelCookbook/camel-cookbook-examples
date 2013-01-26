package org.camelcookbook.routing.loadbalancer;

import org.apache.camel.builder.RouteBuilder;

public class LoadBalancerStickyRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .loadBalance().sticky(header("customerId"))
                .to("mock:first")
                .to("mock:second")
                .to("mock:third")
            .end()
            .to("mock:out");
    }
}
