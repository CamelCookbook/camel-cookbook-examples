package org.camelcookbook.structuringroutes.seda;

import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SedaTimerSpringTest extends CamelSpringTestSupport {

    @EndpointInject(uri = "mock:out")
    MockEndpoint out;

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/seda-timer-context.xml");
    }

    @Test
    public void testLoadBalancing() throws Exception {
        final int pingCount = 10;

        out.setMinimumExpectedMessageCount(pingCount);
        Thread.sleep((pingCount * SedaTimerRouteBuilder.TIMER_PERIOD) + LongRunningProcessor.DELAY_TIME);
        out.assertIsSatisfied();
    }

}

