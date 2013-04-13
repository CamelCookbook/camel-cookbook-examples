package org.camelcookbook.examples.testing.java;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class that demonstrates the fundamental interactions going on to verify that a route behaves as it should.
 */
public class FirstPrinciplesRouteBuilderTest {

    private CamelContext camelContext;

    @Before
    public void setUpContext() throws Exception {
        this.camelContext = new DefaultCamelContext();
        camelContext.addRoutes(new SimpleTransformRouteBuilder());
        camelContext.start();
    }

    @After
    public void cleanUpContext() throws Exception {
        camelContext.stop();
    }

    @Test
    public void testPayloadIsTransformed() throws InterruptedException {
        MockEndpoint out = camelContext.getEndpoint("mock:out", MockEndpoint.class);

        out.setExpectedMessageCount(1);
        out.message(0).body().isEqualTo("Modified: Cheese");

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        producerTemplate.sendBody("direct:in", "Cheese");

        out.assertIsSatisfied();
    }
}
