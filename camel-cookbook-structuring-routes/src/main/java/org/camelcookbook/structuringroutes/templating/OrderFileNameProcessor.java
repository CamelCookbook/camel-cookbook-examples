package org.camelcookbook.structuringroutes.templating;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Processor class that takes a line of CSV as an input on the Exchange:
 * <ol>
 *     <li>gets the first field,</li>
 *     <li>parses it according to a country-specific date format</li>
 *     <li>sets the CamelFileName header to the date the file should be written to</li>
 *     <li>changes the first field to the universal one.</li>
 * </ol>
 */
public class OrderFileNameProcessor implements Processor {

    /**
     * See http://xkcd.com/1179/
     */
    public final static String UNIVERSAL_DATE_FORMAT = "yyyy-MM-dd";

    public String countryDateFormat;

    public void setCountryDateFormat(String countryDateFormat) {
        this.countryDateFormat = countryDateFormat;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();

        // there are better way to handle CSV files, but this is OK as an example
        String[] fields = in.getBody(String.class).split(",");
        String countrySpecificDate = fields[0];

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(countryDateFormat);
        Date date = simpleDateFormat.parse(countrySpecificDate);

        SimpleDateFormat universalDateFormat = new SimpleDateFormat(UNIVERSAL_DATE_FORMAT);
        String universalDate = universalDateFormat.format(date);
        fields[0] = universalDate;

        in.setHeader("CamelFileName", universalDate + ".csv");
        in.setBody(StringUtils.join(fields, ","));
    }
}
