/*
 * Originally (c) 2014 Dmitry Leskov, http://www.dmitryleskov.com
 * Released into the public domain under the Unlicense, http://unlicense.org
 */

package com.dmitryleskov.jmhproject1;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.*;
import org.openjdk.jmh.runner.options.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class BinaryToSimpleInsertionCutoff {

    @Param({"sorted", "reverse", "random"})
    public String test;

    final public int chunkSize = 12;

    @Param({"4", "5", "6", "7"})
    public int cutoff;

    public static final int problemSize = 1024*1024;
    
    public TestDataGenerator data;
    
    public Integer[] integerData;
    public String[] stringData;

    private Comparable[] a, aux;
    
    @Setup
    public void init() {
        data = new TestDataGenerator(problemSize);
        stringData = TestDataGenerator.asStringArray(data.getIntData(test));
        a = new String[problemSize];
        aux = new String[problemSize];
    }
    
//    @GenerateMicroBenchmark
    public Comparable[] testArrayCopy() {
        System.arraycopy(stringData, 0, a, 0, problemSize);
        return a;
    }
    
    public interface Sorter {
        public void sort(Comparable[] a, int lo, int hi);
    }
    
    private class Insertion implements Sorter {
        public void sort(Comparable[] a, int lo, int hi) {
            for (int i = lo; i <= hi; i++)
                for (int j = i; j > lo && less(a[j], a[j-1]); j--)
                    exch(a, j, j-1);
            assert isSorted(a, lo, hi);
        }
    }    

    private class InsertionX implements Sorter {

        public void sort(Comparable[] a, int lo, int hi) {
            for (int i = lo; i <= hi; i++) {
                Comparable v = a[i];
                int j;
                for (j = i - 1; j >= lo && less(v, a[j]); j--);
                j++;
                if (j < i) {
                    for (int k = i; k > j; k--) {
                        a[k] = a[k - 1];
                    }
                    a[j] = v;
                }
            }
            assert isSorted(a, lo, hi);
        }
    }

    
    private class InsertionAC implements Sorter {

        public void sort(Comparable[] a, int lo, int hi) {
            for (int i = lo; i <= hi; i++) {
                Comparable v = a[i];
                int j;
                for (j = i - 1; j >= lo && less(v, a[j]); j--);
                j++;
                if (j < i) {
                    System.arraycopy(a, j, a, j + 1, i - j);
                    a[j] = v;
                }
            }
            assert isSorted(a, lo, hi);
        }
    }
    
    private class BinaryInsertion implements Sorter {
        @Override
        public void sort(Comparable[] a, int lo, int hi) {
            for (int i = lo + 1; i <= hi; i++) {
                Comparable v = a[i];
                int lolo = lo;
                int hihi = i - 1;
                while (lolo <= hihi) {
                    int mid = lolo + (hihi - lolo) / 2;
                    if (less(v, a[mid])) hihi = mid - 1;
                    else lolo = mid + 1;
                }
                System.arraycopy(a, lolo, a, lolo + 1, i - lolo);
                a[lolo] = v;
            }
            assert isSorted(a, lo, hi);
        }
    }    

    private class BinaryInsertionShortCircuit implements Sorter {
        @Override
        public void sort(Comparable[] a, int lo, int hi) {
            for (int i = lo + 1; i <= hi; i++) {
                if (less(a[i], a[i - 1])) {
                    Comparable v = a[i];
                    int lolo = lo;
                    int hihi = i - 2;
                    while (lolo <= hihi) {
                        int mid = lolo + (hihi - lolo) / 2;
                        if (less(v, a[mid])) hihi = mid - 1;
                        else lolo = mid+1;
                    }
                    System.arraycopy(a, lolo, a, lolo + 1, i - lolo);
                    a[lolo] = v;
                }
            }
            assert isSorted(a, lo, hi);
        }
    }    
    
    private class BinaryInsertionDoubleCompare implements Sorter {
        @Override
        public void sort(Comparable[] a, int lo, int hi) {
            for (int i = lo + 1; i <= hi; i++) {
                if (less(a[i], a[i - 1])) {
                    Comparable v = a[i];
                    int lolo = lo;
                    int hihi = i - 2;
                    while (lolo <= hihi) {
                        int mid = lolo + (hihi - lolo) / 2;
                        if (less(v, a[mid])) hihi = mid - 1;
                        else {
                            lolo = mid + 1;
                            if (less(v, a[lolo])) break;
                        }
                    }
                    System.arraycopy(a, lolo, a, lolo + 1, i - lolo);
                    a[lolo] = v;
                }
            }
            assert isSorted(a, lo, hi);
        }
    }    

    public class BinaryInsertionX implements Sorter {
        @Override
        public void sort(Comparable[] a, int lo, int hi) {
            int simple = hi < lo + cutoff ? hi : lo + cutoff; // cutoff position
            for (int i = lo + 1; i <= simple; i++) {
                Comparable v = a[i];
                int j = i-1;
                while (j >= lo && less(v, a[j])) {
                    a[j+1] = a[j];
                    j--;
                }
                a[j+1] = v;
            }
            for (int i = lo + cutoff + 1; i <= hi; i++) {
                if (less(a[i], a[i-1])) {
                    Comparable v = a[i];
                    int lolo = lo;
                    int hihi = i-2;
                    while (lolo <= hihi) {
                        int mid = lolo + (hihi - lolo) / 2;
                        if (less(v, a[mid])) hihi = mid - 1;
                        else lolo = mid+1;
                    }
                    System.arraycopy(a, lolo, a, lolo+1, i-lolo);
                    a[lolo] = v;
                }
            }
            assert isSorted(a, lo, hi);
        }
    }    
    
    
    @SuppressWarnings("unchecked")
    private static boolean less(Comparable a, Comparable b) {
        return (a.compareTo(b) < 0);
    }

    private static void exch(Comparable[] a, int i, int j) {
        Comparable swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

    @GenerateMicroBenchmark
    public Comparable[] testInsertion() {
        System.arraycopy(stringData, 0, a, 0, problemSize);
        Sorter sorter = new Insertion();
        for (int lo = 0; lo < problemSize-chunkSize; lo += chunkSize) {
            sorter.sort(a, lo, lo+chunkSize-1);
        }
        return a;
    }

    @GenerateMicroBenchmark
    public Comparable[] testInsertionX() {
        System.arraycopy(stringData, 0, a, 0, problemSize);
        Sorter sorter = new InsertionX();
        for (int lo = 0; lo < problemSize-chunkSize; lo += chunkSize) {
            sorter.sort(a, lo, lo+chunkSize-1);
        }
        return a;
    }
    @GenerateMicroBenchmark
    public Comparable[] testInsertionAC() {
        System.arraycopy(stringData, 0, a, 0, problemSize);
        Sorter sorter = new InsertionAC();
        for (int lo = 0; lo < problemSize-chunkSize; lo += chunkSize) {
            sorter.sort(a, lo, lo+chunkSize-1);
        }
        return a;
    }

    @GenerateMicroBenchmark
    public Comparable[] testBinaryInsertion() {
        System.arraycopy(stringData, 0, a, 0, problemSize);
        Sorter sorter = new BinaryInsertion();
        for (int lo = 0; lo < problemSize-chunkSize; lo += chunkSize) {
            sorter.sort(a, lo, lo+chunkSize-1);
        }
        return a;
    }

    @GenerateMicroBenchmark
    public Comparable[] testBinaryInsertionShortCircuit() {
        System.arraycopy(stringData, 0, a, 0, problemSize);
        Sorter sorter = new BinaryInsertionShortCircuit();
        for (int lo = 0; lo < problemSize-chunkSize; lo += chunkSize) {
            sorter.sort(a, lo, lo+chunkSize-1);
        }
        return a;
    }

    @GenerateMicroBenchmark
    public Comparable[] testBinaryInsertionDoubleCompare() {
        System.arraycopy(stringData, 0, a, 0, problemSize);
        Sorter sorter = new BinaryInsertionDoubleCompare();
        for (int lo = 0; lo < problemSize-chunkSize; lo += chunkSize) {
            sorter.sort(a, lo, lo+chunkSize-1);
        }
        return a;
    }

    @GenerateMicroBenchmark
    public Comparable[] testBinaryInsertionX() {
        System.arraycopy(stringData, 0, a, 0, problemSize);
        Sorter sorter = new BinaryInsertionX();
        for (int lo = 0; lo < problemSize-chunkSize; lo += chunkSize) {
            sorter.sort(a, lo, lo+chunkSize-1);
        }
        return a;
    }
    
    // is the array sorted from a[lo] to a[hi]?
    private static boolean isSorted(Comparable[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++)
            if (less(a[i], a[i-1])) return false;
        return true;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + BinaryToSimpleInsertionCutoff.class.getSimpleName() + ".*BinaryInsertionX.*")
                .forks(1)
                .jvmArgs("-server")
                .build();

        new Runner(opt).run();
    }    
    
}
