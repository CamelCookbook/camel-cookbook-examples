package org.camelcookbook.splitjoin.splitxml;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;

/**
* @author jkorab
*/
class SplitXmlNamespacesRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
    Namespaces namespaces = new Namespaces("c", "http://camelcookbook.org/schema/books")
        .add("se", "http://camelcookbook.org/schema/somethingElse");

    from("direct:in")
        .split(namespaces.xpath("//c:book[@category='Tech']/c:authors/c:author/text()"))
        .to("mock:out");
    }
}
