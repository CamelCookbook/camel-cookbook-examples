package org.camelcookbook.parallelprocessing.threadpoolprofiles;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.ThreadPoolBuilder;
import org.apache.camel.builder.ThreadPoolProfileBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spi.ThreadPoolProfile;
import org.apache.camel.support.SynchronizationAdapter;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.camelcookbook.parallelprocessing.threadpools.CustomThreadPoolInlineRouteBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Test class that exercises a custom thread pool created from a profile using the threads DSL.
 * @author jkorab
 */
public class CustomThreadPoolProfileTest extends CamelTestSupport {

    @Override
    public RouteBuilder createRouteBuilder() {
        return new CustomThreadPoolProfileRouteBuilder();
    }

    @Test
    public void testProcessedByCustomThreadPool() throws InterruptedException {
        final int messageCount = 50;
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.setExpectedMessageCount(messageCount);
        mockOut.setResultWaitTime(6000);

        for (int i = 0; i < messageCount; i++) {
            template.asyncSendBody("direct:in", "Message[" + i + "]");
        }

        assertMockEndpointsSatisfied();
        // no way to check programatically whether the profile was actually engaged, as Camel uses the
        // default naming strategy for threads
    }

}
