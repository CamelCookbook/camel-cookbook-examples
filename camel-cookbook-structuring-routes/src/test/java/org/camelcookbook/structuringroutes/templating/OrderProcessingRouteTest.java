/*
 * Copyright (C) Scott Cranton, Jakub Korab, and Christian Posta
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

package org.camelcookbook.structuringroutes.templating;

import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class OrderProcessingRouteTest extends CamelTestSupport {
    public static final String ID = "testOrders";

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        OrderFileNameProcessor orderFileNameProcessor = new OrderFileNameProcessor();
        orderFileNameProcessor.setCountryDateFormat("dd-MM-yyyy");

        OrderProcessingRoute routeBuilder = new OrderProcessingRoute();
        routeBuilder.setId(ID);
        routeBuilder.setInputDirectory("input");
        routeBuilder.setOutputDirectory("output");
        routeBuilder.setOrderFileNameProcessor(orderFileNameProcessor);

        return routeBuilder;
    }

    @Test
    public void testRoutingLogic() throws Exception {
        context.getRouteDefinition(ID)
                .adviceWith(context, new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith("direct:in");

                        interceptSendToEndpoint("file://output")
                                .skipSendToOriginalEndpoint()
                                .to("mock:out");
                    }
                });
        context.start();

        final MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.setExpectedMessageCount(1);
        mockOut.message(0).body().startsWith("2013-11-23");
        mockOut.message(0).header(Exchange.FILE_NAME).isEqualTo("2013-11-23.csv");

        fluentTemplate().to("direct:in").withBody("23-11-2013,1,Geology rocks t-shirt").send();

        assertMockEndpointsSatisfied();
    }
}
