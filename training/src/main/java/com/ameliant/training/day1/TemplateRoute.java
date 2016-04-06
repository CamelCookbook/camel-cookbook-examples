package com.ameliant.training.day1;

import org.apache.camel.builder.RouteBuilder;

public class TemplateRoute extends RouteBuilder {

    private String startUri;
    private String endUri;
    private int offset;
    private String prefix;

    public void setEndUri(String endUri) {
        this.endUri = endUri;
    }

    public void setStartUri(String startUri) {
        this.startUri = startUri;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void configure() throws Exception {
        from(startUri)
                .routeId(prefix + ".in")
                .startupOrder(offset + 20)
            .log("Received: ${body}")
            .toF("direct:%s.transform", prefix);

        fromF("direct:%s.transform", prefix)
                .routeId(prefix + ".transform")
                .startupOrder(offset + 10)
            .transform(simple("Hello ${body}"))
                .id(prefix + ".greeting")
            .to(endUri);
    }

}
