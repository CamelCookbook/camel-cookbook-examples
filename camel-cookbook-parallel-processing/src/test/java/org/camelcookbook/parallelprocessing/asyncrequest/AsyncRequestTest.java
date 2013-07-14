package org.camelcookbook.parallelprocessing.asyncrequest;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.Synchronization;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Test class that exercises parallel threading using the threads DSL.
 * @author jkorab
 */
public class AsyncRequestTest extends CamelTestSupport {

    private final Logger log = LoggerFactory.getLogger(AsyncRequestTest.class);

    @Override
    public RouteBuilder createRouteBuilder() {
        return new SlowProcessingRouteBuilder();
    }

    @Test
    public void testAsyncRequest() throws InterruptedException, ExecutionException {
        Future<Object> future = template.asyncRequestBody("direct:processInOut", "SomePayload");
        while(!future.isDone()) {
            log.info("Doing something else while processing...");
            Thread.sleep(200);
        }
        String response = (String) future.get(); // throws ExecutionException
        log.info("Received a response");
        assertEquals("Processed SomePayload", response);
    }

    @Test
    public void testAsyncRequestWithCallback() throws InterruptedException, ExecutionException {
        Future<Object> future = template.asyncCallbackRequestBody("direct:processInOut", "AnotherPayload", new Synchronization() {
            @Override
            public void onComplete(Exchange exchange) {
                assertEquals("Processed AnotherPayload", exchange.getOut().getBody());
            }

            @Override
            public void onFailure(Exchange exchange) {
                fail();
            }
        });
        while(!future.isDone()) {
            log.info("Doing something else while processing...");
            Thread.sleep(200);
        }
        log.info("Received a response");
    }

}
