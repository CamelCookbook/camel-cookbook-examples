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

package org.camelcookbook.examples.testing.exchange;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ComplicatedProcessorTest {

    @Test
    public void testPrepend() throws Exception {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        Message in = exchange.getIn();
        in.setHeader("action", "prepend");
        in.setBody("text");

        Processor processor = new ComplicatedProcessor();
        processor.process(exchange);

        assertTrue(in.getHeader("actionTaken", Boolean.class));
        assertEquals("SOMETHING text", in.getBody());
    }
}
