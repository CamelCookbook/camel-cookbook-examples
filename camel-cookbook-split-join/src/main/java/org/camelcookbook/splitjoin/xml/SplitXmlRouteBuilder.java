package org.camelcookbook.splitjoin.xml;

import org.apache.camel.builder.RouteBuilder;

/**
* @author jkorab
*/
class SplitXmlRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
    from("direct:in")
        .split(xpath("//book[@category='Tech']/authors/author/text()"))
        .to("mock:out");
    }
}
