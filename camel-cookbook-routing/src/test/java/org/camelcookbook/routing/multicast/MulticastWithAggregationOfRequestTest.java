package org.camelcookbook.routing.multicast;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class MulticastWithAggregationOfRequestTest extends CamelTestSupport {

    public static final String MESSAGE_BODY = "Message to be multicast";

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new MulticastWithAggregationOfRequestRouteBuilder();
    }

    @Produce(uri = "direct:in")
    protected ProducerTemplate template;

    @Test
    public void testAggregationOfResponsesFromMulticast() throws InterruptedException {
        String response = (String) template.requestBody(MESSAGE_BODY);
        assertEquals("Message to be multicast,first response,second response", response);
    }

}
