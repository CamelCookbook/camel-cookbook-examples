package org.camelcookbook.structuringroutes.vm;

import static junit.framework.Assert.assertFalse;
import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;

import org.camelcookbook.structuringroutes.vm.ExternalLoggingRouteBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * To test communication between Camel contexts in the same JVM, we are going to wire up the test my hand instead of
 * relying on CamelTestSupport.
 */
public class VmTest {

    private CamelContext testHarnessContext;
    private CamelContext externalLoggingContext;

    @Before
    public void setupContexts() throws Exception {
        testHarnessContext = new DefaultCamelContext();
        testHarnessContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:in")
                        .setHeader("harness.threadName", simple("${threadName}"))
                        .to("vm:logMessageToBackendSystem")
                        .log("Completed logging");
            }
        });
        testHarnessContext.start();

        externalLoggingContext = new DefaultCamelContext();
        externalLoggingContext.addRoutes(new ExternalLoggingRouteBuilder("vm"));
        externalLoggingContext.start();
    }

    @After
    public void shutdownContexts() throws Exception {
        testHarnessContext.stop();
        externalLoggingContext.stop();
    }

    @Test
    public void testMessagePassing() throws InterruptedException {
        ProducerTemplate producerTemplate = testHarnessContext.createProducerTemplate();

        MockEndpoint out = externalLoggingContext.getEndpoint("mock:out", MockEndpoint.class);
        out.setExpectedMessageCount(1);
        out.message(0).body().equals("logging: something happened");

        producerTemplate.sendBody("direct:in", "something happened");
        out.assertIsSatisfied(1000);
        Message message = out.getExchanges().get(0).getIn();
        assertFalse(message.getHeader("harness.threadName").equals(
                message.getHeader(ExternalLoggingRouteBuilder.LOGGING_THREAD_NAME)));

    }
}
