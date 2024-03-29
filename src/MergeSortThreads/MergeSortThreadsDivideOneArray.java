package MergeSortThreads;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This version of MergeSortThreadsDivide uses only one array that is twice as long.
 * As long as the array we need to sort is half int.MaxValue, it should be able to work.
 *
 * @param <E>
 */
public class MergeSortThreadsDivideOneArray<E extends Comparable<? super E>> {
    private E[] array;
    private E[] sortingArray;
    private ArrayList<SortSection> sections;
    private boolean completed;

    /**
     * Method private to prevent initializing without an array
     */
    private MergeSortThreadsDivideOneArray() {
    }

    /**
     * Initialises MergeSortThreadsDivide with an array.
     * The number of threads that will be used is equal to Runtime.getRuntime().availableProcessors() - 1.
     *
     * @param array the array to sort
     */
    public MergeSortThreadsDivideOneArray(E[] array) {
        this(array, Runtime.getRuntime().availableProcessors() - 1);
    }

    /**
     * Initialises MergeSortThreadsDivide with an array and a desired threadCount.
     *
     * @param array       the array to sort
     * @param threadCount the number of threads that should be used while sorting
     */
    public MergeSortThreadsDivideOneArray(E[] array, int threadCount) {
        this.array = array;
        this.sortingArray = Arrays.copyOf(array, array.length * 2);
        System.arraycopy(array, 0, sortingArray, array.length, array.length);
        completed = false;
        setupSections(threadCount);
    }

    /**
     * Divides the array into sections.
     * Each section will have a thread object attached to it.
     *
     * @param threadCount the number of sections the array will be divided into
     */
    private void setupSections(int threadCount) {
        sections = new ArrayList<>(threadCount);
        int size = array.length / threadCount;
        int remainder = array.length % threadCount;

        boolean leftOrRightArray = threadCount % 2 == 1;
        boolean even = threadCount % 2 == 0;
        int lowerIndex = 0;
        int upperIndex = size - 1;
        if (remainder > 0)
            upperIndex++;
        sections.add(new SortSection(0, 0, upperIndex, !even, new MergeSortThread()));
        sections.get(0).mergeSortThread.section = sections.get(0);
        for (int i = 1; i < threadCount - 1; i++) {
            lowerIndex = upperIndex + 1;
            upperIndex = Math.min(array.length - 1, lowerIndex + size - 1);
            if (i < remainder)
                upperIndex++;
            sections.add(new SortSection(i, lowerIndex, upperIndex, !leftOrRightArray, new MergeSortThread()));
            leftOrRightArray = !leftOrRightArray;
            sections.get(i).mergeSortThread.section = sections.get(i);
        }
        sections.add(new SortSection(threadCount - 1, upperIndex + 1, array.length - 1, !even, new MergeSortThread()));

        sections.get(threadCount - 1).mergeSortThread.section = sections.get(threadCount - 1);
    }

    /**
     * Begins the sorting on all the threads.
     * Calling start after previously calling start or run will throw an IllegalStateException.
     *
     * @throws IllegalStateException if the Sorting has already started or completed
     */
    public void start() {
        if (completed)
            throw new IllegalStateException("Trying to start MergeSortThreadsDivide after the array was already finished." +
                    " You probably called start after previously calling run or start.");
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).mergeSortThread.getState() != Thread.State.NEW)
                throw new IllegalStateException("Trying to run MergeSortThreadsDivide after calling start. " +
                        "The threads where still sorting when called.");
            sections.get(i).mergeSortThread.start();
        }
    }

    /**
     * Runs the sorting on the single main thread.
     * Calling run after previously calling start or run will throw an IllegalStateException.
     *
     * @throws IllegalStateException if the Sorting has already started or completed
     */
    public void run() {
        if (completed)
            throw new IllegalStateException("Trying to run MergeSortThreadsDivide after the array was already finished." +
                    " You probably called run after previously calling run or start.");
        for (int i = 0; i < sections.size(); i++) {
            if (sections.get(i).mergeSortThread.getState() != Thread.State.NEW)
                throw new IllegalStateException("Trying to run MergeSortThreadsDivide after calling start. " +
                        "The threads where still sorting when called.");
            sections.get(i).mergeSortThread.run();
        }
    }

    /**
     * Finishes sorting the array.
     * Once the method returns the array is guaranteed to be sorted.
     */
    public void complete() {
        while (!completed) {
            for (int i = 0; i < sections.size(); i++) {
                try {
                    sections.get(i).mergeSortThread.join();
                } catch (InterruptedException e) {
                    System.out.println("A thread was interrupted while completing the sort. Thread index " + i +
                            "Trying to continue on as normal.");
                }
            }
        }
    }

    /**
     * Returns weather or not the array is sorted.
     *
     * @return true if the array is sorted, false if it is still sorting
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * A container holding the range of the section, the thread to initially sort the section
     * weather it is sorted or not, and which sections are in its chain.
     * Each section has two ranges associated with them, one from lowerIndex to upperIndex.
     * The other is from lowerIndex + array.length to upperIndex + array.length.
     * Sections can be chained together to create parts of the array that are sorted and broken up in location.
     */
    private class SortSection {
        public final int sectionIndex;
        //Points to the next section in the part of the sorted section chain
        public SortSection merged;
        public boolean sorted;
        //True if the sorted array is on the left side of the array
        //False if the sorted array is on the right side of the array
        public boolean leftOrRightArray;
        //How many sections are in the chain
        public int sectionLength;
        public int lowerIndex;
        public int upperIndex;
        public MergeSortThread mergeSortThread;

        public SortSection(int sectionIndex, int lowerIndex, int upperIndex, boolean leftOrRightArray, MergeSortThread mergeSortThread) {
            this.sectionIndex = sectionIndex;
            this.lowerIndex = lowerIndex;
            this.upperIndex = upperIndex;
            this.leftOrRightArray = leftOrRightArray;
            this.mergeSortThread = mergeSortThread;
            this.sorted = false;
            this.merged = null;
            this.sectionLength = 1;
        }
    }

    /**
     * Gets a new target section index on the True array (array) to merge to.
     * If the target section index is found it sets that section to not sorted.
     * If no section is found it sets the given section as sorted.
     * <p>
     * Two methods where required so that there is a half chance two threads will
     * be blocked by the synchronized section.
     * Synchronized blocks are required because two threads could be searching for a section to merge
     * and pass each other.
     *
     * @param sectionIndex the given section
     * @return -1 if no target section is found, 0 through sections.size() - 1 if a section is found
     */
    private synchronized int getNewTrueSectionToMergeInto(int sectionIndex) {
        for (int i = 0; i < sections.size(); i++) {
            if (i == sectionIndex)
                continue;
            if (sections.get(i).sorted && sections.get(i).leftOrRightArray) {
                sections.get(i).sorted = false;
                return i;
            }
        }
        sections.get(sectionIndex).sorted = true;
        return -1;
    }

    /**
     * Gets a new target section index on the False array (sortingArray) to merge to.
     * If the target section index is found it sets that section to not sorted.
     * If no section is found it sets the given section to sorted.
     * <p>
     * Two methods where required so that there is a half chance two threads will
     * be blocked by the synchronized section.
     * Synchronized blocks are required because two threads could be searching for a section to merge
     * and pass each other.
     *
     * @param sectionIndex the given section
     * @return -1 if no target section is found, 0 through sections.size() - 1 if a section is found
     */
    private synchronized int getNewFalseSectionToMergeInto(int sectionIndex) {
        for (int i = 0; i < sections.size(); i++) {
            if (i == sectionIndex)
                continue;
            if (sections.get(i).sorted && !sections.get(i).leftOrRightArray) {
                sections.get(i).sorted = false;
                return i;
            }
        }
        sections.get(sectionIndex).sorted = true;
        return -1;
    }

    /**
     * A thread that manages sorting and merging the section with each other.
     */
    private class MergeSortThread extends Thread {
        SortSection section;

        /**
         * Sorts its own individual section then looks for sections to merge with.
         * If it does not find any sections it will return.
         * If it is the last thread running it will set the MergeSortThreadsDivide as sorted
         * section.leftOrRightArray needs to be inverted each time the section is sorted again.
         */
        @Override
        public void run() {
            //Merge the individual section
            mergeSortRec(section.leftOrRightArray, section.lowerIndex, section.upperIndex);
            section.leftOrRightArray = !section.leftOrRightArray;

            //Merges with eligible sections until sorting is complete
            //or there aren't any more eligible sections to merge with
            int mergeTargetIndex = -1;
            if (section.leftOrRightArray)
                mergeTargetIndex = getNewTrueSectionToMergeInto(section.sectionIndex);
            else
                mergeTargetIndex = getNewFalseSectionToMergeInto(section.sectionIndex);
            while (mergeTargetIndex != -1) {
                //Merge the sections
                section = mergeSections(section.leftOrRightArray, section, sections.get(mergeTargetIndex));
                section.leftOrRightArray = !section.leftOrRightArray;

                //Check for if a new section is eligible to merge with
                if (section.leftOrRightArray)
                    mergeTargetIndex = getNewTrueSectionToMergeInto(section.sectionIndex);
                else
                    mergeTargetIndex = getNewFalseSectionToMergeInto(section.sectionIndex);
            }
            //The sorting is complete
            if (section.sectionLength == sections.size()) {
                System.arraycopy(sortingArray, 0, array, 0, array.length);
                completed = true;
            }
        }

        /**
         * MergeSorts the two SortSections chains together into a single SortSection chain.
         * The SortSection in the chain are order by their index.
         * Returns the SortSection at the begging of the chain.
         *
         * @param leftOrRight which array the sorted array should be on
         * @param section1    the first section chain
         * @param section2    the second section chain
         * @return a new sorted SortSection chain
         */
        private SortSection mergeSections(boolean leftOrRight, SortSection section1, SortSection section2) {
            //Set up the new newSectionOrder, indices pointing towards the output SortSection chain
            int[] newSectionOrder = new int[section1.sectionLength + section2.sectionLength];
            getSectionOrder(newSectionOrder, section1, section2);
            int newSectionOrderIndex = 0;

            //Add section.lowerIndex and section.upperIndex by offset to get the real input section range
            int offset = 0;
            //Add section.lowerIndex and section.upperIndex by offset to get the real output section range
            int reverseOffset = array.length;
            if (leftOrRight) {
                offset = array.length;
                reverseOffset = 0;
            }
            //The indices of the two objects to compare
            int lhs = section1.lowerIndex + offset, rhs = section2.lowerIndex + offset;

            //The index at the output array to sortingArray the values to
            int index = sections.get(newSectionOrder[newSectionOrderIndex]).lowerIndex + reverseOffset;

            //Compares the two sections and copies the lower one to the output array
            //until one of the SortSection chains has no more elements
            while (lhs <= section1.upperIndex + offset && rhs <= section2.upperIndex + offset) {
                if ((sortingArray[lhs]).compareTo(sortingArray[rhs]) <= 0) {
                    //Add the left object to the output array
                    sortingArray[index] = sortingArray[lhs];
                    lhs++;
                    if (lhs > section1.upperIndex + offset && section1.merged != null) {
                        //Move to the next section of the section chain
                        section1 = section1.merged;
                        lhs = section1.lowerIndex + offset;
                    }
                } else {
                    //Add the right object to the output array
                    sortingArray[index] = sortingArray[rhs];
                    rhs++;
                    if (rhs > section2.upperIndex + offset && section2.merged != null) {
                        //Move to the next section of the section chain
                        section2 = section2.merged;
                        rhs = section2.lowerIndex + offset;
                    }
                }

                //Update the output array index
                index++;
                if (index > sections.get(newSectionOrder[newSectionOrderIndex]).upperIndex + reverseOffset) {
                    //Move to the next target section in the output section chain
                    newSectionOrderIndex++;
                    index = sections.get(newSectionOrder[newSectionOrderIndex]).lowerIndex + reverseOffset;
                }
            }
            //sortingArray the rest of the values to the output array
            if (lhs <= section1.upperIndex + offset) {
                while (true) {
                    //Copies the values from the input section to the output section
                    //Increments through the input and output sections when they run out of values
                    int length = Math.min(section1.upperIndex - lhs + 1 + offset, sections.get(newSectionOrder[newSectionOrderIndex]).upperIndex - index + 1 + reverseOffset);
                    System.arraycopy(sortingArray, lhs, sortingArray, index, length);
                    index += length;
                    lhs += length;
                    if (lhs > section1.upperIndex + offset) {
                        //Move to the next target section in the input section chain
                        if (section1.merged != null) {
                            section1 = section1.merged;
                            lhs = section1.lowerIndex + offset;
                        } else {
                            break;
                        }
                    }
                    if (index > sections.get(newSectionOrder[newSectionOrderIndex]).upperIndex + reverseOffset) {
                        //Move to the next target section in the output section chain
                        newSectionOrderIndex++;
                        index = sections.get(newSectionOrder[newSectionOrderIndex]).lowerIndex + reverseOffset;
                    }
                }
            } else {
                while (true) {
                    //Copies the values from the input section to the output section
                    //Increments through the input and output sections when they run out of values
                    int length = Math.min(section2.upperIndex - rhs + 1 + offset, sections.get(newSectionOrder[newSectionOrderIndex]).upperIndex - index + 1 + reverseOffset);
                    System.arraycopy(sortingArray, rhs, sortingArray, index, length);
                    index += length;
                    rhs += length;
                    if (rhs > section2.upperIndex + offset) {
                        //Move to the next target section in the input section chain
                        if (section2.merged != null) {
                            section2 = section2.merged;
                            rhs = section2.lowerIndex + offset;
                        } else {
                            break;
                        }
                    }
                    if (index > sections.get(newSectionOrder[newSectionOrderIndex]).upperIndex + reverseOffset) {
                        //Move to the next target section in the output section chain
                        newSectionOrderIndex++;
                        index = sections.get(newSectionOrder[newSectionOrderIndex]).lowerIndex + reverseOffset;
                    }
                }
            }
            //Sets up the new SortSection chain
            for (int i = 0; i < newSectionOrder.length; i++) {
                if (i == newSectionOrder.length - 1)
                    sections.get(newSectionOrder[i]).merged = null;
                else
                    sections.get(newSectionOrder[i]).merged = sections.get(newSectionOrder[i + 1]);
            }
            sections.get(newSectionOrder[0]).sectionLength = newSectionOrder.length;
            return sections.get(newSectionOrder[0]);
        }
    }

    /**
     * Populates an array of integers that point to sections with indices in sorted order.
     *
     * @param newSectionOrder the output indices of the new section order
     * @param section1        the first section to merge
     * @param section2        the second section to merge
     */
    private void getSectionOrder(int[] newSectionOrder, SortSection section1, SortSection section2) {
        for (int index = 0; index < newSectionOrder.length; index++) {
            if (section2 == null || (section1 != null && section1.lowerIndex < section2.lowerIndex)) {
                newSectionOrder[index] = section1.sectionIndex;
                section1 = section1.merged;
            } else {
                newSectionOrder[index] = section2.sectionIndex;
                section2 = section2.merged;
            }
        }
    }

    /**
     * Recursively sorts the individual section alternating what arrays are the output and sortingArray.
     *
     * @param leftOrRight which array the sorted array should be on
     * @param lower       the lower index to be sorted
     * @param upper       the upper index to be sorted
     */
    private void mergeSortRec(boolean leftOrRight, int lower, int upper) {
        if (upper - lower <= 0)
            return;
        mergeSortRec(!leftOrRight, lower, lower + ((upper - lower) / 2));
        mergeSortRec(!leftOrRight, lower + ((upper - lower) / 2) + 1, upper);
        int offset = 0;
        if (leftOrRight)
            offset = array.length;
        merge(leftOrRight, lower + offset, lower + ((upper - lower) / 2) + offset, lower + ((upper - lower) / 2) + 1 + offset, upper + offset);
    }

    /**
     * Merges the two parts of the array together in sorted order
     *
     * @param leftOrRight which array the sorted array should be on
     *                    the lower and upper sections should be on the other half of the array
     * @param lower       the lower index to be sorted of the first part
     * @param upper       the upper index to be sorted of the first part
     * @param lower2      the lower index to be sorted of the second part
     * @param upper2      the upper index to be sorted of the second part
     */
    private void merge(boolean leftOrRight, int lower, int upper, int lower2, int upper2) {
        int lhs = lower, rhs = lower2;
        int offset = array.length;
        if (leftOrRight)
            offset = -array.length;
        int index = lower + offset;
        while (lhs <= upper && rhs <= upper2) {
            if ((sortingArray[lhs]).compareTo(sortingArray[rhs]) <= 0) {
                sortingArray[index] = sortingArray[lhs];
                lhs++;
            } else {
                sortingArray[index] = sortingArray[rhs];
                rhs++;
            }
            index++;
        }
        if (lhs > upper) {
            System.arraycopy(sortingArray, rhs, sortingArray, index, upper2 - index + 1 + offset);
        } else {
            System.arraycopy(sortingArray, lhs, sortingArray, index, upper2 - index + 1 + offset);
        }
    }
}