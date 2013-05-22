package org.camelcookbook.splitjoin.split;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstrates that the remaining split elements will be processed by default after an exception is thrown.
 */
public class SplitExceptionHandlingTest extends CamelTestSupport {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new SplitExceptionHandlingRouteBuilder();
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
        } catch (CamelExecutionException ex) {
            assertTrue(ex.getCause() instanceof IllegalStateException);
            assertMockEndpointsSatisfied();
        }

    }

}
