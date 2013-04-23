package org.camelcookbook.examples.testing.dataset;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.camelcookbook.examples.testing.java.SimpleTransformDIRouteBuilder;
import org.junit.Test;

/**
 * Test class that demonstrates using the DataSet component to load test a route.
 */
public class SimpleTransformDIRouteBuilderLoadTest extends CamelTestSupport {
    @Override
    protected CamelContext createCamelContext() throws Exception {
        SimpleRegistry registry = new SimpleRegistry();

        InputDataSet inputDataSet = new InputDataSet();
        inputDataSet.setSize(1000);
        //inputDataSet.setReportCount(100);
        //inputDataSet.setDefaultHeaders(Map<String, Object>);
        // if not defined, implementation can override
        // void populateDefaultHeaders(Map<String, Object> map)
        // once headers have been set the following method is called, which can be overridden to customize the headers on a per message basis
        // void applyHeaders(Exchange exchange, long messageIndex)

        //inputDataSet.setOutputTransformer(Processor); - modify the exchange after its headers and body have been set
        registry.put("input", inputDataSet);

        ExpectedOutputDataSet expectedOutputDataSet = new ExpectedOutputDataSet();
        expectedOutputDataSet.setSize(1000);
        registry.put("expectedOutput", expectedOutputDataSet);

        CamelContext context = new DefaultCamelContext(registry);
        return context;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        SimpleTransformDIRouteBuilder routeBuilder = new SimpleTransformDIRouteBuilder();
        routeBuilder.setSourceUri("dataset:input?produceDelay=1");
        routeBuilder.setTargetUri("dataset:expectedOutput");
        return routeBuilder;
    }

    @Test
    public void testPayloadIsTransformed() throws InterruptedException {
        // A DataSetEndpoint is a sub-class of MockEndpoint that sets up expectations based on
        // the messages created, and the size property on the object.
        // All that is needed for us to test this route is to assert that the endpoint was satisfied.
        MockEndpoint expectedOutput = getMockEndpoint("dataset:expectedOutput");
        expectedOutput.assertIsSatisfied();
    }
}
