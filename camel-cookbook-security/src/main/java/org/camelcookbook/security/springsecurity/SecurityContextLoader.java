package org.camelcookbook.security.springsecurity;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Processor that fetches the user credentials from the Exchange and sets up the
 * {@link Authentication} object on Spring Security's
 * {@link SecurityContextHolder}.
 */public class SecurityContextLoader implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();
        String username = in.getHeader("username", String.class);
        String password = in.getHeader("password", String.class);

        Authentication authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}

