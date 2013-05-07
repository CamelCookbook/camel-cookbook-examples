package org.camelcookbook.splitjoin.split;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.*;

/**
 * Demonstrates that the remaining split elements will be processed by default after an exception is thrown.
 */
public class ExceptionHandlingSplitTest extends CamelTestSupport {
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:in")
                    .split(simple("${body}"))
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                if (exchange.getProperty("CamelSplitIndex").equals(0)) {
                                    throw new IllegalStateException("boom");
                                }
                            }
                        })
                        .to("mock:split")
                    .end()
                    .to("mock:out");
            }
        };
    }

    @Test
    public void testRemainderElementsProcessedOnException() throws Exception {
        String[] array = new String[] {"one", "two", "three"};

        MockEndpoint mockSplit = getMockEndpoint("mock:split");
        mockSplit.expectedMessageCount(2);
        mockSplit.expectedBodiesReceived("two", "three");

        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.expectedMessageCount(0);

        try {
            template.sendBody("direct:in", array);
            fail("Exception not thrown");
        } catch (Exception ex) {
            assertTrue(ex.getCause() instanceof IllegalStateException);
            assertMockEndpointsSatisfied();
        }

    }

}
