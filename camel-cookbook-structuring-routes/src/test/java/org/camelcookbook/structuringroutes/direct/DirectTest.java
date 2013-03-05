package org.camelcookbook.structuringroutes.direct;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class DirectTest extends CamelTestSupport {
    @Produce(uri = "direct:A")
    ProducerTemplate in;

    @EndpointInject(uri = "mock:endA")
    MockEndpoint endA;

    @EndpointInject(uri = "mock:endB")
    MockEndpoint endB;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new DirectRouteBuilder();
    }

    @Test
    public void testInOutMessage() throws Exception {
        String message = "hello";

        String expectedResponse = "A2[ B[ A1[ hello ] ] ]";
        endB.setExpectedMessageCount(1);
        endA.setExpectedMessageCount(1);
        endA.message(0).body().isEqualTo(expectedResponse);

        String response = (String) in.requestBody(message);
        assertEquals(expectedResponse, response);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testInOnlyMessage() throws Exception {
        String message = "hello";

        String expectedResponse = "A2[ B[ A1[ hello ] ] ]";
        endB.setExpectedMessageCount(1);
        endA.setExpectedMessageCount(1);

        in.sendBody(message);
        assertMockEndpointsSatisfied();
    }

}
