package com.ameliant.training.day1;

import org.apache.camel.Header;
import org.apache.camel.builder.RouteBuilder;

public class ChoiceRoute extends RouteBuilder {

    // POJO
    public class LocaleRouter {
        public boolean isSwedishChef(//@Header("locale")
                                     String locale) {
            return (locale != null && locale.equals("se_SE"));
        }
    }

    @Override
    public void configure() throws Exception {
        from("direct:in").routeId("ChoiceRoute")
            .choice()
                .when(method(new LocaleRouter(),
                        "isSwedishChef(${header[locale]})"))
                    .transform(simple("${body} (Bork bork bork)"))
                    .setHeader("sendTo", constant("swedish"))
                .otherwise()
                    .transform(simple("${body}!"))
                    .setHeader("sendTo", constant("out"))
            .end()
            .transform(simple("Hello ${body}"))
            .toD("mock:${header[sendTo]}");
    }

}
