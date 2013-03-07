package org.camelcookbook.structuringroutes.vm;

import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang.Validate;

/**
 * RouteBuilder that logs exchanges to a fictitious backend system. The endpoint scheme is
 * injected into this class to allow us to use it through VM and Direct-VM to highlight the differences.
 */
public class ExternalLoggingRouteBuilder extends RouteBuilder {
    public static final String LOG_MESSAGE_TO_BACKEND_SYSTEM = "logMessageToBackendSystem";
    public static final String LOGGING_THREAD_NAME = "logging.threadName";

    private final String logMessageSourceUri;

    public ExternalLoggingRouteBuilder(String endpointScheme) {
        Validate.notEmpty(endpointScheme, "endpointScheme is null or empty");
        this.logMessageSourceUri = endpointScheme + ":" + LOG_MESSAGE_TO_BACKEND_SYSTEM;
    }

    @Override
    public void configure() throws Exception {
        from(logMessageSourceUri)
            .setHeader(LOGGING_THREAD_NAME, simple("${threadName}"))
            .delay(1000)
            .log("Logged message to backend system ${body} by thread[${threadName}] ")
            .to("mock:out");
    }
}
