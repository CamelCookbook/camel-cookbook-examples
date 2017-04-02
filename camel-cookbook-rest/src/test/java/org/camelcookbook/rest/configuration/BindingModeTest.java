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

package org.camelcookbook.rest.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

public class BindingModeTest extends CamelTestSupport {
    private final int port1 = AvailablePortFinder.getNextAvailable();

    private ObjectWriter objectWriter = new ObjectMapper().writer();

    private ItemService getItemService() {
        return context().getRegistry().lookupByNameAndType("itemService", ItemService.class);
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();

        registry.bind("itemService", new ItemService());

        return registry;
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new BindingModeRouteBuilder(port1);
    }

    @Test
    public void testGetOne() throws Exception {
        final Item origItem = getItemService().getItem(0);
        final String origItemJson = objectWriter.writeValueAsString(origItem);

        String outJson = fluentTemplate().to("undertow:http://localhost:" + port1 + "/items/0")
                .withHeader(Exchange.HTTP_METHOD, "GET")
                .withHeader("Accept", "application/json")
                .request(String.class);

        assertEquals(origItemJson, outJson);

        String outXml = fluentTemplate().to("undertow:http://localhost:" + port1 + "/items/0")
                .withHeader(Exchange.HTTP_METHOD, "GET")
                .withHeader("Accept", "application/xml")
                .request(String.class);

        JAXBContext jaxbContext = JAXBContext.newInstance(Item.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        Item itemOut = (Item) jaxbUnmarshaller.unmarshal(new StringReader(outXml));

        assertEquals(origItem, itemOut);
    }

    @Test
    public void testGetOneJson() throws Exception {
        final String origValue = objectWriter.writeValueAsString(getItemService().getItem(0));

        String out = fluentTemplate().to("undertow:http://localhost:" + port1 + "/items/0/json")
                .withHeader(Exchange.HTTP_METHOD, "GET")
                .request(String.class);

        assertEquals(origValue, out);
    }

    @Test
    public void testGetOneXml() throws Exception {
        final Item origItem = getItemService().getItem(0);

        String out = fluentTemplate().to("undertow:http://localhost:" + port1 + "/items/0/xml")
                .withHeader(Exchange.HTTP_METHOD, "GET")
                .request(String.class);

        JAXBContext jaxbContext = JAXBContext.newInstance(Item.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        Item itemOut = (Item) jaxbUnmarshaller.unmarshal(new StringReader(out));

        assertEquals(origItem, itemOut);
    }
}