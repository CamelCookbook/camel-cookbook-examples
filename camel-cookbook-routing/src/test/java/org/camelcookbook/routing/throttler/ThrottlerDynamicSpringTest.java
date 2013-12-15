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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ThrottlerDynamicSpringTest extends CamelSpringTestSupport {
    private static final Logger LOG = LoggerFactory.getLogger(ThrottlerDynamicSpringTest.class);

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/throttler-dynamic-context.xml");
    }

    @Test
    public void testThrottleDynamic() throws Exception {
        final int throttleRate = 3;
        final int messageCount = throttleRate + 2;

        getMockEndpoint("mock:unthrottled").expectedMessageCount(messageCount);
        getMockEndpoint("mock:throttled").expectedMessageCount(throttleRate);
        getMockEndpoint("mock:after").expectedMessageCount(throttleRate);

        ExecutorService executor = Executors.newFixedThreadPool(messageCount);

        // Send the message on separate threads as sendBody will block on the throttler
        final AtomicInteger threadCount = new AtomicInteger(0);
        for (int i = 0; i < messageCount; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    template.sendBodyAndHeader("direct:start", "Camel Rocks", "ThrottleRate", throttleRate);

                    final int threadId = threadCount.incrementAndGet();
                    LOG.info("Thread {} finished", threadId);
                }
            });
        }

        assertMockEndpointsSatisfied();

        LOG.info("Threads completed {} of {}", threadCount.get(), messageCount);

        //TODO: fix race condition in following assertion
        //assertEquals("Threads completed should equal throttle rate", throttleRate, threadCount.get());

        executor.shutdownNow();
    }
}
