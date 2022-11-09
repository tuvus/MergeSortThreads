package MergeSortThreads;

import java.util.ArrayList;

/**
 * MergeSortThreads is an instantiated class that sorts an array given in its constructor during its life.
 * MergeSortThreads uses the classical MergeSort algorithm using multithreading.
 * MergeSortThreads divides the array into x parts, where x is the smallest even root of the available thread count.
 * It also only uses two arrays instead of recursively creating more arrays.
 * While sorting the main thread will be available to do other work with.
 * The sorting is completed when isCompleted returns true.
 * To ensure that the array is sorted call .complete() on MergeSortThreads.
 *
 * @param <E> the type to be sorted
 * @author Oskar
 */
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

    /**
     * MergeSortThread handles calling mergeSortRec and merging different partitions of the array together.
     * The thread will end once it is sorted and can't merge into any other partitions
     */
    private class MergeSortThread extends Thread {
        ArrayPartition arrayPartition;

        protected MergeSortThread(ArrayPartition arrayPartition) {
            this.arrayPartition = arrayPartition;
        }

        @Override
        public void run() {
            //Sort this individual partition
            if (arrayPartition.outputArray)
                mergeSortRec(array, copy, arrayPartition.lowerIndex, arrayPartition.upperIndex);
            else
                mergeSortRec(copy, array, arrayPartition.lowerIndex, arrayPartition.upperIndex);
            arrayPartition.outputArray = !arrayPartition.outputArray;

            //Check if the child other child ArrayPartition has already finished
            while (arrayPartition.parentPartition != null && arrayPartition.parentPartition.setSorted()) {
                arrayPartition = arrayPartition.parentPartition;
                //Merge with the other child ArrayPartition
                if (arrayPartition.outputArray)
                    merge(array, copy,
                        arrayPartition.leftPartition.lowerIndex, arrayPartition.leftPartition.upperIndex,
                        arrayPartition.rightPartition.lowerIndex, arrayPartition.rightPartition.upperIndex);
                else
                    merge(copy, array,
                            arrayPartition.leftPartition.lowerIndex, arrayPartition.leftPartition.upperIndex,
                            arrayPartition.rightPartition.lowerIndex, arrayPartition.rightPartition.upperIndex);
            }

            //The entire array has been sorted
            if (arrayPartition.parentPartition == null)
                completed = true;
        }
    }

    /**
     * ArrayPartition holds the indelicacies for the upper and lower bounds of its section.
     * ArrayPartitions are created in a pyramid manner.
     * Each bottom level ArrayPartition holds a thread object.
     * Once both left/right child partition are sorted the second thread will sort the parent partition.
     * If there is no parent partition then the array has been sorted.
     */
    private class ArrayPartition {
        public final ArrayPartition parentPartition;
        private boolean halfSorted;
        public boolean outputArray;
        public final int lowerIndex;
        public final int upperIndex;
        public ArrayPartition leftPartition;
        public ArrayPartition rightPartition;

        public ArrayPartition(ArrayPartition parentPartition, boolean outputArray, int lowerIndex, int upperIndex, int levels) {
            this.parentPartition = parentPartition;
            this.halfSorted = false;
            this.outputArray = outputArray;
            this.lowerIndex = lowerIndex;
            this.upperIndex = upperIndex;
            if (levels <= 0 || upperIndex - lowerIndex <= 10) {
                //Bottom level ArrayPartition, creates a thread
                halfSorted = true;
                threads.add(new MergeSortThread(this));
            } else {
                //Parent ArrayPartition, creates two children
                leftPartition = new ArrayPartition(this, !outputArray, lowerIndex, lowerIndex + ((upperIndex - lowerIndex) / 2), levels - 1);
                rightPartition = new ArrayPartition(this, !outputArray, lowerIndex + ((upperIndex - lowerIndex) / 2) + 1, upperIndex, levels - 1);
            }
        }

        /**
         * Makes sure that both sections won't check/update if the parent is half-sorted at the same time
         * which would leave the parent unmerged
         * @return returns true if the other child array is already sorted, false if it has not
         */
        private synchronized boolean setSorted() {
            if (!halfSorted) {
                halfSorted = true;
                return false;
            }
            return true;
        }
    }

    /**
     * Initializes the ArrayPartition hierarchy in a pyramid shape.
     * Creates threads equal to the smallest root bigger than threadCount.
     * @param threadCount the minimum number of threads to create
     */
    private void setupSort(int threadCount) {
        threads = new ArrayList<>((int) Math.pow(Math.ceil(Math.sqrt(threadCount)), 2));
        arrayPartition = new ArrayPartition(null, true, 0, array.length - 1, (int) Math.ceil(Math.sqrt(threadCount)));
    }

    /**
     * Begins the sorting on all the threads.
     * Calling start after previously calling start or run will throw an IllegalStateException.
     * @throws IllegalStateException if the Sorting has already started or completed
     */
    public void start() {
        if (completed)
            throw new IllegalStateException("Trying to start MergeSortThreads after the array was already finished." +
                    " You probably called start after previously calling run or start.");
        for (int i = 0; i < threads.size(); i++) {
            if (threads.get(i).getState() != Thread.State.NEW)
                throw new IllegalStateException("Trying to run MergeSortThreads after calling start. " +
                        "The threads where still sorting when called.");
            threads.get(i).start();
        }
    }

    /**
     * Runs the sorting on the single main thread.
     * Calling run after previously calling start or run will throw an IllegalStateException.
     * @throws IllegalStateException if the Sorting has already started or completed
     */
    public void run() {
        if (completed)
            throw new IllegalStateException("Trying to run MergeSortThreads after the array was already finished." +
                    " You probably called run after previously calling run or start.");
        for (int i = 0; i < threads.size(); i++) {
            if (threads.get(i).getState() != Thread.State.NEW)
                throw new IllegalStateException("Trying to run MergeSortThreads after calling start. " +
                        "The threads where still sorting when called.");
            threads.get(i).run();
        }
    }

    /**
     * Finishes sorting the array.
     * Once the method returns the array is guaranteed to be sorted.
     */
    public void complete() {
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

    /**
     * Returns weather or not the array is sorted.
     * @return true if the array is sorted, false if it is still sorting
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Recursively sorts the individual section alternating what arrays are the output and copy.
     * @param output the array that should be sorted at between the indices given
     * @param copy  the array with the given values
     * @param lower  the lower index to be sorted
     * @param upper  the upper index to be sorted
     */
    private void mergeSortRec(E[] output, E[] copy, int lower, int upper) {
        if (upper - lower <= 0)
            return;
        mergeSortRec(copy, output, lower, lower + ((upper - lower) / 2));
        mergeSortRec(copy, output, lower + ((upper - lower) / 2) + 1, upper);
        merge(output, copy, lower, lower + ((upper - lower) / 2), lower + ((upper - lower) / 2) + 1, upper);
    }

    /**
     * Merges the two parts of the array together in sorted order
     * @param output the array that should end up sorted
     * @param copy the half sorted array with the values to sort
     * @param lower the lower index to be sorted of the first part
     * @param upper the upper index to be sorted of the first part
     * @param lower2 the lower index to be sorted of the second part
     * @param upper2 the upper index to be sorted of the second part
     */
    private void merge(E[] output, E[] copy, int lower, int upper, int lower2, int upper2) {
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