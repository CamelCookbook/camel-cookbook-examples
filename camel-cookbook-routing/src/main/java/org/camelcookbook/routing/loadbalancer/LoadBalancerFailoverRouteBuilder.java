package org.camelcookbook.routing.loadbalancer;

import org.apache.camel.builder.RouteBuilder;

public class LoadBalancerFailoverRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("direct:in")
            .loadBalance()
                .failover(-1, false, true)
                .to("mock:first")
                .to("direct:second")
                .to("mock:third")
            .end()
            .to("mock:out");

        from("direct:second")
            .throwException(new IllegalStateException("oops"));
    }
}
