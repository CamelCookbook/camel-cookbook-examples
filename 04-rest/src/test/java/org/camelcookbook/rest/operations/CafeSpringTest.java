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

package org.camelcookbook.rest.operations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.camelcookbook.rest.common.MenuItem;
import org.camelcookbook.rest.common.MenuService;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Collection;

public class CafeSpringTest extends CamelSpringTestSupport {
    private final int port1 = AvailablePortFinder.getNextAvailable();

    private ObjectWriter objectWriter = new ObjectMapper().writer();

    private MenuService getMenuService() {
        return context().getRegistry().lookupByNameAndType("menuService", MenuService.class);
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        System.setProperty("port1", String.valueOf(port1));

        return new ClassPathXmlApplicationContext("META-INF/spring/operations-context.xml");
    }

    @Test
    public void testGetAll() throws Exception {
        final String origValue = objectWriter.writeValueAsString(getMenuService().getMenuItems());

        String out = fluentTemplate().to("undertow:http://localhost:" + port1 + "/cafe/menu/items")
                .withHeader(Exchange.HTTP_METHOD, "GET")
                .request(String.class);

        assertEquals(origValue, out);
    }

    @Test
    public void testGetOne() throws Exception {
        final String origValue = objectWriter.writeValueAsString(getMenuService().getMenuItem(1));

        String out = fluentTemplate().to("undertow:http://localhost:" + port1 + "/cafe/menu/items/1")
                .withHeader(Exchange.HTTP_METHOD, "GET")
                .request(String.class);

        assertEquals(origValue, out);
    }

    @Test
    public void testGetInvalid() throws Exception {
        final int size = getMenuService().getMenuItems().size();

        try {
            // TODO: report camel-undertow not throwing exception on failure
            String out = fluentTemplate().to("netty4-http:http://localhost:" + port1 + "/cafe/menu/items/" + (size + 1))
                    .withHeader(Exchange.HTTP_METHOD, "GET")
                    .request(String.class);
        } catch (Exception e) {
            // Expect Exception to be thrown since we're requesting an item that does not exist
            //System.out.println("Exception Message = " + e.getMessage());
            return;
        }

        fail("Expected call to fail with exception thrown");
    }

    @Test
    public void testCreate() throws Exception {
        Collection<MenuItem> menuItems = getMenuService().getMenuItems();
        assertEquals(2, menuItems.size());

        MenuItem newItem = new MenuItem();
        newItem.setName("Test Item");
        newItem.setDescription("Test New Item Create");
        newItem.setCost(5);
        String newItemJson = objectWriter.writeValueAsString(newItem);

        Exchange outExchange = template().request("undertow:http://localhost:" + port1 + "/cafe/menu/items", new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        exchange.getIn().setBody(newItemJson);
                        exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
                    }
                }
        );

        assertEquals(201, outExchange.getOut().getHeader(Exchange.HTTP_RESPONSE_CODE));

        String out = outExchange.getOut().getBody(String.class);
        assertEquals("3", out);

        Collection<MenuItem> menuUpdateItems = getMenuService().getMenuItems();
        assertEquals(3, menuUpdateItems.size());

        MenuItem item3 = getMenuService().getMenuItem(3);
        assertEquals(3, item3.getId());
        assertEquals(newItem.getName(), item3.getName());
        assertEquals(newItem.getDescription(), item3.getDescription());
        assertEquals(newItem.getCost(), item3.getCost());
    }

    @Test
    public void testUpdate() throws Exception {
        Collection<MenuItem> menuItems = getMenuService().getMenuItems();
        assertEquals(2, menuItems.size());

        MenuItem origMenuItem2 = getMenuService().getMenuItem(2);

        MenuItem newItem = new MenuItem();
        newItem.setId(origMenuItem2.getId());
        newItem.setName("Test " + origMenuItem2.getName());
        newItem.setDescription("Test " + origMenuItem2.getDescription());
        newItem.setCost(origMenuItem2.getCost() + 1);

        assertNotEquals(origMenuItem2, newItem);

        String newItemJson = objectWriter.writeValueAsString(newItem);

        String out = fluentTemplate().to("undertow:http://localhost:" + port1 + "/cafe/menu/items/2")
                .withHeader(Exchange.HTTP_METHOD, "PUT")
                .withHeader(Exchange.CONTENT_ENCODING, "application/json")
                .withBody(newItemJson)
                .request(String.class);

        assertEquals(newItemJson, out);

        Collection<MenuItem> menuUpdateItems = getMenuService().getMenuItems();
        assertEquals(2, menuUpdateItems.size());

        MenuItem curItem2 = getMenuService().getMenuItem(2);
        assertEquals(2, curItem2.getId());
        assertEquals(newItem.getName(), curItem2.getName());
        assertEquals(newItem.getDescription(), curItem2.getDescription());
        assertEquals(newItem.getCost(), curItem2.getCost());
    }

    @Test
    public void testDelete() throws Exception {
        Collection<MenuItem> menuItems = getMenuService().getMenuItems();
        assertEquals(2, menuItems.size());

        fluentTemplate().to("undertow:http://localhost:" + port1 + "/cafe/menu/items/2")
                .withHeader(Exchange.HTTP_METHOD, "DELETE")
                .send();

        Collection<MenuItem> menuUpdateItems = getMenuService().getMenuItems();
        assertEquals(1, menuUpdateItems.size());
        assertEquals(menuItems.iterator().next(), menuUpdateItems.iterator().next());
    }
}