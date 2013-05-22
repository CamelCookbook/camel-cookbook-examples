package org.camelcookbook.splitjoin.aggregate;

import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;

/**
 * Test class that demonstrates a use of a dynamic completion size with aggregation.
 * @author jkorab
 */
public class AggregatorDynamicCompletionSizeSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/META-INF/spring/aggregatorDynamicCompletionSize-context.xml");
    }

    @Test
    public void testAggregation() throws InterruptedException {
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.setExpectedMessageCount(2);

        Map<String, Object> oddHeaders = new HashMap<String, Object>();
        oddHeaders.put("group", "odd");
        oddHeaders.put("batchSize", "5");

        Map<String, Object> evenHeaders = new HashMap<String, Object>();
        evenHeaders.put("group", "even");
        evenHeaders.put("batchSize", "4");

        template.sendBodyAndHeaders("direct:in", "One", oddHeaders);
        template.sendBodyAndHeaders("direct:in", "Two", evenHeaders);
        template.sendBodyAndHeaders("direct:in", "Three", oddHeaders);
        template.sendBodyAndHeaders("direct:in", "Four", evenHeaders);
        template.sendBodyAndHeaders("direct:in", "Five", oddHeaders);
        template.sendBodyAndHeaders("direct:in", "Six", evenHeaders);
        template.sendBodyAndHeaders("direct:in", "Seven", oddHeaders);
        template.sendBodyAndHeaders("direct:in", "Eight", evenHeaders);
        template.sendBodyAndHeaders("direct:in", "Nine", oddHeaders);

        assertMockEndpointsSatisfied();

        List<Exchange> receivedExchanges = mockOut.getReceivedExchanges();
        Set<String> even = receivedExchanges.get(0).getIn().getBody(Set.class);
        assertTrue(even.containsAll(Arrays.asList("Two", "Four", "Six", "Eight")));

        Set<String> odd = receivedExchanges.get(1).getIn().getBody(Set.class);
        assertTrue(odd.containsAll(Arrays.asList("One", "Three", "Five", "Seven", "Nine")));
    }

}
