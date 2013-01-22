package org.camelcookbook.routing.recipientlist;

import org.apache.camel.Exchange;

/**
 * Routing bean used to determine a list of endpoints to be triggered by a recipient list.
 */
public class MessageRouter {

    public String getEndpointsToRouteMessageTo(Exchange exchange) {
        String orderType = exchange.getIn().getHeader("orderType", String.class);
        if (orderType == null) {
            return "direct:unrecognized";
        } else if (orderType.equals("priority")) {
            return "direct:order.priority,direct:billing";
        } else {
            return "direct:order.normal,direct:billing";
        }
    }
}
