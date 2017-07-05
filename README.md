Apache Camel Developer's Cookbook, 2nd Edition Samples
======================================================

[![Build Status](https://travis-ci.org/CamelCookbook/camel-cookbook-examples.png?branch=camel-v2.19.x)](https://travis-ci.org/CamelCookbook/camel-cookbook-examples)
[![Stories in Ready](https://badge.waffle.io/CamelCookbook/camel-cookbook-examples.png?label=ready&title=Ready)](https://waffle.io/CamelCookbook/camel-cookbook-examples?utm_source=badge)

This project contains the sample code for the [_Apache Camel Developer's Cookbook, 2nd Edition_](http://www.packtpub.com/apache-camel-developers-cookbook/book)
(Packt Publishing, TBD 2017) by [Scott Cranton](https://github.com/scranton), [Jakub Korab](https://github.com/jkorab), and [Christian Posta](https://github.com/christian-posta).
The latest version of this code is available on [GitHub](http://github.com/CamelCookbook/camel-cookbook-examples).

*This project is up to date with [Apache Camel 2.19.2](http://camel.apache.org/camel-2192-release.html).*

All of the examples are driven through JUnit tests, and are collectively structured as a set
of Apache Maven projects. To execute them, you will need a copy of the [Java 8 JDK](http://openjdk.java.net/install/)
and an [Apache Maven 3](http://maven.apache.org/) installation. 
Maven will download all of the appropriate project dependencies.

In order to execute all the tests, all you need to do is run:

    $ mvn clean install

You will find the sample code laid out for the chapters as follows:

1. Structuring Routes - `camel-cookbook-structuring-routes`
2. Message Routing - `camel-cookbook-routing`
3. Routing to your Code - `camel-cookbook-extend`
4. Rest - `camel-cookbook-rest`
5. Transformation - `camel-cookbook-transformation`
6. Splitting and Aggregating - `camel-cookbook-split-join`
7. Parallel Processing - `camel-cookbook-parallel-processing`
8. Error Handling and Compensation - `camel-cookbook-error`
9. Transactions and Idempotency - `camel-cookbook-transactions`
10. Runtimes - `camel-cookbook-runtimes`
11. Testing - `camel-cookbook-testing`
12. Monitoring and Debugging - `camel-cookbook-monitoring`
13. Security - `camel-cookbook-security`
14. Web Services - `camel-cookbook-web-services`
