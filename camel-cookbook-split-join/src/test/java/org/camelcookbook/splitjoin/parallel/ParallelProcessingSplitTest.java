package org.camelcookbook.splitjoin.parallel;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class that demonstrates split message processing in parallel.
 * @author jkorab
 */
public class ParallelProcessingSplitTest extends CamelTestSupport {

    @Override
    public RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:in")
                    .split(body()).parallelProcessing()
                        .log("Processing message[${property.CamelSplitIndex}]")
                        .to("mock:split")
                    .end()
                    .to("mock:out");
            }
        };
    }

    @Test
    public void testSplittingInParallel() throws InterruptedException {
        List<String> messageFragments = new ArrayList<String>();
        int fragmentCount = 50;
        for (int i = 0; i < fragmentCount; i++) {
            messageFragments.add("fragment" + i);
        }
        MockEndpoint mockSplit = getMockEndpoint("mock:split");
        mockSplit.setExpectedMessageCount(fragmentCount);
        mockSplit.expectedBodiesReceivedInAnyOrder(messageFragments);

        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo(messageFragments);

        template.sendBody("direct:in", messageFragments);

        assertMockEndpointsSatisfied();
    }
}
