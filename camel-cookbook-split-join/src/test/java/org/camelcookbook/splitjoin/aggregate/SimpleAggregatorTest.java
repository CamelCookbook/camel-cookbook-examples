package org.camelcookbook.splitjoin.aggregate;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Test class that demonstrates a simple example of aggregation.
 * @author jkorab
 */
public class SimpleAggregatorTest extends CamelTestSupport {

    @Override
    public RouteBuilder createRouteBuilder() {
        return new SimpleAggregatorRouteBuilder();
    }

    @Test
    public void testAggregation() throws InterruptedException {
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.setExpectedMessageCount(2);

        template.sendBodyAndHeader("direct:in", "One", "group", "odd");
        template.sendBodyAndHeader("direct:in", "Two", "group", "even");
        template.sendBodyAndHeader("direct:in", "Three", "group", "odd");
        template.sendBodyAndHeader("direct:in", "Four", "group", "even");
        template.sendBodyAndHeader("direct:in", "Five", "group", "odd");
        template.sendBodyAndHeader("direct:in", "Six", "group", "even");
        template.sendBodyAndHeader("direct:in", "Seven", "group", "odd");
        template.sendBodyAndHeader("direct:in", "Eight", "group", "even");
        template.sendBodyAndHeader("direct:in", "Nine", "group", "odd");
        template.sendBodyAndHeader("direct:in", "Ten", "group", "even");

        assertMockEndpointsSatisfied();

        List<Exchange> receivedExchanges = mockOut.getReceivedExchanges();
        Set<String> odd = receivedExchanges.get(0).getIn().getBody(Set.class);
        assertTrue(odd.containsAll(Arrays.asList("One", "Three", "Five", "Seven", "Nine")));

        Set<String> even = receivedExchanges.get(1).getIn().getBody(Set.class);
        assertTrue(even.containsAll(Arrays.asList("Two", "Four", "Six", "Eight", "Ten")));
    }

}
