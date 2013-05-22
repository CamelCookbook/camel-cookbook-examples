package org.camelcookbook.splitjoin.xml;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Demonstrates the splitting of Xml files through XPath expression.
 *
 * This test is intended to be run out of Maven, as it references the target directory.
 */
public class XmlSplitSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/META-INF/spring/splitXml-context.xml");
    }

    @Test
    public void testSplitArray() throws Exception {
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.expectedMessageCount(2);
        mockOut.expectedBodiesReceived("Scott Cranton", "Jakub Korab");


        String filename = "target/classes/xml/books.xml";
        assertFileExists(filename);
        InputStream booksStream = new FileInputStream(filename);

        template.sendBody("direct:in", booksStream);

        assertMockEndpointsSatisfied();
    }
}
