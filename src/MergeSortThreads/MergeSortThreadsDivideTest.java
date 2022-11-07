package MergeSortThreads;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

public class MergeSortThreadsDivideTest {
    @Test
    public void miniSortTest() {
        Integer[] integers = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        MergeSortThreadsDivide<Integer> mergeSortThreadsDivide = new MergeSortThreadsDivide<>(integers);
        mergeSortThreadsDivide.startSort();
        Assert.assertTrue(checkSorted(integers));
    }

    @Test
    public void mediumSortTest() {
        Integer[] integers = new Integer[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21};
        MergeSortThreadsDivide<Integer> mergeSortThreadsDivide = new MergeSortThreadsDivide<>(integers);
        mergeSortThreadsDivide.startSort();
        Assert.assertTrue(checkSorted(integers));
    }

    @Test
    public void largeSortTest() {
        Integer[] integers = new Integer[]{7, 4, 1, 10, 0, 8, 14, 15, 3, 9, 5, 6, 11, 12, 2, 13,
                -1, -12, -13, -14, -15, -2, -3, -16, -4, -5, -10, -11, -6, -7, -8, -9};
        MergeSortThreadsDivide<Integer> mergeSortThreadsDivide = new MergeSortThreadsDivide<>(integers);
        mergeSortThreadsDivide.startSort();
        Assert.assertTrue(checkSorted(integers));
    }

    @Test
    public void randomTest() {
        Random random = new Random();
        Integer[] integers = new Integer[random.nextInt(20, 100000)];
        for (int i = 0; i < integers.length; i++) {
            integers[i] = random.nextInt();
        }
        MergeSortThreadsDivide<Integer> mergeSortThreadsDivide = new MergeSortThreadsDivide<>(integers);
        mergeSortThreadsDivide.startSort();
        boolean out = checkSorted(integers);
        Assert.assertTrue(out);
    }

    @Test
    public void randomBruteForceTest() {
        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            Integer[] integers = new Integer[random.nextInt(100, 100000)];
            for (int f = 0; f < integers.length; f++) {
                integers[f] = random.nextInt();
            }
            int threads = random.nextInt(3,20);
            MergeSortThreadsDivide<Integer> mergeSortThreadsDivide = new MergeSortThreadsDivide<>(integers, threads);
            mergeSortThreadsDivide.startSort();
            boolean out = checkSorted(integers);
            if (!out)
                Assert.fail("Test " + i + " failed with " + threads + " threads and an output of:\n" + Arrays.toString(integers));
        }
    }

    boolean checkSorted(Comparable[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i - 1].compareTo(array[i]) > 0) {
                return false;
            }
        }
        return true;
    }
}
