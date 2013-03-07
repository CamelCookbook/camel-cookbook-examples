package org.camelcookbook.structuringroutes.templating;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang.Validate;

import javax.annotation.PostConstruct;

public class OrderProcessingRouteBuilder extends RouteBuilder {
    String inputUri;
    String outputUri;
    private OrderFileNameProcessor orderFileNameProcessor;

    public void setInputDirectory(String inputDirectory) {
        inputUri = "file://" + inputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        outputUri = "file://" + outputDirectory;
    }

    public void setOrderFileNameProcessor(OrderFileNameProcessor orderFileNameProcessor) {
        this.orderFileNameProcessor = orderFileNameProcessor;
    }

    @PostConstruct
    public void checkMandatoryProperties() {
        Validate.notEmpty(inputUri, "inputUri is empty");
        Validate.notEmpty(outputUri, "outputUri is empty");
        Validate.notNull(orderFileNameProcessor, "orderFileNameProcessor is null");
    }

    @Override
    public void configure() throws Exception {
        from(inputUri)
            .split(body(String.class).tokenize("\n")) // split into individual lines
                .process(orderFileNameProcessor)
                .log("Writing file: ${header.CamelFileName}")
                .to(outputUri)
            .end();
    }

}
