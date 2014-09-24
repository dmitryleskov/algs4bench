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
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class MyBenchmark {

//    @Param({"MergeX", "MergeXBinary"})
//    public String algorithm;


    private static final int problemSize = 100000;
    private static final String algorithm = "MergeXBinary";
    
    public TestDataGenerator data;
    
    public Integer[] integerData;
    
    // One cannot refer to classes in the default package from a named package,
    // hence all this fiddling with reflection
    public Method sortMethod;

    
    
    @Setup
    public void init() {
        data = new TestDataGenerator(problemSize);
        integerData = TestDataGenerator.asIntegerArray(data.getIntData("shuffled"));
        try {
            sortMethod = Class.forName(algorithm).getDeclaredMethod("sort", Comparable[].class);
        } catch (ClassNotFoundException ex) {
            System.out.println("No such class: " + algorithm);
        } catch (NoSuchMethodException ex) {
            System.out.println("Class " + algorithm + " has no static method sort(Comparable[])");
        }
    }

    public Comparable[] sort(Method m, Comparable[] a) {
        try {
            m.invoke(null, (Object) a);
        } catch (SecurityException |
                IllegalAccessException |
                IllegalArgumentException |
                InvocationTargetException ex) {
            System.out.println(ex.getMessage());
        }
        return a;
    }
   
    
    @Benchmark
    public Comparable[] testSort() {
        Comparable[] a = integerData.clone();
        sort(sortMethod, a);
        return a;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + MyBenchmark.class.getSimpleName() + ".*")
                .forks(1)
                .jvmArgs("-server")
                .build();

        new Runner(opt).run();
    }    
    
}
