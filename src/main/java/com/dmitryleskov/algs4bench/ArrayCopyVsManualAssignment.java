/*
 * Copyright (c) 2014, Dmitry Leskov. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dmitryleskov.algs4bench;

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
public class ArrayCopyVsManualAssignment {

    @Param({"1", "2", "3", "4", "5", "6", "7"})
    public int chunkSize; // = 16;

    public static final int problemSize = 1024*1024;
    
    public TestDataGenerator data;
    
    public Integer[] integerData;
    public String[] stringData;

    private Comparable[] a, aux;
    
    @Setup
    public void init() {
        data = new TestDataGenerator(problemSize);
        stringData = TestDataGenerator.asStringArray(data.getIntData("sorted"));
        a = new String[problemSize];
    }
    
    @Benchmark
    public Comparable[] testArrayCopy() {
        for(int i = problemSize - chunkSize - 1; i >= 0; i -= chunkSize) {
            System.arraycopy(a, i, a, i+1, chunkSize);
        }
        return a;
    }

    @Benchmark
    public Comparable[] testManualCopy() {
        for(int i = problemSize - chunkSize - 1; i >= 0; i -= chunkSize) {
            for(int j = i + chunkSize - 1; j >= i; j--) a[j+1] = a[j];
        }
        return a;
    }
}
