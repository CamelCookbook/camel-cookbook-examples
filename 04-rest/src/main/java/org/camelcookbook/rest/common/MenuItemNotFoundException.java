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

/**
 * Exception when item id is not found.
 */
public class MenuItemNotFoundException extends Exception {
    private static final long serialVersionUID = 5929423385228527233L;

    private int id;

    public MenuItemNotFoundException(int id) {
        super("Menu Item id " + id + " not found");
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
