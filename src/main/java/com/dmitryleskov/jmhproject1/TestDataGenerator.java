package com.dmitryleskov.jmhproject1;


import java.util.HashMap;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author Dmitry Leskov
 */
public class TestDataGenerator {

    private HashMap<String, int[]> data;
            
    public TestDataGenerator(int problemSize) {
    
        data = new HashMap<>();

        int[] constant = new int[problemSize];
        for (int i = 0; i < problemSize; i++) {
            constant[i] = 1;
        }
        data.put("constant", constant);
        int[] interleaved = new int[problemSize];
        for (int i = 0; i < problemSize; i++) {
            interleaved[i] = i % 2;
        }
        data.put("interleaved", interleaved);
        int[] sorted = new int[problemSize];
        for (int i = 0; i < problemSize; i++) {
            sorted[i] = i;
        }
        data.put("sorted", sorted);
        int[] reverse = new int[problemSize];
        for (int i = 0; i < problemSize; i++) {
            reverse[i] = problemSize-i;
        }
        data.put("reverse", reverse);
        int[] inversions = new int[problemSize];
        for (int i = 0; i < problemSize; i++) {
            if (i % 2 == 0) {
                inversions[i] = i + 1;
            } else {
                inversions[i] = i - 1;
            }
        }
        data.put("inversions", inversions);
        Random r = new Random();
        int[] random = new int[problemSize];
        for (int i = 0; i < problemSize; i++) {
            random[i] = r.nextInt(problemSize < 20 ? 2 : problemSize/10);
        }
        data.put("random", random);
        int[] shuffled = new int[problemSize];
        for (int i = 0; i < problemSize; i++) {
            shuffled[i] = i;
        }
        for (int i = 0; i < problemSize; i++) {
            int j = i + r.nextInt(problemSize-i);
            int swap = shuffled[i];
            shuffled[i] = shuffled[j];
            shuffled[j] = swap;
        }
        data.put("shuffled", shuffled);
    }
    
    public Set<String> keySet() {
        return data.keySet();
    }
    
    public int[] getIntData(String key) {
        return data.get(key).clone();
    }
    
    public Integer[] getIntegerData(String key) {
        int[] intArray = data.get(key);
        Integer[] array = new Integer[intArray.length];
        for (int i = 0; i < intArray.length; i++) { array[i] = intArray[i]; }
        return array;
    }

    public void put(String key, int[] intArray) {
        data.put(key, intArray);
    }
    
    public static Integer[] asIntegerArray(int[] intArray) {
        Integer[] array = new Integer[intArray.length];
        for (int i = 0; i < intArray.length; i++) { array[i] = intArray[i]; }
        return array;
    }

    public static String[] asStringArray(int[] intArray) {
        String[] array = new String[intArray.length];
        for (int i = 0; i < intArray.length; i++) { 
            array[i] = String.format("0123456789%010d", intArray[i]); 
        }
        return array;
    }
    
}
