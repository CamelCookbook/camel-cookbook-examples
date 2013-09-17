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
            .to("crypto:sign://usingKeystore?keystore=#keyStore&alias=scott&password=camelkeypass&algorithm=SHA1withRSA")
            .log("Message signed")
            .to("mock:signed")
            .to("direct:verify");

        from("direct:verify")
            .to("crypto:verify//usingKeystore?keystore=#trustStore&alias=scott&algorithm=SHA1withRSA")
            .to("mock:verified");
    }

}
