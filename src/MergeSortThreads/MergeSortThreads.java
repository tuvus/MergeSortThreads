package MergeSortThreads;

import org.junit.rules.Stopwatch;

import java.util.Timer;

public class MergeSortThreads {
    private MergeSortThreads() {
    }

    private static class MergeSortThread extends Thread {
        ArrayPartition arrayPartition;

        protected MergeSortThread(ArrayPartition arrayPartition) {
            this.arrayPartition = arrayPartition;
        }

        @Override
        public void run() {
            mergeSortRec(arrayPartition.output, arrayPartition.copy, arrayPartition.lowerIndex, arrayPartition.upperIndex);
            while (arrayPartition.setSorted()) {
                if (arrayPartition.parentPartition == null)
                    break;
                arrayPartition = arrayPartition.parentPartition;
                merge(arrayPartition.output, arrayPartition.copy,
                        arrayPartition.leftPartition.lowerIndex, arrayPartition.leftPartition.upperIndex,
                        arrayPartition.rightPartition.lowerIndex, arrayPartition.rightPartition.upperIndex);
            }
        }
    }

    private static class ArrayPartition {
        public final ArrayPartition parentPartition;
        private boolean sorted;
        public final Comparable[] output;
        public final Comparable[] copy;
        public final int lowerIndex;
        public final int upperIndex;
        public ArrayPartition leftPartition;
        public ArrayPartition rightPartition;

        public ArrayPartition(ArrayPartition parentPartition, Comparable[] output, Comparable[] copy, int lowerIndex, int upperIndex, int levels, MergeSortThread[] threads) {
            this.parentPartition = parentPartition;
            this.output = output;
            this.copy = copy;
            this.lowerIndex = lowerIndex;
            this.upperIndex = upperIndex;
            if (levels <= 0) {
                int firstInactiveThread = -1;
                for (int i = 0; i < threads.length; i++) {
                    if (threads[i] == null) {
                        firstInactiveThread = i;
                        break;
                    }
                }
                if (firstInactiveThread != -1) {
                    threads[firstInactiveThread] = new MergeSortThread(this);
                    threads[firstInactiveThread].start();
                    return;
                } else {
                    throw new IllegalArgumentException("There was not enough room in the threads array for the thread count that was generated." +
                            " The array needs to be able to store all threads that are generated in order to work correctly.");
                }
            }
            leftPartition = new ArrayPartition(this, output, copy, lowerIndex, lowerIndex + ((upperIndex - lowerIndex) / 2), levels - 1, threads);
            rightPartition = new ArrayPartition(this, output, copy, lowerIndex + ((upperIndex - lowerIndex) / 2) + 1, upperIndex, levels - 1, threads);
        }

        public synchronized boolean setSorted() {
            if (!sorted) {
                sorted = true;
                return false;
            }
            return true;
        }
    }

    public static void sortArray(Comparable[] array) {
        int threadCount = Runtime.getRuntime().availableProcessors();
        final MergeSortThread[] threads = new MergeSortThread[(int)Math.pow(Math.ceil(Math.sqrt(threadCount)),2)];
        ArrayPartition arrayPartition = new ArrayPartition(null, array, array.clone(), 0, array.length - 1, (int) Math.ceil(Math.sqrt(threadCount)), threads);
        long time = System.currentTimeMillis();
        while (!arrayPartition.sorted) {
            if (System.currentTimeMillis() - time > 30000) {
                System.out.println("Ending code execution because of time.");
                for (int i = 0; i < threads.length; i++) {
                    threads[i].interrupt();
                }
                return;
            }
        }
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