package org.camelcookbook.structuringroutes.seda;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class LongRunningProcessor implements Processor {

    final public static int DELAY_TIME = 3000;

    @Override
    public void process(Exchange exchange) throws Exception {
        Thread.sleep(3000);
        exchange.getIn().setBody("Long running process finished");
    }

}
