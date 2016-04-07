package com.ameliant.training.day3.typeconverters;

import org.apache.camel.Converter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author jkorab
 */
@Converter
public class DateTimeConverter {

    public static final String FORMAT = "yyyyMMdd";

    @Converter
    public static DateTime convertString(String strDate) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(FORMAT);
        return fmt.parseDateTime(strDate);
    }

    @Converter
    public static String convertDateTime(DateTime dateTime) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(FORMAT);
        return fmt.print(dateTime);
    }

}
