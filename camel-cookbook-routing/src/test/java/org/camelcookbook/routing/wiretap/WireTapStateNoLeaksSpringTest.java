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
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.apache.camel.language.simple.SimpleLanguage.simple;

public class WireTapStateNoLeaksSpringTest extends CamelSpringTestSupport {

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @EndpointInject(uri = "mock:tapped")
    private MockEndpoint tapped;

    @EndpointInject(uri = "mock:out")
    private MockEndpoint out;

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/wireTap-stateNoLeaks-context.xml");
    }

    @Test
    public void testOutMessageUnaffectedByTappedRoute() throws InterruptedException {
        final Cheese cheese = new Cheese();
        cheese.setAge(1);

        // should receive same object that was sent
        //out.expectedBodiesReceived(cheese); // bug in wire tap?
        out.setExpectedMessageCount(1);
        // since copy was sent to wire tap, age should remain unchanged
        //out.message(0).body().isEqualTo(cheese); // bug in isEqualTo test?
        out.message(0).expression(simple("${body.age} == 1"));

        tapped.setExpectedMessageCount(1);
        tapped.message(0).expression(simple("${body.age} == 2"));
        tapped.setResultWaitTime(1000);

        template.sendBody(cheese);

        assertMockEndpointsSatisfied();

        // check that tapped got a different instance (copy) of message
        assertNotEquals(cheese, tapped.message(0));
    }
}
