package org.camelcookbook.structuringroutes.seda;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class SedaTimerTest extends CamelTestSupport {
    @EndpointInject(uri = "mock:out")
    MockEndpoint out;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new SedaTimerRouteBuilder();
    }

    @Test
    public void testLoadBalancing() throws Exception {
        final int pingCount = 10;

        out.setMinimumExpectedMessageCount(pingCount);
        Thread.sleep((pingCount * SedaTimerRouteBuilder.TIMER_PERIOD) + LongRunningProcessor.DELAY_TIME);
        out.assertIsSatisfied();
    }

}
