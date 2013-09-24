package org.camelcookbook.security.springsecurity;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.security.auth.Subject;

/**
 * Processor that fetches the user credentials from the Exchange and sets up the
 * {@link org.springframework.security.core.Authentication} object on the Exchange.
 */
public class SecuritySubjectLoader implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();
        String username = in.getHeader("username", String.class);
        String password = in.getHeader("password", String.class);

        Authentication authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);
        Subject subject = new Subject();
        subject.getPrincipals().add(authenticationToken);
        in.setHeader(Exchange.AUTHENTICATION, subject);
    }
}

