package com.dmitryleskov.algs4bench;

/*************************************************************************
 *  Compilation:  javac MergeXBinary.java
 *  Execution:    java MergeXBinary < input.txt
 *  Dependencies: StdOut.java StdIn.java
 *  Data files:   http://algs4.cs.princeton.edu/22mergesort/tiny.txt
 *                http://algs4.cs.princeton.edu/22mergesort/words3.txt
 *   
 *  Sorts a sequence of strings from standard input using an
 *  optimized version of mergesort.
 *   
 *  % more tiny.txt
 *  S O R T E X A M P L E
 *
 *  % java MergeX < tiny.txt
 *  A E E L M O P R S T X                 [ one string per line ]
 *    
 *  % more words3.txt
 *  bed bug dad yes zoo ... all bad yet
 *  
 *  % java MergeX < words3.txt
 *  all bad bed bug dad ... yes yet zoo    [ one string per line ]
 *
 *************************************************************************/

/**
 *  The <tt>MergeXBinary</tt> class provides static methods for sorting an
 *  array using an optimized version of mergesort.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/21elementary">Section 2.1</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  @author Dmitry Leskov
 */
public class MergeXBinary {
    private static final int CUTOFF = 15;  // cutoff to binary insertion sort

    // This class should not be instantiated.
    private MergeXBinary() { }

    private static void merge(Comparable[] src, Comparable[] dst, int lo, int mid, int hi) {

        // precondition: src[lo .. mid] and src[mid+1 .. hi] are sorted subarrays
        assert isSorted(src, lo, mid);
        assert isSorted(src, mid+1, hi);

        int i = lo, j = mid+1;
        for (int k = lo; k <= hi; k++) {
            if      (i > mid)              dst[k] = src[j++];
            else if (j > hi)               dst[k] = src[i++];
            else if (less(src[j], src[i])) dst[k] = src[j++];   // to ensure stability
            else                           dst[k] = src[i++];
        }

        // postcondition: dst[lo .. hi] is sorted subarray
        assert isSorted(dst, lo, hi);
    }
    
    private static void sort(Comparable[] src, Comparable[] dst, int lo, int hi) {
        // if (hi <= lo) return;
        if (hi <= lo + CUTOFF) { 
//            BinaryInsertionX.sort(dst, lo, hi);
//            binaryInsertionSort(dst, lo, hi);
            insertionSort(dst, lo, hi);
            return;
        }
        int mid = lo + (hi - lo) / 2;
        sort(dst, src, lo, mid);
        sort(dst, src, mid+1, hi);

        // if (!less(src[mid+1], src[mid])) {
        //    for (int i = lo; i <= hi; i++) dst[i] = src[i];
        //    return;
        // }

        // using System.arraycopy() is a bit faster than the above loop
        if (!less(src[mid+1], src[mid])) {
            System.arraycopy(src, lo, dst, lo, hi - lo + 1);
            return;
        }

        merge(src, dst, lo, mid, hi);
    }

    /**
     * Rearranges the array in ascending order, using the natural order.
     * @param a the array to be sorted
     */
    public static void sort(Comparable[] a) {
        Comparable[] aux = a.clone();
        sort(aux, a, 0, a.length-1);  
        assert isSorted(a);
    }

    // sort from a[lo] to a[hi] using insertion sort
    private static void insertionSort(Comparable[] a, int lo, int hi) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(a[j], a[j-1]); j--)
                exch(a, j, j-1);
    }
    
    public static void binaryInsertionSort(Comparable[] a, int lo, int hi) {
        for (int i = lo + 1; i <= (hi < lo + 7 ? hi : lo + 7); i++) {
            for (int j = i; j > lo && less(a[j], a[j-1]); j--)
                exch(a, j, j-1);
        }
        
        for (int i = lo + 8; i <= hi; i++) {
        
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
            assert isSorted(a, lo, i);
        }
        assert isSorted(a, lo, hi);
    }

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

    // exchange a[i] and a[j]
    private static void exch(Comparable[] a, int i, int j) {
        Comparable swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }
    
    // is a[i] < a[j]?
    // a and b are actually elements of the same array, so suppressing warnings is safe
//    @SuppressWarnings("unchecked")
    private static boolean less(Comparable a, Comparable b) {
        return (a.compareTo(b) < 0);
    }

   /***********************************************************************
    *  Check if array is sorted - useful for debugging
    ***********************************************************************/
    private static boolean isSorted(Comparable[] a) {
        return isSorted(a, 0, a.length - 1);
    }

    private static boolean isSorted(Comparable[] a, int lo, int hi) {
        for (int i = lo + 1; i <= hi; i++)
            if (less(a[i], a[i-1])) return false;
        return true;
    }

//    // print array to standard output
//    private static void show(Comparable[] a) {
//        for (int i = 0; i < a.length; i++) {
//            StdOut.println(a[i]);
//        }
//    }
//
//    /**
//     * Reads in a sequence of strings from standard input; mergesorts them
//     * (using an optimized version of mergesort); 
//     * and prints them to standard output in ascending order. 
//     */
//    public static void main(String[] args) {
//        String[] a = StdIn.readAllStrings();
//        MergeXBinary.sort(a);
//        show(a);
//    }
}
