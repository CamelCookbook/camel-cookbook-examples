package org.camelcookbook.security.signatures;

import org.apache.camel.*;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Test;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.crypto.DigitalSignatureComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyStore;
import java.security.SignatureException;

/**
 * Demonstrates the use of public and private keys to digitally sign a message payload.
 */
public class SignaturesTest extends CamelTestSupport {

    private final Logger log = LoggerFactory.getLogger(SignaturesTest.class);

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new SignaturesRouteBuilder();
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        final String keyStorePassword = "keystorePassword";
        final String trustStorePassword = "truststorePassword";


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
        return camelContext;
    }

    @Test
    public void testMessageSigning() throws InterruptedException {
        MockEndpoint mockVerified = getMockEndpoint("mock:verified");
        mockVerified.setExpectedMessageCount(1);

        template.sendBody("direct:sign", "foo");

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testMessageModificationAfterSigning() throws InterruptedException {
        MockEndpoint mockSigned = getMockEndpoint("mock:signed");
        mockSigned.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Message in = exchange.getIn();
                in.setBody(in.getBody(String.class) + "modified");
            }
        });

        MockEndpoint mockVerified = getMockEndpoint("mock:verified");
        mockVerified.setExpectedMessageCount(0);

        try {
            template.sendBody("direct:sign", "foo");
            fail();
        } catch (CamelExecutionException cex) {
            assertTrue(ExceptionUtils.getRootCause(cex) instanceof SignatureException);
            assertEquals("SignatureException: Cannot verify signature of exchange",
                    ExceptionUtils.getRootCauseMessage(cex));
        }

        assertMockEndpointsSatisfied();
    }

}
