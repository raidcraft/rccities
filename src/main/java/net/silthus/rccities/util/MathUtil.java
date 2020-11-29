package net.silthus.rccities.util;

import java.util.Random;

/**
 * @author Silthus
 */
public final class MathUtil {

    public static final Random RANDOM = new Random();

    public static double toPercent(double percent) {

        return ((int) ((percent * 100.0) * 100)) / 100.0;
    }

    public static double trim(double number) {

        return ((int) (100.0 * number)) / 100.0;
    }

    public static int[] getDigits(double value, int commaCount) {

        char[] c_values = ("" + (int) (value * Math.pow(10, commaCount)))
                .toCharArray();
        int[] values = new int[c_values.length];
        for (int i = 0; i < c_values.length; i++) {
            values[i] = Character.digit(c_values[i], 10);
        }
        return values;
    }
}