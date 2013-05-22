package org.camelcookbook.splitjoin.splitaggregate;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

/**
 * Demonstrates the splitting of a payload, processing of each of the fragments and reaggregating the results.
 */
public class SplitAggregateTest extends CamelTestSupport {
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new SplitAggregateRouteBuilder();
    }

    @Test
    public void testSplitAggregatesResponses() throws Exception {
        String[] array = new String[]{"one", "two", "three"};

        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.expectedMessageCount(1);

        template.sendBody("direct:in", array);

        assertMockEndpointsSatisfied();
        Exchange exchange = mockOut.getReceivedExchanges().get(0);
        Set<String> backendResponses = exchange.getIn().getBody(Set.class);
        assertTrue(backendResponses.containsAll(
                Arrays.asList("Processed: one",
                        "Processed: two",
                        "Processed: three")));
    }

}
