package org.camelcookbook.security.encryption;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.crypto.CryptoDataFormat;
import org.apache.camel.spi.Registry;


import java.security.Key;

/**
 * Demonstrates the use of a shared secret key to encrypt and decrypt a message.
 */
public class EncryptionDynamicRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        final CryptoDataFormat crypto = new CryptoDataFormat("DES", null);

        from("direct:encrypt")
             .process(new Processor() {
                 @Override
                 public void process(Exchange exchange) throws Exception {
                     Registry registry = exchange.getContext().getRegistry();
                     Message in = exchange.getIn();
                     Key key = registry.lookupByNameAndType("shared_" + in.getHeader("system"), Key.class);
                     in.setHeader(CryptoDataFormat.KEY, key);
                 }
             })
            .log("Encrypting message: ${body} using ${header[CamelCryptoKey]}")
            .marshal(crypto)
            .log("Message encrypted: ${body}")
            .to("direct:decrypt");

        from("direct:decrypt")
            .log("Decrypting message: ${body} using ${header[CamelCryptoKey]}")
            .unmarshal(crypto)
            .log("Message decrypted: ${body}")
            .to("mock:decrypted");
    }
}