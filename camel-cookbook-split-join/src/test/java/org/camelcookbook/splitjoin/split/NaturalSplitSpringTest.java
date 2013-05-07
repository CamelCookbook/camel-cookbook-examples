package org.camelcookbook.splitjoin.split;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Demonstrates the splitting of arrays, Lists and Iterators into the elements that make them up.
 */
public class NaturalSplitSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/META-INF/spring/naturalSplit-context.xml");
    }

    @Test
    public void testSplitArray() throws Exception {
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.expectedMessageCount(3);
        mockOut.expectedBodiesReceived("one", "two", "three");

        String[] array = new String[] {"one", "two", "three"};
        template.sendBody("direct:in", array);

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSplitList() throws Exception {
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.expectedMessageCount(3);
        mockOut.expectedBodiesReceived("one", "two", "three");

        List<String> list = new LinkedList<String>();
        list.add("one");
        list.add("two");
        list.add("three");

        template.sendBody("direct:in", list);

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSplitIterable() throws Exception {
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.expectedMessageCount(3);
        mockOut.expectedBodiesReceivedInAnyOrder("one", "two", "three");

        Set<String> set = new TreeSet<String>();
        set.add("one");
        set.add("two");
        set.add("three");

        template.sendBody("direct:in", set.iterator());

        assertMockEndpointsSatisfied();
    }

}
