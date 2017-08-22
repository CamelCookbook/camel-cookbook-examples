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

package org.camelcookbook.rest.param;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestParamType;

/**
 * Simple REST DSL example
 */
public class ParamRoute extends RouteBuilder {
    private int port1;

    public ParamRoute() {
    }

    public ParamRoute(int port) {
        this.port1 = port;
    }

    public void setPort1(int port1) {
        this.port1 = port1;
    }

    @Override
    public void configure() throws Exception {
        restConfiguration()
            .component("undertow").host("localhost").port(port1);

        rest("/say")
            .get("/hello")
                .route().transform().constant("Hello World").endRest()
            .get("/hello/{name}")
                .route().transform(simple("Hello ${header.name}")).endRest()
            .get("/hello/query/{name}?verbose={verbose}")
                .param().name("verbose").type(RestParamType.query).defaultValue("false").endParam()
                .to("direct:hello")
            .post("/bye/{name}")
                .toD("mock:${header.name}");

        from("direct:hello")
            .choice()
                .when(header("verbose").isEqualTo(true))
                    .transform(simple("Hello there ${header.name}! How are you today?")).endChoice()
                .otherwise()
                    .transform(simple("Yo ${header.name}"));
    }
}
