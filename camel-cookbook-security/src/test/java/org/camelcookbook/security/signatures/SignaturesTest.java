package org.camelcookbook.security.signatures;

import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.crypto.DigitalSignatureComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyStore;

/**
 * @author jkorab
 */
public class SignaturesTest extends CamelTestSupport {

    private Logger log = LoggerFactory.getLogger(SignaturesTest.class);

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new SignaturesRouteBuilder();
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        String keyStorePassword = "camelstorepass";
        String trustStorePassword = "camelstorepass";


        SimpleRegistry registry = new SimpleRegistry();

        KeyStore keyStore = KeyStore.getInstance("JKS"); // Java keystore

        ClassLoader classLoader = getClass().getClassLoader();
        log.info("Loading keystore from [{}]", classLoader.getResource("keystore.jks").toString());
        keyStore.load(classLoader.getResourceAsStream("keystore.jks"), keyStorePassword.toCharArray());
        registry.put("keyStore", keyStore);

        KeyStore trustStore = KeyStore.getInstance("JKS"); // Java keystore
        trustStore.load(classLoader.getResourceAsStream("truststore.jks"), trustStorePassword.toCharArray());
        registry.put("trustStore", trustStore);

        CamelContext camelContext = new DefaultCamelContext(registry);

        DigitalSignatureComponent digitalSignatureComponent = new DigitalSignatureComponent();
        camelContext.addComponent("crypto", digitalSignatureComponent);

        return camelContext;
    }

    @Test
    public void testMessageSigning() throws InterruptedException {
        MockEndpoint mockVerified = getMockEndpoint("mock:verified");
        mockVerified.setExpectedMessageCount(1);

        template.sendBody("direct:sign", "foo");

        assertMockEndpointsSatisfied();
    }
}
