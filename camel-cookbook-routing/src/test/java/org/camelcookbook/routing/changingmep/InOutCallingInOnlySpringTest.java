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

package org.camelcookbook.routing.changingmep;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class InOutCallingInOnlySpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("spring/changingMep-inOutCallingInOnly-context.xml");
    }

    @Produce(uri = "direct:in")
    protected ProducerTemplate template;

    @EndpointInject(uri = "mock:oneWay")
    private MockEndpoint oneWay;

    @EndpointInject(uri = "mock:afterOneWay")
    private MockEndpoint afterOneWay;

    @Test
    public void testMEPChangedForOneWay() throws InterruptedException {
        String messageBody = "message";

        oneWay.setExpectedMessageCount(1);
        oneWay.message(0).exchangePattern().isEqualTo(ExchangePattern.InOnly);
        afterOneWay.setExpectedMessageCount(1);
        afterOneWay.message(0).exchangePattern().isEqualTo(ExchangePattern.InOut);

        template.requestBody(messageBody);

        assertMockEndpointsSatisfied();
        Exchange oneWayExchange = oneWay.getReceivedExchanges().get(0);
        Exchange afterOneWayExchange = afterOneWay.getReceivedExchanges().get(0);
        // these are not the same exchange objects
        assertNotEquals(oneWayExchange, afterOneWayExchange);

        // the bodies should be the same - shallow copy
        assertEquals(oneWayExchange.getIn().getBody(), afterOneWayExchange.getIn().getBody());

        // the transactions are the same
        assertEquals(oneWayExchange.getUnitOfWork(), afterOneWayExchange.getUnitOfWork());
    }

}
