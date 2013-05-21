package org.camelcookbook.splitjoin.aggregate;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.HashSet;
import java.util.Set;

/**
 * @author jkorab
 */
public class SetAggregationStrategy implements AggregationStrategy{
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        String body = newExchange.getIn().getBody(String.class);
        Set<String> set;
        if (oldExchange == null) {
            set = new HashSet<String>();
            set.add(body);
            newExchange.getIn().setBody(set);
            return newExchange;
        }  else {
            set = oldExchange.getIn().getBody(Set.class);
            set.add(body);
            return oldExchange;
        }
    }
}
