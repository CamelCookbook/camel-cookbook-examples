package org.camelcookbook.structuringroutes.routecontrol;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Processor class that turns off a named route after a predefined number of messages has flown through it.
 * TODO write test
 */
public class RouteStoppingProcessor implements Processor {
    private final static int MAX_MESSAGES = 10;
    private final AtomicInteger messageCount = new AtomicInteger();
    private String routeName;

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        int count = messageCount.getAndIncrement();
        if (count == MAX_MESSAGES) {
            exchange.getContext().stopRoute(routeName);
        }
    }
}
