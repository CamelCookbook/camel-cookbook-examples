package org.camelcookbook.examples.testing.blueprint;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.blueprint.CamelBlueprintTestSupport;
import org.junit.Test;

import java.util.Dictionary;

/**
 * Test class that demonstrates overriding properties in a Blueprint environment.
 */
public class PropertyOverrideBlueprintTest extends CamelBlueprintTestSupport {

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
    protected String useOverridePropertiesWithConfigAdmin(Dictionary props) throws Exception {
        props.put("transform.message", "Overridden");
        return "org.camelcookbook.testing";
    }

    @Test
    public void testPayloadIsTransformed() throws InterruptedException {
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("Overridden: Cheese");

        producerTemplate.sendBody("Cheese");

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testPayloadIsTransformedAgain() throws InterruptedException {
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().isEqualTo("Overridden: Foo");

        producerTemplate.sendBody("Foo");

        assertMockEndpointsSatisfied();
    }

}
