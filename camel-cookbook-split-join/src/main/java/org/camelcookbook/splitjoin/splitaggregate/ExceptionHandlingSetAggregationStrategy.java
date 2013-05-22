package org.camelcookbook.splitjoin.splitaggregate;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import java.util.HashSet;
import java.util.Set;

/**
 * @author jkorab
 */
public class ExceptionHandlingSetAggregationStrategy implements AggregationStrategy{
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        String body = newExchange.getIn().getBody(String.class);
        Exception exception = newExchange.getException();
        if (exception != null) {
            newExchange.setException(null); // remove the exception
            body = "Failed: " + body;
        }
        if (oldExchange == null) {
            Set<String> set = new HashSet<String>();
            set.add(body);
            newExchange.getIn().setBody(set);
            return newExchange;
        }  else {
            Set<String> set = oldExchange.getIn().getBody(Set.class);
            set.add(body);
            return oldExchange;
        }
    }
}
