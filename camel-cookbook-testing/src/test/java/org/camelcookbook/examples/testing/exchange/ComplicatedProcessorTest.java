package org.camelcookbook.examples.testing.exchange;


import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author jkorab
 */
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
