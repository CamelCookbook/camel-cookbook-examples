package org.camelcookbook.examples.testing.automocking;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author jkorab
 */
public class AutoMockingFixedEndpointsSpringTest extends CamelSpringTestSupport {

    @Produce(uri = "activemq:in")
    ProducerTemplate in;

    @EndpointInject(uri = "mock:activemq:out")
    MockEndpoint mockOut;

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/META-INF/spring/fixedEndpoints-context.xml");
    }

    @Override
    public String isMockEndpoints() {
        return "activemq:out";
    }

    @Test
    public void testTransformationThroughAutoMock() throws Exception {
        mockOut.expectedBodiesReceived("Modified: testMessage");
        in.sendBody("testMessage");
        mockOut.assertIsSatisfied();
    }
}
