package MergeSortThreads;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class MergeSortThreadsDivideTest {
    @Test
    public void miniSortTest() {
        Integer[] integers = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        new MergeSortThreadsDivide<Integer>(integers);
        Assert.assertTrue(checkSorted(integers));
    }

    @Test
    public void mediumSortTest() {
        Integer[] integers = new Integer[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21};
        new MergeSortThreadsDivide<Integer>(integers);
        Assert.assertTrue(checkSorted(integers));
    }

    @Test
    public void largeSortTest() {
        Integer[] integers = new Integer[]{7, 4, 1, 10, 0, 8, 14, 15, 3, 9, 5, 6, 11, 12, 2, 13,
                -1, -12, -13, -14, -15, -2, -3, -16, -4, -5, -10, -11, -6, -7, -8, -9};
        new MergeSortThreadsDivide<Integer>(integers);
        Assert.assertTrue(checkSorted(integers));
    }

    @Test
    public void randomTest() {
        Random random = new Random();
        Integer[] integers = new Integer[random.nextInt(20, 100000)];
        for (int i = 0; i < integers.length; i++) {
            integers[i] = random.nextInt();
        }
        new MergeSortThreadsDivide<Integer>(integers);
        boolean out = checkSorted(integers);
        Assert.assertTrue(out);
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
