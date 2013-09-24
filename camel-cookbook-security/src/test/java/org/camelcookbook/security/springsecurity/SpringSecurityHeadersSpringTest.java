package org.camelcookbook.security.springsecurity;

import org.apache.camel.CamelAuthorizationException;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.HashMap;
import java.util.Map;

/**
 * Demonstrates the Spring Security to apply authentication and authorization to a route.
 */
public class SpringSecurityHeadersSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/springSecurityHeaders-context.xml");
    }

    @Test
    public void testSecuredServiceAccess() throws InterruptedException {
        MockEndpoint mockSecure = getMockEndpoint("mock:secure");
        mockSecure.setExpectedMessageCount(1);
        mockSecure.expectedBodiesReceived("foo");

        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("username", "jakub");
        headers.put("password", "supersecretpassword1");
        template.sendBodyAndHeaders("direct:in", "foo", headers);

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testBadPassword() {
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("username", "jakub");
        headers.put("password", "iforgotmypassword");
        try {
            template.sendBodyAndHeaders("direct:in", "foo", headers);
            fail();
        } catch (CamelExecutionException ex) {
            CamelAuthorizationException cax = (CamelAuthorizationException) ex.getCause();
            assertTrue(ExceptionUtils.getRootCause(cax) instanceof BadCredentialsException);
        }
    }

    @Test
    public void testNotAuthorized() {
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("username", "scott");
        headers.put("password", "supersecretpassword2");
        try {
            template.sendBodyAndHeaders("direct:in", "foo", headers);
            fail();
        } catch (CamelExecutionException ex) {
            assertTrue(ExceptionUtils.getCause(ex) instanceof CamelAuthorizationException);
        }
    }
}
