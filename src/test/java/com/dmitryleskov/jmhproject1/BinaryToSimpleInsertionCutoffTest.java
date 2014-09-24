/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dmitryleskov.jmhproject1;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author snowman
 */
public class BinaryToSimpleInsertionCutoffTest {
    
    public BinaryToSimpleInsertionCutoffTest() {
    }

    private String[] sorted;
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        sorted = new String[16];
        for (int i = 0; i < sorted.length; i++) {
            sorted[i] = String.format("%09d", i);
        }
    }
    
    @After
    public void tearDown() {
    }

//    /**
//     * Test of init method, of class BinaryToSimpleInsertionCutoff.
//     */
//    @org.junit.Test
//    public void testInit() {
//        System.out.println("init");
//        BinaryToSimpleInsertionCutoff instance = new BinaryToSimpleInsertionCutoff();
//        instance.init();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of testArrayCopy method, of class BinaryToSimpleInsertionCutoff.
//     */
//    @org.junit.Test
//    public void testTestArrayCopy() {
//        System.out.println("testArrayCopy");
//        BinaryToSimpleInsertionCutoff instance = new BinaryToSimpleInsertionCutoff();
//        Comparable[] expResult = null;
//        Comparable[] result = instance.testArrayCopy();
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of testInsertion method, of class BinaryToSimpleInsertionCutoff.
//     */
//    @org.junit.Test
//    public void testTestInsertion() {
//        System.out.println("testInsertion");
//        BinaryToSimpleInsertionCutoff instance = new BinaryToSimpleInsertionCutoff();
//        Comparable[] expResult = null;
//        Comparable[] result = instance.testInsertion();
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of testBinaryInsertion method, of class BinaryToSimpleInsertionCutoff.
//     */
//    @org.junit.Test
//    public void testTestBinaryInsertion() {
//        System.out.println("testBinaryInsertion");
//        BinaryToSimpleInsertionCutoff instance = new BinaryToSimpleInsertionCutoff();
//        Comparable[] expResult = null;
//        Comparable[] result = instance.testBinaryInsertion();
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of testBinaryInsertionX method, of class BinaryToSimpleInsertionCutoff.
     */
    @org.junit.Test
    public void testBinaryInsertionX() {
        System.out.println("BinaryInsertionX");
        BinaryToSimpleInsertionCutoff instance = new BinaryToSimpleInsertionCutoff();
        Comparable[] expResult = sorted.clone();
        BinaryToSimpleInsertionCutoff.Sorter sorter = instance.new BinaryInsertionX();
        sorter.sort(sorted, 0, sorted.length-1);
        assertArrayEquals(expResult, sorted);
    }

    
}
