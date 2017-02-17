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

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MenuService {
    private final Map<Integer, MenuItem> menu = new TreeMap<>();
    private final AtomicInteger ids = new AtomicInteger();

    // TODO: add unit tests for common classes

    public MenuService() throws Exception {
        MenuItem item = new MenuItem();
        item.setName("Coffee");
        item.setCost(1);
        item.setDescription("Kona Zoom Zoom");
        createMenuItem(item);

        item = new MenuItem();
        item.setName("Bagel");
        item.setCost(3);
        item.setDescription("Bagel with Cream Cheese");
        createMenuItem(item);
    }

    public int createMenuItem(MenuItem item) throws MenuItemInvalidException {
        if (item.getCost() <=0) {
            throw new MenuItemInvalidException("Cost must be greater than 0");
        }

        int id = ids.incrementAndGet();
        item.setId(id);
        menu.put(id, item);
        return id;
    }

    public Collection<MenuItem> getMenuItems() {
        return menu.values();
    }

    public MenuItem getMenuItem(int itemId) throws MenuItemNotFoundException {
        MenuItem item = menu.get(itemId);
        if (item == null) {
            throw new MenuItemNotFoundException(itemId);
        }
        return item;
    }

    public void updateMenuItem(MenuItem item) throws MenuItemNotFoundException {
        if (!menu.containsKey(item.getId())) {
            throw new MenuItemNotFoundException(item.getId());
        }
        menu.put(item.getId(), item);
    }

    public void removeMenuItem(int itemId) {
        menu.remove(itemId);
    }
}
