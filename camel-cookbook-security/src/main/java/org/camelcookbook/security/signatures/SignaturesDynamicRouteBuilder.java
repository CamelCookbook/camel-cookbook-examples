package org.camelcookbook.security.signatures;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.crypto.DigitalSignatureConstants;

/**
 * Demonstrates the use of public and private keys to digitally sign a message payload.
 */
public class SignaturesDynamicRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:sign_a")
            .to("crypto:sign://usingKeystore?keystore=#keyStore&alias=system_a&password=keyPasswordA")
            .setHeader("sendingSystem", constant("a"))
            .to("direct:verify");

        from("direct:sign_b")
             .to("crypto:sign://usingKeystore?keystore=#keyStore&alias=system_b&password=keyPasswordB")
             .setHeader("sendingSystem", constant("b"))
             .to("direct:verify");

        from("direct:verify")
            .log("Verifying message")
            .setHeader(DigitalSignatureConstants.KEYSTORE_ALIAS,
                    simple("system_${header[sendingSystem]}"))
            .to("crypto:verify//usingKeystore?keystore=#trustStore")
            .log("Message verified")
            .to("mock:verified");
    }

}
