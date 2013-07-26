package org.camelcookbook.examples.transactions.oncompletion;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Demonstrates the use of a Synchronization to change state depending on whether an Exchange completed or failed.
 */
public class DynamicOnCompletionTest extends CamelTestSupport {

    private ExchangeCountingProcessor exchangeCountingProcessor;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        exchangeCountingProcessor = new ExchangeCountingProcessor();
        return new DynamicOnCompletionRouteBuilder(exchangeCountingProcessor);
    }

    @Test
    public void testOnCompletion() throws InterruptedException {
        MockEndpoint mockCompleted = getMockEndpoint("mock:completed");
        mockCompleted.setExpectedMessageCount(1);
        mockCompleted.message(0).body().isEqualTo("this message should complete");

        template.asyncSendBody("direct:in", "this message should explode");
        template.asyncSendBody("direct:in", "this message should complete");

        Thread.sleep(100);
        assertMockEndpointsSatisfied();

        assertEquals(2, exchangeCountingProcessor.getExchangesSeen());
        assertEquals(1, exchangeCountingProcessor.getExchangesCompleted());
        assertEquals(1, exchangeCountingProcessor.getExchangesFailed());
    }
}
