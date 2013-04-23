package org.camelcookbook.examples.testing.spring;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * Test class that demonstrates the fundamental interactions going on to verify that a route behaves as it should.
 */
@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration({"/spring/test-properties-context.xml",
        "/META-INF/spring/simpleTransform-context.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SimpleTransformEnhancedSpringTest {

    @Autowired
    private CamelContext camelContext;

    @Produce(uri = "direct:in")
    private ProducerTemplate producerTemplate;

    @EndpointInject(uri = "mock:out")
    private MockEndpoint mockOut;

    @Test
    public void testPayloadIsTransformed() throws InterruptedException {
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("Modified: Cheese");

        producerTemplate.sendBody("Cheese");

        MockEndpoint.assertIsSatisfied(camelContext);
    }

    @Test
    public void testPayloadIsTransformedAgainAndMocksWorkCorrectly() throws InterruptedException {
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("Modified: Foo");

        producerTemplate.sendBody("Foo");

        MockEndpoint.assertIsSatisfied(camelContext);
    }
}
