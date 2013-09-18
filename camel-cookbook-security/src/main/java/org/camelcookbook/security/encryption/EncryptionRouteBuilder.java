package org.camelcookbook.security.encryption;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.crypto.CryptoDataFormat;
import org.apache.commons.lang.Validate;

import java.security.Key;

/**
 * Demonstrates the use of a shared secret key to encrypt and decrypt a message.
 */
public class EncryptionRouteBuilder extends RouteBuilder {

    private final Key sharedKey;

    public EncryptionRouteBuilder(Key sharedKey) {
        Validate.notNull(sharedKey, "sharedKey is null");
        this.sharedKey = sharedKey;
    }

    @Override
    public void configure() throws Exception {
        CryptoDataFormat sharedKeyCrypto = new CryptoDataFormat("DES", sharedKey);

        from("direct:encrypt")
            .log("Encrypting message")
            .marshal(sharedKeyCrypto)
            .log("Message encrypted: ${body}")
            .to("direct:decrypt");

        from("direct:decrypt")
            .log("Decrypting message")
            .unmarshal(sharedKeyCrypto)
            .log("Message decrypted: ${body}")
            .to("mock:decrypted");
    }
}