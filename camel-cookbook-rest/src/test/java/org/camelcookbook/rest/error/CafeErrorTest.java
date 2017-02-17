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
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.common.HttpOperationFailedException;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.camelcookbook.rest.common.MenuItem;
import org.camelcookbook.rest.common.MenuService;
import org.camelcookbook.rest.operations.CafeRouteBuilder;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;

public class CafeErrorTest extends CamelTestSupport {
    private final int port1 = AvailablePortFinder.getNextAvailable();

    private MenuService menuService;
    private ObjectWriter objectWriter;

    @Override
    public void doPreSetup() throws Exception {
        menuService = new MenuService();
        objectWriter = new ObjectMapper().writer();
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();

        registry.bind("menuService", menuService);

        return registry;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new CafeErrorRouteBuilder(port1);
    }

    @Test
    @Ignore
    public void testInvalidJson() throws Exception {
        // TODO: clean up

        String out = template.requestBodyAndHeader("http://localhost:" + port1 + "/cafe/menu/update", "This is not JSON format", Exchange.HTTP_METHOD, "PUT", String.class);
    }

    @Test
    public void testGetInvalid() throws Exception {
        final int size = menuService.getMenuItems().size();

        // TODO: clean up

        Exchange exchange = template.request("http://localhost:" + port1 + "/cafe/menu/" + (size + 1), new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setBody(null);
                        exchange.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
                    }
                });

        HttpOperationFailedException exception = exchange.getException(HttpOperationFailedException.class);

        System.out.println("Message " + exception.getResponseBody());
        assertEquals(400, exception.getStatusCode());
    }
}