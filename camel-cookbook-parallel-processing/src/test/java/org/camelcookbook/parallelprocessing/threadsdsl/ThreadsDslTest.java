package org.camelcookbook.parallelprocessing.threadsdsl;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

/**
 * Test class that exercises parallel threading using the threads DSL.
 * @author jkorab
 */
public class ThreadsDslTest extends CamelTestSupport {

    @Override
    public RouteBuilder createRouteBuilder() {
        return new ThreadsDslRouteBuilder();
    }

    @Test
    public void testParallelConsumption() throws InterruptedException {
        final int messageCount = 50;
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.setExpectedMessageCount(messageCount);
        mockOut.setResultWaitTime(5000);

        for (int i = 0; i < messageCount; i++) {
            template.asyncSendBody("direct:in", "Message[" + i + "]");
        }

        assertMockEndpointsSatisfied();
    }


}
