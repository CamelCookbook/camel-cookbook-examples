package org.camelcookbook.splitjoin.split;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.LinkedList;
import java.util.List;

/**
 * Demonstrates the splitting of a List by using a Simple expression to locate it in an object graph.
 */
public class SplitSimpleExpressionSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/META-INF/spring/splitSimpleExpression-context.xml");
    }

    @Test
    public void testSimpleExpressionReferenceToList() throws Exception {
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.expectedMessageCount(3);
        mockOut.expectedBodiesReceived("one", "two", "three");

        List<String> list = new LinkedList<String>();
        list.add("one");
        list.add("two");
        list.add("three");
        ListWrapper wrapper = new ListWrapper();
        wrapper.setWrapped(list);

        template.sendBody("direct:in", wrapper);

        assertMockEndpointsSatisfied();
    }

}
