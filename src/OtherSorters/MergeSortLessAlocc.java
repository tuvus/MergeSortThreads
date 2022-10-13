package OtherSorters;

public class MergeSortLessAlocc {
    public static void sortArray(Comparable[] array) {
        mergeSortRec(array, array.clone(), 0, array.length - 1);
    }

    /**
     * @param output the array to return to
     * @param copy   the array with copied values
     * @param lower  the lower index to be sorted
     * @param upper  the upper index to be sorted
     */
    private static void mergeSortRec(Comparable[] output, Comparable[] copy, int lower, int upper) {
        if (upper - lower <= 0)
            return;
        mergeSortRec(copy, output, lower, lower + ((upper - lower) / 2));
        mergeSortRec(copy, output, lower + ((upper - lower) / 2) + 1, upper);
        merge(output, copy, lower, lower + ((upper - lower) / 2), lower + ((upper - lower) / 2) + 1, upper);
    }

    private static void merge(Comparable[] output, Comparable[] copy, int lower, int upper, int lower2, int upper2) {
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
