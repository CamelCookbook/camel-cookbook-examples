package org.camelcookbook.parallelprocessing.endpointconsumers;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.camelcookbook.parallelprocessing.endpointConsumers.EndpointConsumersSedaRouteBuilder;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Test class that exercises parallel threads in Seda.
 * @author jkorab
 */
public class EndpointConsumersSedaTest extends CamelTestSupport {

    @Override
    public RouteBuilder createRouteBuilder() {
        return new EndpointConsumersSedaRouteBuilder();
    }

    @Test
    public void testParallelConsumption() throws InterruptedException {
        final int messageCount = 100;
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.setExpectedMessageCount(messageCount);
        mockOut.setResultWaitTime(5000);

        for (int i = 0; i < messageCount; i++) {
            template.sendBody("seda:in", "Message[" + i + "]");
        }

        assertMockEndpointsSatisfied();
    }

}
