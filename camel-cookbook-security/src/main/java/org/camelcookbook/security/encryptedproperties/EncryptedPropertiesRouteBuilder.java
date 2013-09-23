package org.camelcookbook.security.encryptedproperties;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.crypto.CryptoDataFormat;
import org.apache.commons.lang.Validate;

import java.security.Key;

/**
 * Demonstrates the use of encrypted properties in Camel routes.
 */
public class EncryptedPropertiesRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("{{start.endpoint}}")
            .setHeader("dbPassword", simple("{{database.password}}"))
            .log("{{log.message}}")
            .to("{{end.endpoint}}");

    }
}