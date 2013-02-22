package org.camelcookbook.structuringroutes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
* Route that logs a message every second.
*/
@Component
public class LogMessageOnTimerEventRouteBuilder extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer:logMessageTimer?period=1s")
            .to("mylogger:insideTheRoute?showHeaders=true")
            .log("Event triggered by ${property.CamelTimerName} at ${header.CamelTimerFiredTime}");
    }
}
