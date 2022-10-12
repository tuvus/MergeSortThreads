package MergeSortThreads;

import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;

import java.util.Random;

public class MergeSortThreadsTest {
    @Test
    public void sortTest() {
        Integer[] integers = new Integer[6];
        integers[0] = 1;
        integers[1] = -1;
        integers[2] = 0;
        integers[3] = 2;
        integers[4] = -2;
        integers[5] = 3;
        MergeSortThreads.sortArray(integers);
        Assert.assertTrue(checkSorted(integers));
    }

    @Test
    public void randomTest() {
        Assert.fail();
        Random random = new Random();
        Integer[] integers = new Integer[random.nextInt(100)];
        for (int i = 0; i < integers.length;i++) {
            integers[i] = random.nextInt();
        }
        MergeSortThreads.sortArray(integers);
        Assert.assertTrue(checkSorted(integers));
    }

    boolean checkSorted(Comparable[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i-1].compareTo(array[i]) <= 0) {
                return false;
            }
        }
        return true;
    }
}
