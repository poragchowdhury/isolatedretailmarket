package edu.utep.poragchowdhury.core;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Utilities {
    /**
     * Converts a string of the form a/b (fraction) into a double
     * @param fString String to convert
     * @return double representing a/b
     */
    public static double parseFractionString(@NotNull String fString) {
        try {
            // Try to parse it regularly first, it might not be a fraction after all
            return Double.parseDouble(fString);
        } catch (NumberFormatException ex) {
            String[] split = fString.split("/");
            double numerator = Double.parseDouble(split[0]);
            double denominator = Double.parseDouble(split[1]);
            return numerator / denominator;
        }
    }

    public static double distance(@NotNull double[] eq1, @NotNull double[] eq2) {
        double dist = 0;
        int commonLength = Math.min(eq1.length, eq2.length);
        for (int i = 0; i < commonLength; i++)
            dist += (eq1[i] - eq2[i]) * (eq1[i] - eq2[i]);

        dist = Math.sqrt(dist);
        return dist;
    }

    public static int countZeros(@NotNull double[] arr) {
        int zeroCount = 0;
        for (double d : arr) {
            if (d == 0.0d)
                zeroCount++;
        }
        return zeroCount;
    }

    public static int argMinZeros(@NotNull List<double[]> lst) {
        int resultIDX = 0;
        int lowestCount = countZeros(lst.get(0));
        for (int i = 1; i < lst.size(); i++) {
            double[] arr = lst.get(i);
            int zeroCount = countZeros(arr);
            if (zeroCount < lowestCount) {
                lowestCount = zeroCount;
                resultIDX = i;
            }
        }
        return resultIDX;
    }

    @NotNull
    public static <K, V extends Comparable<V>> Map<K, V> sortByValues(@NotNull final Map<K, V> map) {
        Comparator<K> valueComparator = (k1, k2) -> {
            int compare = map.get(k1).compareTo(map.get(k2));
            if (compare == 0)
                return 1;
            else
                return compare;
        };

        Map<K, V> sortedByValues = new TreeMap<>(valueComparator);
        sortedByValues.putAll(map);
        return sortedByValues;
    }

    /*
     * Generation random values between -max to +max
     */
    public static double getRandomValInRange(double max) {
        int divisor = 100;
        while (max % 1 != 0) {
            max *= 10;
            divisor *= 10;
        }
        int maxbound = (int) max * 100;
        Random r = new Random();
        int randInt = r.nextInt(maxbound + 1);
        double val = (double) randInt / divisor;
        int coin = r.nextInt(2);
        if (coin == 0)
            val *= -1;
        return val;
    }
}
