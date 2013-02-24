package org.camelcookbook.structuringroutes;

import org.apache.camel.builder.RouteBuilder;

/**
 * Route that demonstrates the use of properties in Java.
 */
public class PropertyConsumingRouteBuilder extends RouteBuilder{

    @Override
    public void configure() throws Exception {
        from("{{start.endpoint}}")
            .transform().simple("{{transform.message}}: ${body}")
            .log("Set message to ${body}")
            .to("{{end.endpoint}}");
    }

}
