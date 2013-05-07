package org.camelcookbook.splitjoin.split;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.*;

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
        String[] array = new String[] {"one", "two", "three"};

        MockEndpoint mockSplit = getMockEndpoint("mock:split");
        mockSplit.expectedMessageCount(3);
        mockSplit.expectedBodiesReceived("one", "two", "three");

        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.expectedMessageCount(1);
        mockOut.message(0).body().isEqualTo(array);

        template.sendBody("direct:in", array);

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSplitList() throws Exception {
        List<String> list = new LinkedList<String>();
        list.add("one");
        list.add("two");
        list.add("three");

        MockEndpoint mockSplit = getMockEndpoint("mock:split");
        mockSplit.expectedMessageCount(3);
        mockSplit.expectedBodiesReceived("one", "two", "three");

        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.expectedMessageCount(1);
        mockOut.message(0).body().isEqualTo(list);

        template.sendBody("direct:in", list);

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testSplitIterable() throws Exception {
        Set<String> set = new TreeSet<String>();
        set.add("one");
        set.add("two");
        set.add("three");
        Iterator<String> iterator = set.iterator();

        MockEndpoint mockSplit = getMockEndpoint("mock:split");
        mockSplit.expectedMessageCount(3);
        mockSplit.expectedBodiesReceivedInAnyOrder("one", "two", "three");

        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.expectedMessageCount(1);
        mockOut.message(0).body().isEqualTo(iterator);

        template.sendBody("direct:in", iterator);

        assertMockEndpointsSatisfied();
    }

}
