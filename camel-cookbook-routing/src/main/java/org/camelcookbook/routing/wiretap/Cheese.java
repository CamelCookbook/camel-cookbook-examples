package org.camelcookbook.routing.wiretap;

/**
 * Simple POJO that allows for leaky state between threads.
 */
public class Cheese implements Cloneable {
    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Cheese clone() {
        Cheese cheese = new Cheese();
        cheese.setAge(this.getAge());
        return cheese;
    }
}
