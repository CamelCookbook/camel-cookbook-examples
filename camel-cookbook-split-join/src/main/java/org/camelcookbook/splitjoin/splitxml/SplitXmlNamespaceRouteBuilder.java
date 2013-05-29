package org.camelcookbook.splitjoin.splitxml;

import org.apache.camel.builder.RouteBuilder;

/**
* @author jkorab
*/
class SplitXmlNamespaceRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
    from("direct:in")
        .split(xpath("//c:book[@category='Tech']/c:authors/c:author/text()")
                .namespace("c", "http://camelcookbook.org/schema/books"))
        .to("mock:out");
    }
}
