package org.camelcookbook.splitjoin.splitaggregate;

import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.Set;

/**
 * Demonstrates the handling of an exception during splitting and aggregation.
 */
public class SplitAggregateExceptionHandlingSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/META-INF/spring/splitAggregateExceptionHandling-context.xml");
    }

    @Test
    public void testHandlesException() throws Exception {
        String[] array = new String[]{"one", "two", "three"};

        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.expectedMessageCount(1);

        template.sendBody("direct:in", array);

        assertMockEndpointsSatisfied();
        Exchange exchange = mockOut.getReceivedExchanges().get(0);
        Set<String> backendResponses = exchange.getIn().getBody(Set.class);
        assertTrue(backendResponses.containsAll(
                Arrays.asList("Processed: one",
                        "Failed: two",
                        "Processed: three")));
    }

}
