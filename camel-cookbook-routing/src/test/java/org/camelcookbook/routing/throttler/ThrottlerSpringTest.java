/*
 * Copyright (C) Scott Cranton and Jakub Korab
 * https://github.com/CamelCookbook
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camelcookbook.routing.throttler;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.ShutdownStrategy;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

public class ThrottlerSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("spring/throttler-context.xml");
    }

    @Test
    public void testThrottle() throws Exception {
        final MockEndpoint mockEndpointUnthrottled = getMockEndpoint("mock:unthrottled");
        mockEndpointUnthrottled.expectedMessageCount(10);
        mockEndpointUnthrottled.setResultWaitTime(1000);

        final MockEndpoint mockEndpointThrottled = getMockEndpoint("mock:throttled");
        mockEndpointThrottled.expectedMessageCount(5);
        mockEndpointThrottled.setResultWaitTime(1000);

        final MockEndpoint mockEndpointAfter = getMockEndpoint("mock:after");
        mockEndpointAfter.expectedMessageCount(5);
        mockEndpointAfter.setResultWaitTime(1000);

        // Send the message on separate threads as sendBody will block on the throttler
        for (int i = 0; i < 10; i++) {
            new Thread (new Runnable() {
                @Override
                public void run() {
                    template.sendBody("direct:start", "Camel Rocks");
                }
            }).start();
        }

        assertMockEndpointsSatisfied();
    }
}
