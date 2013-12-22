/*
 * Copyright (C) Scott Cranton and Jakub Korab
 * https://github.com/CamelCookbook
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camelcookbook.transformation.csv;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.apache.camel.util.CastUtils;
import org.camelcookbook.transformation.csv.model.BookModel;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CsvSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/csv-context.xml");
    }

    @Test
    public void testCsvMarshal() throws Exception {
        ArrayList<BookModel> books = new ArrayList<BookModel>();

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-yyyy");

        BookModel book = new BookModel();
        book.setCategory("PROGRAMMING");
        book.setTitle("Camel in Action");
        book.setTitleLanguage("en");
        book.setAuthor1("Claus Ibsen");
        book.setAuthor2("Jon Anstey");
        book.setPublishDate(simpleDateFormat.parse("Dec-2010"));
        book.setPrice(BigDecimal.valueOf(49.99));

        books.add(book);

        book = new BookModel();
        book.setCategory("PROGRAMMING");
        book.setTitle("Apache Camel Developer's Cookbook");
        book.setTitleLanguage("en");
        book.setAuthor1("Scott Cranton");
        book.setAuthor2("Jakub Korab");
        book.setPublishDate(simpleDateFormat.parse("Dec-2013"));
        book.setPrice(BigDecimal.valueOf(49.99));

        books.add(book);

        final String response = template.requestBody("direct:marshal", books, String.class);

        log.info(response);
        final String expects = "PROGRAMMING,Camel in Action,en,Claus Ibsen,Jon Anstey,Dec-2010,49.99\n" +
            "PROGRAMMING,Apache Camel Developer's Cookbook,en,Scott Cranton,Jakub Korab,Dec-2013,49.99\n";
        assertEquals(expects, response);
    }

    @Test
    public void testCsvUnmarshal() throws Exception {
        final String request = "PROGRAMMING,Camel in Action,en,Claus Ibsen,Jon Anstey,Dec-2010,49.99\n" +
            "PROGRAMMING,Apache Camel Developer's Cookbook,en,Scott Cranton,Jakub Korab,Dec-2013,49.99\n";

        final List<Map<String, BookModel>> response = CastUtils.cast(template.requestBody("direct:unmarshal", request, List.class));

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-yyyy");

        BookModel book1 = new BookModel();
        book1.setCategory("PROGRAMMING");
        book1.setTitle("Camel in Action");
        book1.setTitleLanguage("en");
        book1.setAuthor1("Claus Ibsen");
        book1.setAuthor2("Jon Anstey");
        book1.setPublishDate(simpleDateFormat.parse("Dec-2010"));
        book1.setPrice(BigDecimal.valueOf(49.99));

        BookModel book2 = new BookModel();
        book2.setCategory("PROGRAMMING");
        book2.setTitle("Apache Camel Developer's Cookbook");
        book2.setTitleLanguage("en");
        book2.setAuthor1("Scott Cranton");
        book2.setAuthor2("Jakub Korab");
        book2.setPublishDate(simpleDateFormat.parse("Dec-2013"));
        book2.setPrice(BigDecimal.valueOf(49.99));

        Map<String, BookModel> response1 = response.get(0);
        assertEquals(1, response1.size());

        // Map of <class name String>, <Model object value>
        Map.Entry<String, BookModel> entry1 = response1.entrySet().iterator().next();
        assertEquals(BookModel.class.getName(), entry1.getKey());
        assertEquals(book1, entry1.getValue());

        Map<String, BookModel> response2 = response.get(1);
        assertEquals(1, response1.size());
        Map.Entry<String, BookModel> entry2 = response2.entrySet().iterator().next();
        assertEquals(BookModel.class.getName(), entry2.getKey());
        assertEquals(book2, entry2.getValue());
    }
}
