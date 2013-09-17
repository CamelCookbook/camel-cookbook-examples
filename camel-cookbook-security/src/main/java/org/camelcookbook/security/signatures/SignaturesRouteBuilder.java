package org.camelcookbook.security.signatures;

import org.apache.camel.builder.RouteBuilder;

/**
 * Demonstrates the use of public and private keys to digitally sign a message payload.
 */
public class SignaturesRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:sign")
            .log("Signing message")
            .to("crypto:sign://usingKeystore?keystore=#keyStore&alias=system_a&password=keyPasswordA")
            .log("Message signed")
            .to("mock:signed")
            .to("direct:verify");

        from("direct:verify")
            .log("Verifying message")
            .to("crypto:verify//usingKeystore?keystore=#trustStore&alias=system_a")
            .log("Message verified")
            .to("mock:verified");
    }

}
