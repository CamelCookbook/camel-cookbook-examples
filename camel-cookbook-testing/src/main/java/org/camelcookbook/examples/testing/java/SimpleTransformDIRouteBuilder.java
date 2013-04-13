package org.camelcookbook.examples.testing.java;

import org.apache.camel.builder.RouteBuilder;

/**
 * Route builder that prepends the body of the exchange that is passed to it.
 */
public class SimpleTransformDIRouteBuilder extends RouteBuilder {
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
            .transform(simple("Modified: ${body}"))
            .to(targetUri);
    }

}
