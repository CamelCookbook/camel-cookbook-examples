package com.ameliant.training.day1;

import org.apache.camel.Header;
import org.apache.camel.builder.RouteBuilder;

public class RecipientListRoute extends RouteBuilder {

    // POJO
    public class LocaleRouter {
        public boolean isSwedishChef(//@Header("locale")
                                     String locale) {
            return (locale != null && locale.equals("se_SE"));
        }

        public String whereTo(String locale) {
            if (isSwedishChef(locale)) {
                return "mock:swedish,direct:repeat";
            } else {
                return "mock:out";
            }
        }
    }

    @Override
    public void configure() throws Exception {
        from("direct:in").routeId("RecipientListRoute")
            .choice()
                .when(method(new LocaleRouter(),
                        "isSwedishChef(${header[locale]})"))
                    .transform(simple("${body} (Bork bork bork)"))
                .otherwise()
                    .transform(simple("${body}!"))
            .end()
            .transform(simple("Hello ${body}"))
            .setHeader("whereTo", method(new LocaleRouter(),
                    "whereTo(${header[locale]})"))
            .recipientList(header("whereTo"));

        from("direct:repeat").routeId("RecipientListRoute.repeat")
            .transform(simple("Hello ${body}"))
            .log("Repeated: ${body}")
            .to("mock:out");

    }

}
