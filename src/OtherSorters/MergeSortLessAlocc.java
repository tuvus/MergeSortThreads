package OtherSorters;


/**
 * MergeSortThreadsLessAlocc is a static class that sorts an array.
 * MergeSortThreadsLessAlocc uses the classical MergeSort algorithm using multithreading.
 * It also only uses two arrays instead of recursively creating more arrays.
 * The space complexity is O(2) rather than O(n)
 *
 * @author Oskar
 */
public class MergeSortLessAlocc {
    /**
     * Sorts the array using mergesort and only a space complexity of O(2).
     *
     * @param array the array to sort
     * @param <E>   the type of element to sort
     */
    public static <E extends Comparable<? super E>> void sortArray(E[] array) {
        mergeSortRec(array, array.clone(), 0, array.length - 1);
    }

    /**
     * Recursively sorts the individual section alternating what arrays are the output and copy.
     *
     * @param output the array that should be sorted at between the indices given
     * @param copy   the array with the given values
     * @param lower  the lower index to be sorted
     * @param upper  the upper index to be sorted
     * @param <E>    the type of element to sort
     */
    private static <E extends Comparable<? super E>> void mergeSortRec(E[] output, E[] copy, int lower, int upper) {
        if (upper - lower <= 0)
            return;
        mergeSortRec(copy, output, lower, lower + ((upper - lower) / 2));
        mergeSortRec(copy, output, lower + ((upper - lower) / 2) + 1, upper);
        merge(output, copy, lower, lower + ((upper - lower) / 2), lower + ((upper - lower) / 2) + 1, upper);
    }

    /**
     * Merges the two parts of the array together in sorted order
     *
     * @param output the array that should end up sorted
     * @param copy   the half sorted array with the values to sort
     * @param lower  the lower index to be sorted of the first part
     * @param upper  the upper index to be sorted of the first part
     * @param lower2 the lower index to be sorted of the second part
     * @param upper2 the upper index to be sorted of the second part
     * @param <E>    the type of element to sort
     */
    private static <E extends Comparable<? super E>> void merge(E[] output, E[] copy, int lower, int upper, int lower2, int upper2) {
        int lhs = lower, rhs = lower2;
        int index = lower;
        while (lhs <= upper && rhs <= upper2) {
            if (copy[lhs].compareTo(copy[rhs]) <= 0) {
                output[index] = copy[lhs];
                lhs++;
            } else {
                output[index] = copy[rhs];
                rhs++;
            }
            index++;
        }
        if (lhs > upper)
            System.arraycopy(copy, rhs, output, index, upper2 - index + 1);
        else
            System.arraycopy(copy, lhs, output, index, upper2 - index + 1);
    }
}