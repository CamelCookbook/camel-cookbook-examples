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

package org.camelcookbook.splitjoin.splitxml;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;

class SplitXmlNamespacesRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        Namespaces ns = new Namespaces("c", "http://camelcookbook.org/schema/books")
            .add("se", "http://camelcookbook.org/schema/somethingElse");

        from("direct:in")
            .split(ns.xpath("/c:books/c:book[@category='Tech']/c:authors/c:author/text()"))
                .to("mock:out")
            .end();
    }
}
