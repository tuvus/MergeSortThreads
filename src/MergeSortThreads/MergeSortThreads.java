package MergeSortThreads;

public class MergeSortThreads {
    public static void sortArray(Comparable[] array) {
        mergeSortRec(array, array.clone(), 0, array.length - 1);
    }

    /**
     * @param output the array to return to
     * @param copy   the array with copied values
     * @param lower  the lower index to be sorted
     * @param upper  the upper index to be sorted
     */

    public static void mergeSortRec(Comparable[] output, Comparable[] copy, int lower, int upper) {
        if (upper - lower == 1)
            return;
        mergeSortRec(copy, output, lower, upper / 2);
        mergeSortRec(copy, output, upper / 2 + 1, upper);
        merge(output, copy, lower, upper / 2, upper, upper / 2 + 1);
    }

    public static void merge(Comparable[] output, Comparable[] copy, int lower, int upper, int lower2, int upper2) {
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
        if (lhs >= upper)
            System.arraycopy(copy, rhs, output, index, upper2-index);
        else
            System.arraycopy(copy, lhs, output, index, upper2-index);
    }
}