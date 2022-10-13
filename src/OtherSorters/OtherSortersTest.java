package OtherSorters;

import SortBenchmarks.SorterBenchmarks;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class OtherSortersTest {
    @Test
    public void mergeSortTest() {
        Integer[] integers = new Integer[6];
        integers[0] = 1;
        integers[1] = -1;
        integers[2] = 0;
        integers[3] = 2;
        integers[4] = -2;
        integers[5] = 3;
        MergeSort.sortArray(integers);
        Assert.assertTrue(checkSorted(integers));
    }

    @Test
    public void mergeSortRandomTest() {
        Integer[] integers = getRandomIntegers();
        MergeSort.sortArray(integers);
        Assert.assertTrue(checkSorted(integers));
    }

    @Test
    public void mergeSortLessAloccTest() {
        Integer[] integers = new Integer[6];
        integers[0] = 1;
        integers[1] = -1;
        integers[2] = 0;
        integers[3] = 2;
        integers[4] = -2;
        integers[5] = 3;
        MergeSortLessAlocc.sortArray(integers);
        Assert.assertTrue(checkSorted(integers));
    }

    @Test
    public void mergeSortLessAloccRandomTest() {
        Integer[] integers = getRandomIntegers();
        MergeSortLessAlocc.sortArray(integers);
        Assert.assertTrue(checkSorted(integers));
    }

    boolean checkSorted(Comparable[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i-1].compareTo(array[i]) > 0) {
                return false;
            }
        }
        return true;
    }

    Integer[] getRandomIntegers() {
        Random random = new Random();
        Integer[] integers = new Integer[random.nextInt(20,1000)];
        for (int i = 0; i < integers.length;i++) {
            integers[i] = random.nextInt();
        }
        return integers;
    }
}
