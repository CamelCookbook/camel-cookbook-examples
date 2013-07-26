package org.camelcookbook.examples.transactions.oncompletion;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Demonstrates the use of onCompletion blocks.
 */
public class OnCompletionTest extends CamelTestSupport {

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new OnCompletionRouteBuilder();
    }

    @Test
    public void testOnCompletionDefinedAtRouteLevel() throws InterruptedException {
        MockEndpoint mockFailed = getMockEndpoint("mock:failed");
        mockFailed.setExpectedMessageCount(1);
        mockFailed.message(0).body().isEqualTo("this message should explode");

        template.asyncSendBody("direct:onCompletion", "this message should explode");

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testOnCompletionNotDefinedAtRouteLevel() throws InterruptedException {
        MockEndpoint mockGlobal = getMockEndpoint("mock:global");
        mockGlobal.setExpectedMessageCount(1);
        mockGlobal.message(0).body().isEqualTo("this message should explode");

        template.asyncSendBody("direct:noOnCompletion", "this message should explode");

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testOnCompletionChained() throws InterruptedException {
        MockEndpoint mockFailed = getMockEndpoint("mock:failed");
        mockFailed.setExpectedMessageCount(1);
        mockFailed.message(0).body().isEqualTo("this message should explode");

        MockEndpoint mockCompleted = getMockEndpoint("mock:completed");
        mockCompleted.setExpectedMessageCount(1);
        mockCompleted.message(0).body().isEqualTo("this message should complete");

        // here we have 2 onCompletions set - one on a top-level route, and another on a sub-route
        // both should be triggered depending on success or failure
        template.asyncSendBody("direct:chained", "this message should explode");
        template.asyncSendBody("direct:chained", "this message should complete");

        assertMockEndpointsSatisfied();
    }

}
