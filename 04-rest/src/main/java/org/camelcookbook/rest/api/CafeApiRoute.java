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

package org.camelcookbook.rest.api;

import com.fasterxml.jackson.core.JsonParseException;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.camelcookbook.rest.common.MenuItem;
import org.camelcookbook.rest.common.MenuItemInvalidException;
import org.camelcookbook.rest.common.MenuItemNotFoundException;

/**
 * Simple REST DSL example
 */
public class CafeApiRoute extends RouteBuilder {
    private int port1;

    public CafeApiRoute() {
    }

    public CafeApiRoute(int port) {
        this.port1 = port;
    }

    public void setPort1(int port1) {
        this.port1 = port1;
    }

    @Override
    public void configure() throws Exception {
        onException(MenuItemNotFoundException.class)
            .handled(true)
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(404))
            .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
            .setBody().simple("${exception.message}");

        onException(MenuItemInvalidException.class)
            .handled(true)
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
            .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
            .setBody().simple("${exception.message}");

        onException(JsonParseException.class)
            .handled(true)
            .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
            .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
            .setBody().constant("Invalid json data");

        restConfiguration()
            .component("undertow").port(port1)
            .bindingMode(RestBindingMode.json)
            .apiContextPath("api-doc")
            .apiProperty("api.title", "Cafe Menu")
            .apiProperty("api.version", "1.0.0")
            .apiProperty("api.description", "Cafe Menu Sample API")
            .apiProperty("api.contact.name", "Camel Cookbook")
            .apiProperty("api.contact.url", "http://www.camelcookbook.org")
            .apiProperty("api.license.name", "Apache 2.0")
            .apiProperty("api.license.url", "http://www.apache.org/licenses/LICENSE-2.0.html")
            .enableCORS(true);

        rest("/cafe/menu").description("Cafe Menu Services")
            .get("/items").description("Returns all menu items").outType(MenuItem[].class)
                .responseMessage().code(200).message("All of the menu items").endResponseMessage()
                .to("bean:menuService?method=getMenuItems")
            .get("/items/{id}").description("Returns menu item with matching id").outType(MenuItem.class)
                .param().name("id").type(RestParamType.path).description("The id of the item").dataType("int").endParam()
                .responseMessage().code(200).message("The requested menu item").endResponseMessage()
                .responseMessage().code(404).message("Menu item not found").endResponseMessage()
                .to("bean:menuService?method=getMenuItem(${header.id})")
            .post("/items").description("Creates a new menu item").type(MenuItem.class)
                .param().name("body").type(RestParamType.body).description("The item to create").endParam()
                .responseMessage().code(201).message("Successfully created menu item").endResponseMessage()
                .responseMessage().code(400).message("Invalid menu item").endResponseMessage()
                .route().to("bean:menuService?method=createMenuItem").setHeader(Exchange.HTTP_RESPONSE_CODE, constant(201)).endRest()
            .put("/items/{id}").description("Updates an existing or creates a new menu item").type(MenuItem.class)
                .param().name("id").type(RestParamType.path).description("The id of the item").dataType("int").endParam()
                .param().name("body").type(RestParamType.body).description("The menu item new contents").endParam()
                .responseMessage().code(200).message("Successfully updated item").endResponseMessage()
                .responseMessage().code(400).message("Invalid menu item").endResponseMessage()
                .to("bean:menuService?method=updateMenuItem(${header.id}, ${body})")
            .delete("/items/{id}").description("Deletes the specified item")
                .param().name("id").type(RestParamType.path).description("The id of the item").dataType("int").endParam()
                .responseMessage().code(200).message("Successfully deleted item").endResponseMessage()
                .to("bean:menuService?method=removeMenuItem(${header.id})");
    }
}
