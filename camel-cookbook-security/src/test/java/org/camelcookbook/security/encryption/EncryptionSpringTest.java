package org.camelcookbook.security.encryption;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Demonstrates the use of a shared secret key to encrypt and decrypt a message.
 */
public class EncryptionSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/encryption-context.xml");
    }

    @Test
    public void testMessageEncryption() throws InterruptedException {
        MockEndpoint mockDecrypted = getMockEndpoint("mock:decrypted");
        mockDecrypted.setExpectedMessageCount(1);
        mockDecrypted.message(0).body().isEqualTo("foo");

        template.sendBody("direct:encrypt", "foo");

        assertMockEndpointsSatisfied();
    }


}
