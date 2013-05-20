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
public class ParallelProcessingExceptionHandlingSplitTest extends CamelTestSupport {

    @Override
    public RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:in")
                    .split(body()).parallelProcessing().stopOnException()
                        .log("Processing message[${property.CamelSplitIndex}]")
                        .to("direct:failOn20th")
                    .end()
                    .to("mock:out");

                from("direct:failOn20th")
                    .choice()
                        .when(simple("${property.CamelSplitIndex} == 20"))
                            .throwException(new IllegalStateException("boom"))
                        .otherwise()
                            .to("mock:split")
                    .endChoice();
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

        int indexOnWhichExceptionThrown = 20;
        MockEndpoint mockSplit = getMockEndpoint("mock:split");
        mockSplit.setMinimumExpectedMessageCount(indexOnWhichExceptionThrown);

        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.setExpectedMessageCount(0);

        try {
            template.sendBody("direct:in", messageFragments);
            fail();
        } catch (Exception e) {
            assertMockEndpointsSatisfied();
        }

    }
}
