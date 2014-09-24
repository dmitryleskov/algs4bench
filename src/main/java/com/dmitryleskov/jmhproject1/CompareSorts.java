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
public class CompareSorts {

//    @Param({"MergeX", "MergeXBinary"})
//    public String algorithm;

//    @Param({"2", "4", "8", "16", "32", "64", "128"})
    @Param({"2", "32", "512"})
    public int problemSize;
    
    public TestDataGenerator data;
    
    public Integer[] integerData;
    public String[] stringData;
    
    @Setup
    public void init() {
        data = new TestDataGenerator(problemSize);
        integerData = TestDataGenerator.asIntegerArray(data.getIntData("sorted"));
        stringData = TestDataGenerator.asStringArray(data.getIntData("sorted"));
    }
    
//    @Benchmark
    public Comparable[] testArrayCopy() {
        Comparable[] a = stringData.clone();
        for (int i = 0; i <= 10000/problemSize; i++) {
            System.arraycopy(stringData, 0, a, 0, problemSize);
        }
        return a;
    }
    
//    @Benchmark
    public Comparable[] testInsertionX() {
        Comparable[] a = stringData.clone();
        for (int i = 0; i <= 10000/problemSize; i++) {
            InsertionX.sort(a);
            System.arraycopy(stringData, 0, a, 0, problemSize);
        }
        return a;
    }

    @Benchmark
    public Comparable[] testBinaryInsertionX() {
        Comparable[] a = stringData.clone();
        for (int i = 0; i <= 10000/problemSize; i++) {
            BinaryInsertionX.sort(a);
            System.arraycopy(stringData, 0, a, 0, problemSize);
        }
        return a;
    }

    @Benchmark
    public Comparable[] testMerge() {
        Comparable[] a = stringData.clone();
        for (int i = 0; i <= 10000/problemSize; i++) {
            Merge.sort(a);
            System.arraycopy(stringData, 0, a, 0, problemSize);
        }
        return a;
    }

    @Benchmark
    public Comparable[] testMergeX() {
        Comparable[] a = stringData.clone();
        for (int i = 0; i <= 10000/problemSize; i++) {
            MergeX.sort(a);
            System.arraycopy(stringData, 0, a, 0, problemSize);
        }
        return a;
    }

    @Benchmark
    public Comparable[] testMergeXBinary() {
        Comparable[] a = stringData.clone();
        for (int i = 0; i <= 10000/problemSize; i++) {
            MergeXBinary.sort(a);
            System.arraycopy(stringData, 0, a, 0, problemSize);
        }
        return a;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + CompareSorts.class.getSimpleName() + ".*")
                .forks(1)
                .jvmArgs("-server")
                .build();

        new Runner(opt).run();
    }    
    
}
