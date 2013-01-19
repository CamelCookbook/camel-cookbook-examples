package org.camelcookbook.routing.contentbasedrouter;

import org.apache.camel.test.junit4.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ContentBasedRouterSpringTest extends CamelSpringTestSupport {

    @Test
    public void testWhen() throws Exception {
        getMockEndpoint("mock:camel").expectedMessageCount(1);
        getMockEndpoint("mock:camel2").expectedMessageCount(0);
        getMockEndpoint("mock:other").expectedMessageCount(0);

        template.sendBody("direct:start", "Camel Rocks");

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testOther() throws Exception {
        getMockEndpoint("mock:camel").expectedMessageCount(0);
        getMockEndpoint("mock:camel2").expectedMessageCount(0);
        getMockEndpoint("mock:other").expectedMessageCount(1);

        template.sendBody("direct:start", "Hello World");

        assertMockEndpointsSatisfied();
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("spring/contentbasedrouter-context.xml");
    }
}
