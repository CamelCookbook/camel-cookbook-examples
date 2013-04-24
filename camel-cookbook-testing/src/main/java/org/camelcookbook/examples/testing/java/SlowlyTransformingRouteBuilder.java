package org.camelcookbook.examples.testing.java;

import org.apache.camel.builder.RouteBuilder;

/**
 * Route builder that performs a slow transformation on the body of the exchange.
 */
public class SlowlyTransformingRouteBuilder extends RouteBuilder {
    private String sourceUri;
    private String targetUri;

    public void setSourceUri(String sourceUri) {
        this.sourceUri = sourceUri;
    }

    public void setTargetUri(String targetUri) {
        this.targetUri = targetUri;
    }

    @Override
    public void configure() throws Exception {
        from(sourceUri)
            .to("seda:transformBody");

        from("seda:transformBody?concurrentConsumers=15")
            .transform(simple("Modified: ${body}"))
            .delay(100) // simulate a slow transformation
            .to("seda:sendTransformed");

        from("seda:sendTransformed")
            .resequence().simple("${header.mySequenceId}").stream()
            .to(targetUri);
    }

}
