package com.dmitryleskov.jmhproject1;

/*************************************************************************
 *  Compilation:  javac BinaryInsertionX.java
 *  Execution:    java BinaryInsertionX < input.txt
 *  Dependencies: StdOut.java StdIn.java
 *  Data files:   http://algs4.cs.princeton.edu/21sort/tiny.txt
 *                http://algs4.cs.princeton.edu/21sort/words3.txt
 *
 *  Sorts a sequence of strings from standard input using insertion sort.
 *
 *  % more tiny.txt
 *  S O R T E X A M P L E
 *
 *  % java BinaryInsertionX < tiny.txt
 *  A E E L M O P R S T X                 [ one string per line ]
 *
 *  % more words3.txt
 *  bed bug dad yes zoo ... all bad yet
 *
 *  % java BinaryInsertionX < words3.txt
 *  all bad bed bug dad ... yes yet zoo   [ one string per line ]
 *
 *************************************************************************/

import java.util.Comparator;

/**
 *  The <tt>BinaryInsertionX</tt> class provides static methods for sorting an
 *  array using binary insertion sort.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/21elementary">Section 2.1</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  @author Dmitry Leskov
 */
public class BinaryInsertionX {

    // This class should not be instantiated.
    private BinaryInsertionX() { }

    /**
     * Rearranges the array in ascending order, using the natural order.
     * @param a the array to be sorted
     */
    public static void sort(Comparable[] a) {
        if (a.length > 1) sort(a, 0, a.length - 1);
    }
    
    /**
     * Rearranges an array chunk in ascending order, using the natural order.
     * @param a the array to be sorted
     * @param lo index of the first element of the chunk
     * @param hi index of the last element of the chunk
     */
    public static void sort(Comparable[] a, int lo, int hi) {
        checkRange(a.length, lo, hi);
        for (int i = lo + 1; i <= hi; i++) {
            if (less(a[i], a[i-1])) {
                int k = binarySearch(a, lo, i-1);
                Comparable v = a[i];
                for (int j = i; j > k; j--) {
                    a[j] = a[j-1];
                }
                a[k] = v;
            }
            assert isSorted(a, lo, i);
        }
        assert isSorted(a, lo, hi);
    }
    
    /**
     * Rearranges the array in ascending order, using a comparator.
     * @param a the array
     * @param c the comparator specifying the order
     */
    public static void sort(Object[] a, Comparator c) {
        if (a.length > 1) sort(a, c, 0, a.length - 1);
    }

    /**
     * Rearranges the array in ascending order, using a comparator.
     * @param a the array
     * @param c the comparator specifying the order
     * @param lo index of the first element of the chunk
     * @param hi index of the last element of the chunk
     */
    public static void sort(Object[] a, Comparator c, int lo, int hi) {
        checkRange(a.length, lo, hi);
        for (int i = lo+1; i <= hi; i++) {
            if (less(c, a[i], a[i - 1])) {
                int k = binarySearch(a, c, i);
                Object v = a[i];
                for (int j = i; j > k; j--) {
                    a[j] = a[j-1];
                }
                a[k] = v;
            }
            assert isSorted(a, c, lo, i);
        }
        assert isSorted(a, c, lo, hi);
    }
    
    // return a permutation that gives the elements in a[] in ascending order
    // do not change the original array a[]
    /**
     * Returns a permutation that gives the elements in the array in ascending order.
     * @param a the array
     * @return a permutation <tt>p[]</tt> such that <tt>a[p[0]]</tt>, <tt>a[p[1]]</tt>,
     *    ..., <tt>a[p[N-1]]</tt> are in ascending order
     */
    public static int[] indexSort(Comparable[] a) {
        int N = a.length;
        int[] index = new int[N];
        for (int i = 0; i < N; i++)
            index[i] = i;

        for (int i = 1; i < N; i++) {
            if (less(a[index[i]], a[index[i-1]])) {
                int k = binarySearch(a, index, i);
                int v = index[i];
                for (int j = i; j > k; j--) {
                    index[j] = index[j-1];
                }
                index[k] = v;
            }
            assert isSorted(a, index, 0, i);
        }
        assert isSorted(a, index);
        return index;
    }

    /***********************************************************************
    *  Binary search routines
    ***********************************************************************/
    
    // find the position for a[hi+1] between lo and hi using binary search
    private static int binarySearch(Comparable[] a, int lo, int hi) {
        Comparable key = a[hi+1];
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (less(key, a[mid])) hi = mid - 1;
            else if (less(key, a[mid + 1])) return mid + 1;
            else lo = mid + 1;
        }
        return lo;
    }

    // find the position for a[n] between 0 and n using binary search
    private static int binarySearch(Object[] a, Comparator c, int n) {
        Object key = a[n];
        int lo = 0;
        int hi = n - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (less(c, key, a[mid])) hi = mid - 1;
            else if (less(c, key, a[mid + 1])) return mid + 1;
            else lo = mid + 1;
        }
        return lo;
    }

    // find the position for a[index[n]] between a[index[0]] and a[index[n]] using binary search
    private static int binarySearch(Comparable[] a, int[] index, int n) {
        Comparable key = a[index[n]];
        int lo = 0;
        int hi = n - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (less(key, a[index[mid]])) hi = mid - 1;
            else if (less(key, a[index[mid + 1]])) return mid + 1;
            else lo = mid + 1;
        }
        return lo;
    }

    /**
     * Checks that a range of indices defining an array chunk is valid.
     * @param len array length
     * @param lo index of the first element of the chunk
     * @param hi index of the last element of the chunk
     * @throws IllegalArgumentException if lo > hi
     * @throws ArrayIndexOutOfBoundsException if either lo or hi 
     * are not valid indexes of an array of length len.
     */
    private static void checkRange(int len, int lo, int hi) {
        if (lo > hi)
            throw new IllegalArgumentException("lo(" + lo + ") > hi(" + hi + ")");
        else if (lo < 0)
            throw new ArrayIndexOutOfBoundsException(lo);
        else if (hi >= len)
            throw new ArrayIndexOutOfBoundsException(hi);
    }

   /***********************************************************************
    *  Helper sorting functions
    ***********************************************************************/

    // is v < w ?
    // v and w are elements of the same array, so suppressing warnings is safe
    @SuppressWarnings("unchecked")
    private static boolean less(Comparable v, Comparable w) {
        return (v.compareTo(w) < 0);
    }

    // is v < w ?
    // v and w are elements of the same array, so suppressing warnings is safe
    @SuppressWarnings("unchecked")
    private static boolean less(Comparator c, Object v, Object w) {
        return (c.compare(v, w) < 0);
    }


   /***********************************************************************
    *  Check if array is sorted - useful for debugging
    ***********************************************************************/
    private static boolean isSorted(Comparable[] a) {
        return isSorted(a, 0, a.length - 1);
    }

    // is the array sorted from a[lo] to a[hi]
    private static boolean isSorted(Comparable[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++)
            if (less(a[i], a[i-1])) return false;
        return true;
    }

    private static boolean isSorted(Object[] a, Comparator c) {
        return isSorted(a, c, 0, a.length - 1);
    }

    // is the array sorted from a[lo] to a[hi]
    private static boolean isSorted(Object[] a, Comparator c, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++)
            if (less(c, a[i], a[i-1])) return false;
        return true;
    }

    private static boolean isSorted(Comparable[] a, int[] index) {
        return isSorted(a, index, 0, a.length - 1);
    }

    // is the array sorted from a[lo] to a[hi]
    private static boolean isSorted(Comparable[] a, int[] index, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++)
            if (less(a[index[i]], a[index[i-1]])) return false;
        return true;
    }


//   // print array to standard output
//    private static void show(Comparable[] a) {
//        for (int i = 0; i < a.length; i++) {
//            StdOut.println(a[i]);
//        }
//    }
//
//    /**
//     * Reads in a sequence of strings from standard input; insertion sorts them;
//     * and prints them to standard output in ascending order.
//     */
//    public static void main(String[] args) {
//        String[] a = StdIn.readAllStrings();
//        BinaryInsertion.sort(a);
//        show(a);
//    }
}
