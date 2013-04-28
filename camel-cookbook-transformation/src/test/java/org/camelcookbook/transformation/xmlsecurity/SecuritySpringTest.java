/*
 * Copyright (C) Scott Cranton and Jakub Korab
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

package org.camelcookbook.transformation.xmlsecurity;

import java.io.InputStream;

import org.apache.camel.builder.xml.XPathBuilder;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SecuritySpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/xmlsecurity-context.xml");
    }

    @Test
    public void testSecuritySpring() throws Exception {
        final InputStream resource = getClass().getClassLoader().getResourceAsStream("booklocations.xml");
        final String request = context().getTypeConverter().convertTo(String.class, resource);

        String response = template.requestBody("direct:marshal", request, String.class);

        log.info("Marshal result = {}", response);

        final XPathBuilder builder = XPathBuilder.xpath("exists(/booksignings/store/address/city)").booleanResult();

        assertFalse(builder.evaluate(context(), response, boolean.class));

        response = template.requestBody("direct:unmarshal", response, String.class);

        log.info("Unmarshal result = {}", response);

        assertTrue(builder.evaluate(context(), response, boolean.class));
    }
}
