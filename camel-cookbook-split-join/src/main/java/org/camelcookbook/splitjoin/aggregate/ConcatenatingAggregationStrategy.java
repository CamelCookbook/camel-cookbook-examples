package org.camelcookbook.splitjoin.aggregate;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 * @author jkorab
 */
public class ConcatenatingAggregationStrategy implements AggregationStrategy{
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange;
        }  else {
            String oldBody = oldExchange.getIn().getBody(String.class);
            String newBody = newExchange.getIn().getBody(String.class);
            newExchange.getIn().setBody(oldBody + newBody);
            return newExchange;
        }
    }
}
