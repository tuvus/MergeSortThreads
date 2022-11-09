package MergeSortThreads;

import java.util.ArrayList;

public class MergeSortThreads<E extends Comparable<? super E>> {
    private E[] array;
    private E[] copy;
    private ArrayPartition arrayPartition;
    private ArrayList<MergeSortThread> threads;
    private boolean completed;

    private MergeSortThreads() {
    }

    public MergeSortThreads(E[] array) {
        this.array = array;
        this.copy = array.clone();
        setupSort(Runtime.getRuntime().availableProcessors());
    }


    private class MergeSortThread extends Thread {
        ArrayPartition arrayPartition;

        protected MergeSortThread(ArrayPartition arrayPartition) {
            this.arrayPartition = arrayPartition;
        }

        @Override
        public void run() {
            mergeSortRec(arrayPartition.output, arrayPartition.copy, arrayPartition.lowerIndex, arrayPartition.upperIndex);
            while (arrayPartition.parentPartition != null && arrayPartition.parentPartition.setSorted()) {
                arrayPartition = arrayPartition.parentPartition;
                merge(arrayPartition.output, arrayPartition.copy,
                        arrayPartition.leftPartition.lowerIndex, arrayPartition.leftPartition.upperIndex,
                        arrayPartition.rightPartition.lowerIndex, arrayPartition.rightPartition.upperIndex);
            }
            if (arrayPartition.parentPartition == null)
                completed = true;
        }
    }

    private class ArrayPartition {
        public final ArrayPartition parentPartition;
        private boolean halfSorted;
        public final Comparable[] output;
        public final Comparable[] copy;
        public final int lowerIndex;
        public final int upperIndex;
        public ArrayPartition leftPartition;
        public ArrayPartition rightPartition;

        public ArrayPartition(ArrayPartition parentPartition, Comparable[] output, Comparable[] copy, int lowerIndex, int upperIndex, int levels) {
            this.parentPartition = parentPartition;
            this.halfSorted = false;
            this.output = output;
            this.copy = copy;
            this.lowerIndex = lowerIndex;
            this.upperIndex = upperIndex;
            if (levels <= 0 || upperIndex - lowerIndex <= 10) {
                halfSorted = true;
                threads.add(new MergeSortThread(this));
            } else {
                leftPartition = new ArrayPartition(this, copy, output, lowerIndex, lowerIndex + ((upperIndex - lowerIndex) / 2), levels - 1);
                rightPartition = new ArrayPartition(this, copy, output, lowerIndex + ((upperIndex - lowerIndex) / 2) + 1, upperIndex, levels - 1);
            }
        }

        private synchronized boolean setSorted() {
            if (!halfSorted) {
                halfSorted = true;
                return false;
            }
            return true;
        }
    }

    private void setupSort(int threadCount) {
        threads = new ArrayList<>((int) Math.pow(Math.ceil(Math.sqrt(threadCount)), 2));
        arrayPartition = new ArrayPartition(null, array, array.clone(), 0, array.length - 1, (int) Math.ceil(Math.sqrt(threadCount)));
    }

    public void start() {
        for (int i = 0; i < threads.size(); i++) {
            threads.get(i).start();
        }
    }

    public void run() {
        for (int i = 0; i < threads.size(); i++) {
            threads.get(i).run();
        }
    }

    public void complete () {
        while (!completed) {
            for (int i = 0; i < threads.size(); i++) {
                try {
                    threads.get(i).join();
                } catch (InterruptedException e) {
                    System.out.println("A thread was interrupted while completing the sort. Thread index " + i +
                            "Trying to continue on as normal.");
                }
            }
        }
    }

    public boolean isCompleted() {
        return completed;
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