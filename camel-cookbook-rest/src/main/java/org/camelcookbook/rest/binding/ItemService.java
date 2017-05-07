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

package org.camelcookbook.rest.binding;

import java.util.ArrayList;

public class ItemService {
    private ArrayList<Item> items = new ArrayList<>();

    public ItemService() {
        items.add(new Item("Thing0"));
        items.add(new Item("Thing1"));
    }

    public Item[] getItems() {
        Item[] out = new Item[items.size()];
        return items.toArray(out);
    }

    public Item getItem(int id) {
        return items.get(id);
    }

    public void setItem(int id, Item item) {
        items.set(id, item);
    }

    public void setItems(Item[] items) {
        this.items.clear();
        for (int i = 0; i < items.length; i++) {
            this.items.add(i, items[i]);
        }
    }
}
