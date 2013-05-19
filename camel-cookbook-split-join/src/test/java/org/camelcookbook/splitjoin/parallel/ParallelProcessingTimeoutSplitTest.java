package org.camelcookbook.splitjoin.parallel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class that demonstrates exception handling when processing split messages in parallel.
 * @author jkorab
 */
public class ParallelProcessingTimeoutSplitTest extends CamelTestSupport {

    @Override
    public RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:in")
                    .split(body()).parallelProcessing().timeout(4000)
                        .log("Processing message[${property.CamelSplitIndex}]")
                        .to("direct:delay20th")
                    .end()
                    .to("mock:out");

                from("direct:delay20th")
                    .choice()
                        .when(simple("${property.CamelSplitIndex} == 20"))
                            .to("direct:longDelay")
                        .otherwise()
                            .to("mock:split")
                    .endChoice();

                from("direct:longDelay")
                    .delay(5000);
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
        mockSplit.setExpectedMessageCount(fragmentCount - 1);

        ArrayList<String> expectedFragments = new ArrayList<String>(messageFragments);
        int indexDelayed = 20;
        expectedFragments.remove(indexDelayed);
        mockSplit.expectedBodiesReceivedInAnyOrder(expectedFragments);

        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.setExpectedMessageCount(1);

        template.sendBody("direct:in", messageFragments);
        assertMockEndpointsSatisfied();

    }
}
