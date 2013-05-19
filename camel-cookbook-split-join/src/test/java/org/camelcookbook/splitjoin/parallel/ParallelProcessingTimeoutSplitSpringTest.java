package org.camelcookbook.splitjoin.parallel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class that demonstrates exception handling when processing split messages in parallel.
 * @author jkorab
 */
public class ParallelProcessingTimeoutSplitSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/META-INF/spring/parallelProcessingTimeoutSplit-context.xml");
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

        MockEndpoint mockDelayed = getMockEndpoint("mock:delayed");
        mockDelayed.setExpectedMessageCount(1);

        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.setExpectedMessageCount(1);


        template.sendBody("direct:in", messageFragments);
        assertMockEndpointsSatisfied();

    }
}
