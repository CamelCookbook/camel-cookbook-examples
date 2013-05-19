package org.camelcookbook.splitjoin.parallel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jkorab
 */
public class RandomDelayProcessor implements Processor {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Override
    public void process(Exchange exchange) throws Exception {
        long delay = (long) (Math.random() * 2000);
        log.info("Delaying message by {}", delay);
        Thread.sleep(delay);
    }
}
