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

package org.camelcookbook.rest.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.camelcookbook.rest.common.MenuService;
import org.junit.Test;

public class CafeErrorTest extends CamelTestSupport {
    private final int port1 = AvailablePortFinder.getNextAvailable();

    private ObjectWriter objectWriter = new ObjectMapper().writer();

    private MenuService getMenuService() {
        return context().getRegistry().lookupByNameAndType("menuService", MenuService.class);
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();

        registry.bind("menuService", new MenuService());

        return registry;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new CafeErrorRoute(port1);
    }

    @Test
    public void testInvalidJson() throws Exception {
        try {
            String out = fluentTemplate().to("undertow:http://localhost:" + port1 + "/cafe/menu/items/1")
                    .withHeader(Exchange.HTTP_METHOD, "PUT")
                    .withBody("This is not JSON format")
                    .request(String.class);

            fail("Expected exception to be thrown");
        } catch (CamelExecutionException e) {
            HttpOperationFailedException httpException = assertIsInstanceOf(HttpOperationFailedException.class, e.getCause());

            assertEquals(400, httpException.getStatusCode());
            assertEquals("text/plain", httpException.getResponseHeaders().get(Exchange.CONTENT_TYPE));
            assertEquals("Invalid json data", httpException.getResponseBody());
        }
    }

    @Test
    public void testGetInvalid() throws Exception {
        final int size = getMenuService().getMenuItems().size();

        Exchange exchange = template().request("undertow:http://localhost:" + port1 + "/cafe/menu/items/" + (size + 1), new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setBody(null);
                exchange.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
            }
        });

        assertEquals(404, exchange.getOut().getHeader(Exchange.HTTP_RESPONSE_CODE));
    }
}