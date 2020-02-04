package edu.utep.poragchowdhury.core;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Utilities {
    /**
     * Converts a string of the form a/b (fraction) into a double
     * @param fString String to convert
     * @return double representing a/b
     */
    public static double parseFractionString(String fString) {
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

    public static double distance(double[] eq1, double[] eq2) {
        double dist = 0;
        int commonLength = Math.min(eq1.length, eq2.length);
        for (int i = 0; i < commonLength; i++)
            dist += (eq1[i] - eq2[i]) * (eq1[i] - eq2[i]);

        dist = Math.sqrt(dist);
        return dist;
    }

    public static int countZeros(double[] arr) {
        int zeroCount = 0;
        for (double d : arr) {
            if (d == 0.0d)
                zeroCount++;
        }
        return zeroCount;
    }

    public static int argMinZeros(List<double[]> lst) {
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

    public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
        Comparator<K> valueComparator = new Comparator<K>() {
            public int compare(K k1, K k2) {
                int compare = map.get(k1).compareTo(map.get(k2));
                if (compare == 0)
                    return 1;
                else
                    return compare;
            }
        };

        Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
        sortedByValues.putAll(map);
        return sortedByValues;
    }
}
