package org.camelcookbook.splitjoin.split;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Shows how multi-line Strings may be split using a token.
 */
public class MultiLineStringSplitSpringTest extends CamelSpringTestSupport {
    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/META-INF/spring/multiLineStringSplit-context.xml");
    }

    @Test
    public void testSplitMultilineString() throws InterruptedException {
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.expectedBodiesReceived("this is a", "multi-line", "piece of text");

        String multiLineSting = "this is a\nmulti-line\npiece of text";
        template.sendBody("direct:in", multiLineSting);

        assertMockEndpointsSatisfied();
    }
}
