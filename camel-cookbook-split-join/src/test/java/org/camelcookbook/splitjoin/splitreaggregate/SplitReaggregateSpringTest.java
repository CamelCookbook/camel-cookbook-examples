package org.camelcookbook.splitjoin.splitreaggregate;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Demonstrates the splitting of a payload, processing of each of the fragments and reaggregating the results.
 */
public class SplitReaggregateSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/META-INF/spring/splitReaggregate-context.xml");
    }

    @Test
    public void testSplitAggregatesResponses() throws Exception {
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.expectedMessageCount(2);

        String filename = "target/classes/xml/books-extended.xml";
        assertFileExists(filename);
        InputStream booksStream = new FileInputStream(filename);

        template.sendBody("direct:in", booksStream);

        assertMockEndpointsSatisfied();
        List<Exchange> receivedExchanges = mockOut.getReceivedExchanges();
        assertBooksByCategory(receivedExchanges.get(0));
        assertBooksByCategory(receivedExchanges.get(1));
    }

    private void assertBooksByCategory(Exchange exchange) {
        Message in = exchange.getIn();
        Set<String> books = in.getBody(Set.class);
        String category = in.getHeader("category", String.class);
        if (category.equals("Tech")) {
            assertTrue(books.containsAll(Arrays.asList("Camel Enterprise Integration Cookbook")));
        } else if (category.equals("Cooking")){
            assertTrue(books.containsAll(Arrays.asList("Camel Cookbook",
                    "Double decandence with extra cream", "Cooking with Butter")));
        } else {
            fail();
        }
    }


}
