package org.camelcookbook.examples.testing.blueprint;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Dictionary;

/**
 * Test class that demonstrates overriding properties in a Blueprint environment.
 */
public class PropertyOverrideEtcBlueprintTest extends CamelBlueprintTestSupport {

    @Produce(uri = "direct:in")
    private ProducerTemplate producerTemplate;

    @EndpointInject(uri = "mock:out")
    private MockEndpoint mockOut;

    @Override
    protected String getBlueprintDescriptor() {
        return "/OSGI-INF/blueprint/simpleTransform-context.xml," +
                "/OSGI-INF/blueprint/simpleTransform-props-context.xml";
    }

    @Override
    protected String[] loadConfigAdminConfigurationFile() {
        return new String[] {
            "src/test/resources/blueprint/org.camelcookbook.blueprint.cfg",
            "org.camelcookbook.testing" // Persistent ID
        };
    }

    @Test
    public void testPayloadIsTransformed() throws InterruptedException {
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("etc: Cheese");

        producerTemplate.sendBody("Cheese");

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testPayloadIsTransformedAgain() throws InterruptedException {
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("etc: Foo");

        producerTemplate.sendBody("Foo");

        assertMockEndpointsSatisfied();
    }

}
