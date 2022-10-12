package OtherSorters;

public class MergeSort {
    public static void sortArray(Comparable[] array) {
        mergeSortRec(array);
    }

    /**
     * @param output the array to return to
     */

    private static void mergeSortRec(Comparable[] output) {
        if (output.length == 1)
            return;
        Comparable[] array1 = new Comparable[output.length / 2];
        Comparable[] array2 = new Comparable[output.length - array1.length];
        System.arraycopy(output, 0, array1, 0, array1.length);
        System.arraycopy(output, array1.length, array2, 0, array2.length);
        mergeSortRec(array1);
        mergeSortRec(array2);
        merge(array1, array2, output);
    }

    private static void merge(Comparable[] array1, Comparable[] array2, Comparable[] out) {
        int lhs = 0, rhs = 0;
        while (lhs < array1.length && rhs < array2.length) {
            if (array1[lhs].compareTo(array2[rhs]) <= 0) {
                out[lhs + rhs] = array1[lhs];
                lhs++;
            } else {
                out[lhs + rhs] = array2[rhs];
                rhs++;
            }
        }
        if (lhs >= array1.length)
            System.arraycopy(array2, rhs, out, lhs + rhs, array2.length - rhs);
        else
            System.arraycopy(array1, lhs, out, lhs + rhs, array1.length - lhs);
    }
}