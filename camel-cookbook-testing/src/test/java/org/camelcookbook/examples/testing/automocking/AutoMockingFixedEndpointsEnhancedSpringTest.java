package org.camelcookbook.examples.testing.automocking;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringJUnit4ClassRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration({"/META-INF/spring/fixedEndpoints-context.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpoints("activemq:out")
public class AutoMockingFixedEndpointsEnhancedSpringTest {

    @Produce(uri = "activemq:in")
    ProducerTemplate in;

    @EndpointInject(uri = "mock:activemq:out")
    MockEndpoint mockOut;

    @Test
    public void testTransformationThroughAutoMock() throws Exception {
        mockOut.expectedBodiesReceived("Modified: testMessage");
        in.sendBody("testMessage");
        mockOut.assertIsSatisfied();
    }
}
