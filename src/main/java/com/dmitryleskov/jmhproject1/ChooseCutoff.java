/*
 * Copyright (c) 2005, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.dmitryleskov.jmhproject1;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.*;
import org.openjdk.jmh.runner.options.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class ChooseCutoff {

//    @Param({"MergeX", "MergeXBinary"})
//    public String algorithm;

//    @Param({"2", "4", "8", "16", "32", "64", "128"})
    //@Param({"2", "4", "5", "6", "7", "8", "16"})
//    @Param({"8", "9", "10", "12"})
    public int chunkSize=8;

    public static final int problemSize = 1024*1024;
    
    public TestDataGenerator data;
    
    public Integer[] integerData;
    public String[] stringData;

    private Comparable[] a, aux;
    
    @Setup
    public void init() {
        data = new TestDataGenerator(problemSize);
        integerData = TestDataGenerator.asIntegerArray(data.getIntData("reverse"));
        stringData = TestDataGenerator.asStringArray(data.getIntData("reverse"));
        a = new String[problemSize];
        aux = new String[problemSize];
    }
    
//    @GenerateMicroBenchmark
    public Comparable[] testArrayCopy() {
        System.arraycopy(stringData, 0, a, 0, problemSize);
        return a;
    }

    private void lastMerge(int lo, int hi) {
        System.arraycopy(a, lo, aux, lo, hi-lo+1);
        int mid = lo + (hi - lo) / 2;
        insertionSort(aux, lo, mid);
        insertionSort(aux, mid+1, hi);
        int i = lo, j = mid+1;
        for (int k = lo; k <= hi; k++) {
            if      (i > mid)              a[k] = aux[j++];
            else if (j > hi)               a[k] = aux[i++];
            else if (less(aux[j], aux[i])) a[k] = aux[j++];   // to ensure stability
            else                           a[k] = aux[i++];
        }
    }
    
    private static void insertionSort(Comparable[] a, int lo, int hi) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(a[j], a[j-1]); j--)
                exch(a, j, j-1);
    }
    

    private static void insertionSortX(Comparable[] a, int lo, int hi) {
        for (int i = lo; i <= hi; i++) {
            Comparable v = a[i];
            int j;
            for (j = i - 1; j >= lo && less(v, a[j]); j--);
            j++;
            if (j < i) {
                for (int k = i; k > j; k--) a[k] = a[k-1];
                a[j] = v;
            }
        }
    }

    
    
    private static void insertionSortAC(Comparable[] a, int lo, int hi) {
        for (int i = lo; i <= hi; i++) {
            Comparable v = a[i];
            int j;
            for (j = i - 1; j >= lo && less(v, a[j]); j--);
            j++;
            if (j < i) {
                System.arraycopy(a, j, a, j+1, i-j);
                a[j] = v;
            }
        }
    }

    public static void binaryInsertionSort(Comparable[] a, int lo, int hi) {
//        System.out.printf("lo=%d, hi=%d\n", lo, hi);
        for (int i = lo + 1; i <= hi; i++) {
        
//        for (int i = lo + 1; i <= hi; i++) {
            if (less(a[i], a[i-1])) {
//                int k = binarySearch(a, lo, i-1);
                Comparable v = a[i];
                int lolo = lo;
                int hihi = i-1;
                int k = lo;
                while (lolo <= hihi) {
                    int mid = lolo + (hihi - lolo) / 2;
                    if (less(v, a[mid])) hihi = mid - 1;
                    else if (less(v, a[mid+1])) { k = mid+1; break;}
                    else lolo=mid+1;
                }
                
//                for (int j = i; j > k; j--) {
//                    a[j] = a[j-1];
//                }
                System.arraycopy(a, k, a, k+1, i-k);
                a[k] = v;
            }
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
        for (int lo = 0; lo < problemSize-chunkSize; lo += chunkSize) {
            insertionSort(a, lo, lo+chunkSize-1);
        }
        return a;
    }

    @GenerateMicroBenchmark
    public Comparable[] testMergeI() {
        System.arraycopy(stringData, 0, a, 0, problemSize);
        for (int lo = 0; lo < problemSize-chunkSize; lo += chunkSize) {
            lastMerge(lo, lo+chunkSize-1);
        }
        return a;
    }

    @GenerateMicroBenchmark
    public Comparable[] testInsertionX() {
        System.arraycopy(stringData, 0, a, 0, problemSize);
        for (int lo = 0; lo < problemSize-chunkSize; lo += chunkSize) {
            insertionSortX(a, lo, lo+chunkSize-1);
        }
        return a;
    }

    
    @GenerateMicroBenchmark
    public Comparable[] testInsertionAC() {
        System.arraycopy(stringData, 0, a, 0, problemSize);
        for (int lo = 0; lo < problemSize-chunkSize; lo += chunkSize) {
            insertionSortAC(a, lo, lo+chunkSize-1);
        }
        return a;
    }
    
    
    @GenerateMicroBenchmark
    public Comparable[] testBinaryInsertion() {
        System.arraycopy(stringData, 0, a, 0, problemSize);
        for (int lo = 0; lo < problemSize-chunkSize; lo += chunkSize) {
            binaryInsertionSort(a, lo, lo+chunkSize-1);
        }
        return a;
    }

//    @GenerateMicroBenchmark
    public Comparable[] testMerge() {
        Comparable[] a = stringData.clone();
        for (int i = 0; i <= 10000/problemSize; i++) {
            Merge.sort(a);
            System.arraycopy(stringData, 0, a, 0, problemSize);
        }
        return a;
    }

//    @GenerateMicroBenchmark
    public Comparable[] testMergeX() {
        Comparable[] a = stringData.clone();
        for (int i = 0; i <= 10000/problemSize; i++) {
            MergeX.sort(a);
            System.arraycopy(stringData, 0, a, 0, problemSize);
        }
        return a;
    }

//    @GenerateMicroBenchmark
    public Comparable[] testMergeXBinary() {
        Comparable[] a = stringData.clone();
        for (int i = 0; i <= 10000/problemSize; i++) {
            MergeX.sort(a);
            System.arraycopy(stringData, 0, a, 0, problemSize);
        }
        return a;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + ChooseCutoff.class.getSimpleName() + ".*")
                .forks(1)
                .jvmArgs("-server")
                .build();

        new Runner(opt).run();
    }    
    
}
