//public and static means they can be accessed be other classes within
//framework without instantiating a new instance of the object
//i.e System.out.println(RandomNumberGenerator.get.....)
package com.megamal.framework.util;

import java.util.Random;

/**
 * Created by malberbatovci on 24/09/15.
 */

//class that creates a single static Random object that will be shared across the
//entire application
public class RandomNumberGenerator {

    private static Random rand = new Random();

    public static int getRandIntBetween(int lowerBound, int upperBound) {
        return rand.nextInt(upperBound - lowerBound) + lowerBound;
    }

    public static int getRandInt(int upperBound) {
        return rand.nextInt(upperBound);
    }
}
