package org.camelcookbook.structuringroutes.javaapp;


import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringCamelApplication {
    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/SpringCamelApplication-context.xml");
        applicationContext.start();

        // let the Camel runtime do its job for 5 seconds
        Thread.sleep(5000);

        // shutdown
        applicationContext.stop();
    }
}
