package org.camelcookbook.parallelprocessing.threadsdsl;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Test class that exercises InOut messaging that uses the threads DSL.
 * @author jkorab
 */
public class ThreadsDslInOutTest extends CamelTestSupport {

    @Override
    public RouteBuilder createRouteBuilder() {
        return new ThreadsDslInOutRouteBuilder();
    }

    @Test
    public void testParallelConsumption() throws InterruptedException, ExecutionException {
        final int messageCount = 10;
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.setExpectedMessageCount(messageCount);
        mockOut.setResultWaitTime(5000);

        for (int i = 0; i < messageCount; i++) {
            Future<Object> future = template.asyncRequestBody("direct:in", "Message[" + i + "]");
            // here we get ask the Future to return to us the response, set by the thread assigned by the
            // threads() DSL
            String response = (String) future.get();
            assertTrue(response.equals("Processed"));
        }

        assertMockEndpointsSatisfied();
    }


}
