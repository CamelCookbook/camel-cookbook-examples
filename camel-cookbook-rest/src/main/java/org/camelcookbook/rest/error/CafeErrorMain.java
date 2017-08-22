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

import org.apache.camel.main.Main;
import org.camelcookbook.rest.common.MenuService;

public class CafeErrorMain {
    public static void main(String[] args) throws Exception {
        Main main = new Main();

        main.bind("menuService", new MenuService());

        main.addRouteBuilder(new CafeErrorRoute(8080));

        System.out.println("*******************************");
        System.out.println();
        System.out.println("You can call this service at http://localhost:8080/cafe/menu/items");
        System.out.println();
        System.out.println("*******************************");

        main.run();
    }
}
