package org.camelcookbook.structuringroutes.templating;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class OrderProcessingRouteBuilderTest extends CamelTestSupport {
    @Produce(uri = "direct:in")
    ProducerTemplate in;

    @EndpointInject(uri = "mock:out")
    MockEndpoint out;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        OrderFileNameProcessor orderFileNameProcessor = new OrderFileNameProcessor();
        orderFileNameProcessor.setCountryDateFormat("dd-MM-yyyy");

        OrderProcessingRouteBuilder routeBuilder = new OrderProcessingRouteBuilder();
        routeBuilder.inputUri = "direct:in";
        routeBuilder.outputUri = "mock:out";
        routeBuilder.setOrderFileNameProcessor(orderFileNameProcessor);

        return routeBuilder;
    }

    @Test
    public void testRoutingLogic() throws InterruptedException {
        out.setExpectedMessageCount(1);
        out.message(0).body().startsWith("2013-11-23");
        out.message(0).header("CamelFileName").isEqualTo("2013-11-23.csv");

        in.sendBody("23-11-2013,1,Geology rocks t-shirt");

        assertMockEndpointsSatisfied();
    }
}
