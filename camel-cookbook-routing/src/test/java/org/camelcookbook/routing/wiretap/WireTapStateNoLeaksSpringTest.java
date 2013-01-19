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

package org.camelcookbook.routing.wiretap;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.camelcookbook.routing.model.Cheese;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class WireTapStateNoLeaksSpringTest extends CamelSpringTestSupport {

    @Produce(uri = "direct:in")
    protected ProducerTemplate template;

    @EndpointInject(uri = "mock:tapped")
    private MockEndpoint tapped;

    @EndpointInject(uri = "mock:out")
    private MockEndpoint out;

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("spring/wireTap-stateNoLeaks-context.xml");
    }

    @Test
    public void testOutMessageUnaffectedByTappedRoute() throws InterruptedException {
        Cheese cheese = new Cheese();
        cheese.setAge(1);

        tapped.setExpectedMessageCount(1);
        out.setExpectedMessageCount(1);

        template.sendBody(cheese);

        tapped.setResultWaitTime(1000);
        // check that the endpoints both received the same message
        tapped.assertIsSatisfied();
        out.assertIsSatisfied();

        out.expectedBodyReceived().equals(cheese);
        Cheese outCheese = out.getExchanges().get(0).getIn().getBody(Cheese.class);
        Assert.assertEquals(1, outCheese.getAge());

        Cheese tappedCheese = tapped.getExchanges().get(0).getIn().getBody(Cheese.class);
        Assert.assertEquals(2, tappedCheese.getAge());
    }

}
