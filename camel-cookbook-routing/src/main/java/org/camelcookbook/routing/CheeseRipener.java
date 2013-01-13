package org.camelcookbook.routing;

public class CheeseRipener {
    public static void ripen(Cheese cheese) {
        cheese.setAge(cheese.getAge() + 1);
    }
}
