package org.camelcookbook.examples.transactions.fileconsumption;

import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.lang.Validate;

import java.io.File;

/**
 * @author jkorab
 */
public class FileConsumptionRouteBuilder extends RouteBuilder {
    private final String inputDirectory;
    private final String outputDirectory;
    private final String errorDirectory;

    public FileConsumptionRouteBuilder(String targetIn, String targetOut, String targetErrors) {
        Validate.notEmpty(targetIn, "targetIn is empty");
        Validate.notEmpty(targetOut, "targetOut is empty");
        Validate.notEmpty(targetOut, "targetErrors is empty");

        inputDirectory = new File(targetIn).getAbsolutePath();
        outputDirectory = new File(targetOut).getAbsolutePath();
        errorDirectory = new File(targetErrors).getAbsolutePath();
    }

    @Override
    public void configure() throws Exception {
        from("file:" + inputDirectory + "?moveFailed=" + errorDirectory)
            .log("Consumed file ${header[CamelFileName]}: ${body}")
            .convertBodyTo(String.class)
            .choice()
                .when(simple("${body} contains 'explode'"))
                    .to("mock:explosion")
                    .throwException(new IllegalArgumentException("File caused explosion"))
                .otherwise()
                    .to("file:" + outputDirectory)
            .endChoice();
    }
}
