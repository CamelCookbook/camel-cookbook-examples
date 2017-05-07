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

package org.camelcookbook.rest.common;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class MenuServiceTest {
    private MenuService menuService = null;

    @Before
    public void setUp() throws Exception {
        menuService = new MenuService();
    }

    @Test
    public void createMenuItem() throws Exception {
        final Collection<MenuItem> menuItems = menuService.getMenuItems();
        final int initialSize = menuItems.size();

        MenuItem newItem = new MenuItem();
        newItem.setId(-1); // intentionally initialize to an invalid id
        newItem.setName("Hawt New Stuff");
        newItem.setDescription("So new and yet so cool");
        newItem.setCost(3);

        // assert new Item does not exist in current menu
        assertFalse(menuItems.contains(newItem));

        int newItemId = menuService.createMenuItem(newItem);

        // Should not mutate input item
        assertNotEquals(newItem.getId(), newItemId);

        final MenuItem menuItem = menuService.getMenuItem(newItemId);
        assertNotEquals(newItem, menuItem); // only true because we initialized to invalid id, and service not mutating input

        assertEquals(newItemId, menuItem.getId());
        assertEquals(newItem.getName(), menuItem.getName());
        assertEquals(newItem.getCost(), menuItem.getCost());
        assertEquals(newItem.getDescription(), menuItem.getDescription());

        Collection<MenuItem> newMenuItems = menuService.getMenuItems();
        assertTrue(newMenuItems.size() > initialSize);
        assertTrue(newMenuItems.contains(menuItem));
    }

    @Test
    public void createMenuItemDuplicateId() throws Exception {
        final Collection<MenuItem> menuItems = menuService.getMenuItems();
        final int initialSize = menuItems.size();
        assertTrue(initialSize >= 1); // assumes menuService setup with at least 1 item

        MenuItem newItem = new MenuItem();
        newItem.setId(-1); // intentionally initialize to an invalid id
        newItem.setName("Hawt New Stuff");
        newItem.setDescription("So new and yet so cool");
        newItem.setCost(3);

        // assert new Item does not exist in current menu
        assertFalse(menuItems.contains(newItem));

        try {
            int newItemId = menuService.createMenuItem(1, newItem);
        } catch (MenuItemInvalidException e) {
            return;
        }

        fail("Should have failed with thrown Invalid Item exception");
    }

    @Test
    public void createMenuItemInvalidCost() throws Exception {
        MenuItem newItem = new MenuItem();
        newItem.setId(-1); // intentionally initialize to an invalid id
        newItem.setName("Hawt New Stuff");
        newItem.setDescription("So new and yet so cool");
        newItem.setCost(-1);

        assertTrue(newItem.getCost() <= 0);

        try {
            int newItemId = menuService.createMenuItem(newItem);
            fail("Should have failed with thrown Invalid Item exception");
        } catch (MenuItemInvalidException e) {
        }
    }

    @Test
    public void getMenuItems() throws Exception {
        assertNotNull(menuService.getMenuItems());
    }

    @Test
    public void getMenuItem() throws Exception {
        final Collection<MenuItem> menuItems = menuService.getMenuItems();
        assertTrue(menuItems.size() >= 1);

        final MenuItem menuItem = menuService.getMenuItem(1);
        assertEquals(menuItems.iterator().next(), menuItem);
    }

    @Test
    public void updateMenuItemCreate() throws Exception {
        final Collection<MenuItem> menuItems = menuService.getMenuItems();
        final int initialSize = menuItems.size();

        MenuItem newItem = new MenuItem();
        newItem.setId(-1); // intentionally initialize to an invalid id
        newItem.setName("Hawt New Stuff");
        newItem.setDescription("So new and yet so cool");
        newItem.setCost(3);

        // assert new Item does not exist in current menu
        assertFalse(menuItems.contains(newItem));

        final int newId = initialSize + 1;
        menuService.updateMenuItem(newId, newItem);

        MenuItem curMenuItem = menuService.getMenuItem(newId);

        assertNotNull(curMenuItem);
        assertNotEquals(newItem, curMenuItem); // curMenuItem id should be different, and equal to newId
        assertEquals(newId, curMenuItem.getId());
        assertEquals(newItem.getName(), curMenuItem.getName());
        assertEquals(newItem.getCost(), curMenuItem.getCost());
        assertEquals(newItem.getDescription(), curMenuItem.getDescription());
    }

    @Test
    public void updateMenuItemModify() throws Exception {
        final int testId = 1;

        Collection<MenuItem> menuItems = menuService.getMenuItems();
        final int initialSize = menuItems.size();
        assertTrue(initialSize >= 1);

        MenuItem origMenuItem = menuService.getMenuItem(testId);
        assertEquals(testId, origMenuItem.getId());

        MenuItem newMenuItem = new MenuItem(origMenuItem);
        newMenuItem.setName("Different than " + origMenuItem.getName());

        assertNotEquals(origMenuItem, newMenuItem);

        menuService.updateMenuItem(testId, newMenuItem);

        MenuItem curMenuItem = menuService.getMenuItem(testId);
        assertEquals(newMenuItem, curMenuItem);
    }

    @Test
    public void removeMenuItem() throws Exception {
        final int testId = 1;

        Collection<MenuItem> menuItems = menuService.getMenuItems();
        final int initialSize = menuItems.size();
        assertTrue(initialSize >= 1);

        final MenuItem origMenuItem = menuService.getMenuItem(testId);
        assertNotNull(origMenuItem);

        menuService.removeMenuItem(testId);

        final Collection<MenuItem> curMenuItems = menuService.getMenuItems();
        assertTrue(curMenuItems.size() < initialSize);
        assertFalse(curMenuItems.contains(origMenuItem));

        try {
            MenuItem menuItem = menuService.getMenuItem(testId);
            fail("Should have thrown MenuItemNotFoundException");
        } catch (MenuItemNotFoundException e) {
        }
    }
}