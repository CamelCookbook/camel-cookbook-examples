Apache Camel Developer's Cookbook, 2nd Edition Samples
======================================================

[![Build Status](https://travis-ci.org/CamelCookbook/camel-cookbook-examples.png?branch=master)](https://travis-ci.org/CamelCookbook/camel-cookbook-examples)
[![Stories in Ready](https://badge.waffle.io/CamelCookbook/camel-cookbook-examples.png?label=ready&title=Ready)](https://waffle.io/CamelCookbook/camel-cookbook-examples?utm_source=badge)

This project contains the sample code for the [_Apache Camel Developer's Cookbook, 2nd Edition_](http://www.packtpub.com/apache-camel-developers-cookbook/book)
(Packt Publishing, TBD 2017) by [Scott Cranton](https://github.com/scranton), [Jakub Korab](https://github.com/jkorab), and [Christian Posta](https://github.com/christian-posta).
The latest version of this code is available on [GitHub](http://github.com/CamelCookbook/camel-cookbook-examples).

*This project is up to date with [Apache Camel 2.20.0](http://camel.apache.org/camel-2200-release.html).*

All of the examples are driven through JUnit tests, and are collectively structured as a set
of Apache Maven projects. To execute them, you will need a copy of the [Java 8 JDK](http://openjdk.java.net/install/)
and an [Apache Maven 3](http://maven.apache.org/) installation. 
Maven will download all of the appropriate project dependencies.

In order to execute all the tests, all you need to do is run:

    $ mvn clean install

You will find the sample code laid out for the chapters as follows:

1. Structuring Routes - `01-structuring-routes`
2. Message Routing - `02-routing`
3. Routing to your Code - `03-extend`
4. Rest - `04-rest`
5. Transformation - `05-transformation`
6. Splitting and Aggregating - `06-split-join`
7. Parallel Processing - `07-parallel-processing`
8. Error Handling and Compensation - `08-error`
9. Transactions and Idempotency - `09-transactions`
10. Runtimes - `10-runtimes`
11. Testing - `11-testing`
12. Monitoring and Debugging - `12-monitoring`
13. Security - `13-security`
14. Web Services - `14-web-services`
