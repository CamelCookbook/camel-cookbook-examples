package org.camelcookbook.parallelprocessing.threadpoolprofiles;

import org.apache.camel.Exchange;
import org.apache.camel.Navigate;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.processor.ThreadsProcessor;
import org.apache.camel.support.SynchronizationAdapter;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Test class that exercises a custom thread pool created from a profile using the threads DSL.
 * @author jkorab
 */
public class CustomThreadPoolProfilesSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/META-INF/spring/threadPoolProfiles-context.xml");
    }

    @Test
    public void testProcessedByCustomThreadPoolProfile() throws InterruptedException {
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
