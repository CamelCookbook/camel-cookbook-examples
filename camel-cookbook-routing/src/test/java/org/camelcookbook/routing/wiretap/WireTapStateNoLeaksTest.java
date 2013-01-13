package org.camelcookbook.routing.wiretap;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.camelcookbook.routing.model.Cheese;
import org.junit.Assert;
import org.junit.Test;

public class WireTapStateNoLeaksTest extends CamelTestSupport {

    @Produce(uri = "direct:in")
    protected ProducerTemplate template;

    @EndpointInject(uri = "mock:tapped")
    private MockEndpoint tapped;

    @EndpointInject(uri = "mock:out")
    private MockEndpoint out;

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new WireTapStateNoLeaksRouteBuilder();
    }

    @Test
    public void testOutMessageUnaffectedByTappedRoute() throws InterruptedException {
        Cheese cheese = new Cheese();
        cheese.setAge(1);

        tapped.setExpectedMessageCount(1);
        out.setExpectedMessageCount(1);

        template.sendBody(cheese);

        tapped.setResultWaitTime(1000);
        // check that the endpoints both received the same message
        tapped.assertIsSatisfied();
        out.assertIsSatisfied();

        out.expectedBodyReceived().equals(cheese);
        Cheese outCheese = out.getExchanges().get(0).getIn().getBody(Cheese.class);
        Assert.assertEquals(1, outCheese.getAge());

        Cheese tappedCheese = tapped.getExchanges().get(0).getIn().getBody(Cheese.class);
        Assert.assertEquals(2, tappedCheese.getAge());
    }

}
