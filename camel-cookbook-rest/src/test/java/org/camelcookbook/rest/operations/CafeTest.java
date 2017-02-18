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
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.camelcookbook.rest.common.MenuItem;
import org.camelcookbook.rest.common.MenuService;
import org.junit.Test;

import java.util.Collection;

public class CafeTest extends CamelTestSupport {
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
        return new CafeRouteBuilder(port1);
    }

    @Test
    public void testGetAll() throws Exception {
        final String origValue = objectWriter.writeValueAsString(menuService.getMenuItems());

        String out = template.requestBodyAndHeader("http://localhost:" + port1 + "/cafe/menu/items", null, Exchange.HTTP_METHOD, "GET", String.class);

        assertEquals(origValue, out);
    }

    @Test
    public void testGetOne() throws Exception {
        final String origValue = objectWriter.writeValueAsString(menuService.getMenuItem(1));

        String out = template.requestBodyAndHeader("http://localhost:" + port1 + "/cafe/menu/items/1", null, Exchange.HTTP_METHOD, "GET", String.class);

        assertEquals(origValue, out);
    }

    @Test
    public void testGetInvalid() throws Exception {
        final int size = menuService.getMenuItems().size();

        try {
            String out = template.requestBodyAndHeader("http://localhost:" + port1 + "/cafe/menu/items/" + (size + 1), null, Exchange.HTTP_METHOD, "GET", String.class);
        } catch (Exception e) {
            System.out.println("Exception Message = " + e.getMessage());
            return;
        }

        fail("Expected call to fail with exception thrown");
    }

    @Test
    public void testCreate() throws Exception {
        Collection<MenuItem> menuItems = menuService.getMenuItems();
        assertEquals(2, menuItems.size());

        MenuItem newItem = new MenuItem();
        newItem.setName("Test Item");
        newItem.setDescription("Test New Item Create");
        newItem.setCost(5);
        String newItemJson = objectWriter.writeValueAsString(newItem);

        String out = template.requestBodyAndHeader("http://localhost:" + port1 + "/cafe/menu/items", newItemJson, Exchange.HTTP_METHOD, "POST", String.class);

        assertEquals("3", out);

        Collection<MenuItem> menuUpdateItems = menuService.getMenuItems();
        assertEquals(3, menuUpdateItems.size());

        MenuItem item3 = menuService.getMenuItem(3);
        assertEquals(3, item3.getId());
        assertEquals(newItem.getName(), item3.getName());
        assertEquals(newItem.getDescription(), item3.getDescription());
        assertEquals(newItem.getCost(), item3.getCost());
    }

    @Test
    public void testUpdate() throws Exception {
        Collection<MenuItem> menuItems = menuService.getMenuItems();
        assertEquals(2, menuItems.size());

        MenuItem origMenuItem2 = menuService.getMenuItem(2);

        MenuItem newItem = new MenuItem();
        newItem.setId(origMenuItem2.getId());
        newItem.setName("Test " + origMenuItem2.getName());
        newItem.setDescription("Test " + origMenuItem2.getDescription());
        newItem.setCost(origMenuItem2.getCost() + 1);

        assertNotEquals(origMenuItem2, newItem);

        String newItemJson = objectWriter.writeValueAsString(newItem);

        String out = template.requestBodyAndHeader("http://localhost:" + port1 + "/cafe/menu/items/2", newItemJson, Exchange.HTTP_METHOD, "PUT", String.class);

        assertEquals(newItemJson, out);

        Collection<MenuItem> menuUpdateItems = menuService.getMenuItems();
        assertEquals(2, menuUpdateItems.size());

        MenuItem curItem2 = menuService.getMenuItem(2);
        assertEquals(2, curItem2.getId());
        assertEquals(newItem.getName(), curItem2.getName());
        assertEquals(newItem.getDescription(), curItem2.getDescription());
        assertEquals(newItem.getCost(), curItem2.getCost());
    }

    @Test
    public void testDelete() throws Exception {
        Collection<MenuItem> menuItems = menuService.getMenuItems();
        assertEquals(2, menuItems.size());

        template.requestBodyAndHeader("http://localhost:" + port1 + "/cafe/menu/items/2", null, Exchange.HTTP_METHOD, "DELETE", String.class);

        Collection<MenuItem> menuUpdateItems = menuService.getMenuItems();
        assertEquals(1, menuUpdateItems.size());
        assertEquals(menuItems.iterator().next(), menuUpdateItems.iterator().next());
    }
}