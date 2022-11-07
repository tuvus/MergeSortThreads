package MergeSortThreads;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MergeSortThreadsDivide<E extends Comparable<E>> {
    private E[] array;
    private E[] copy;
    private SortSection<E>[] sections;

    private MergeSortThreadsDivide() {
    }

    public MergeSortThreadsDivide(E[] array) {
        this.array = array;
        this.copy = array.clone();
        int threadCount = Runtime.getRuntime().availableProcessors() - 1;
        sections = new SortSection[threadCount];
        int size = array.length / threadCount;
        int remainder = array.length % threadCount;

        boolean outputArray = threadCount % 2 == 1;
        boolean even = threadCount % 2 == 0;
        int lowerIndex = 0;
        int upperIndex = size - 1;
        if (remainder > 0)
            upperIndex++;
        sections[0] = new SortSection<E>(0, 0, upperIndex, !even, new MergeSortThread());
        sections[0].mergeSortThread.section = sections[0];
        for (int i = 1; i < sections.length - 1; i++) {
            lowerIndex = upperIndex + 1;
            upperIndex = Math.min(array.length - 1, lowerIndex + size - 1);
            if (i < remainder)
                upperIndex++;
            sections[i] = new SortSection<E>(i, lowerIndex, upperIndex, !outputArray, new MergeSortThread());
            outputArray = !outputArray;
            sections[i].mergeSortThread.section = sections[i];
        }
        sections[sections.length - 1] = new SortSection<E>(sections.length - 1, upperIndex + 1, array.length - 1, !even, new MergeSortThread());
        sections[sections.length - 1].mergeSortThread.section = sections[sections.length - 1];

        for (int i = 0; i < sections.length; i++) {
            sections[i].mergeSortThread.start();
        }

        long time = System.currentTimeMillis();
        boolean finished = false;
        while (!finished) {
            finished = true;
            for (SortSection<E> eSortSection : sections) {
                if (eSortSection.mergeSortThread.isAlive()) {
                    finished = false;
                    break;
                }
            }
            if (System.currentTimeMillis() - time > 100000) {
                for (SortSection<E> section : sections) {
                    if (section.mergeSortThread != null)
                        section.mergeSortThread.interrupt();
                }
                break;
            }
        }
        System.out.println("Ending sorting finished: " + finished);
}

private class SortSection<E extends Comparable<E>> {
    public final int sectionIndex;
    public SortSection<E> merged;
    public boolean sorted;
    public boolean outputArray;
    public int sectionLength;
    public int lowerIndex;
    public int upperIndex;
    public MergeSortThread mergeSortThread;

    public SortSection(int sectionIndex, int lowerIndex, int upperIndex, boolean outputArray, MergeSortThread mergeSortThread) {
        this.sectionIndex = sectionIndex;
        this.lowerIndex = lowerIndex;
        this.upperIndex = upperIndex;
        this.outputArray = outputArray;
        this.mergeSortThread = mergeSortThread;
        this.sorted = false;
        this.merged = null;
        this.sectionLength = 1;
    }
}

    private synchronized void printSafe(String text) {

        //System.out.println(text);
    }

    /**
     * Gets a new target section index on the True array (array) to merge to.
     * If the target section index is found it sets that section to not sorted.
     * If no section is found it sets the given section to sorted.
     *
     * @param sectionIndex the given section
     * @return -1 if no target section is found, 0-sections.length - 1 if a section is found
     */
    private synchronized int getNewTrueSectionToMergeInto(int sectionIndex) {
        for (int i = 0; i < sections.length; i++) {
            if (i == sectionIndex)
                continue;
            if (sections[i].sorted && sections[i].outputArray) {
                sections[i].sorted = false;
                return i;
            }
        }
        sections[sectionIndex].sorted = true;
        return -1;
    }

    /**
     * Gets a new target section index on the False array (copy) to merge to.
     * If the target section index is found it sets that section to not sorted.
     * If no section is found it sets the given section to sorted.
     *
     * @param sectionIndex the given section
     * @return -1 if no target section is found, 0-sections.length - 1 if a section is found
     */
    private synchronized int getNewFalseSectionToMergeInto(int sectionIndex) {
        for (int i = 0; i < sections.length; i++) {
            if (i == sectionIndex)
                continue;
            if (sections[i].sorted && !sections[i].outputArray) {
                sections[i].sorted = false;
                return i;
            }
        }
        sections[sectionIndex].sorted = true;
        return -1;
    }

private class MergeSortThread extends Thread {
    SortSection<E> section;

    @Override
    public void run() {
        int originalThread = section.sectionIndex;
        if (section.outputArray)
            mergeSortRec(array, copy, section.lowerIndex, section.upperIndex);
        else
            mergeSortRec(copy, array, section.lowerIndex, section.upperIndex);
        section.outputArray = !section.outputArray;
        int mergeTargetIndex = -1;
        if (section.outputArray)
            mergeTargetIndex = getNewTrueSectionToMergeInto(section.sectionIndex);
        else
            mergeTargetIndex = getNewFalseSectionToMergeInto(section.sectionIndex);
        while (mergeTargetIndex != -1) {
            if (section.outputArray)
                section = mergeSections(array, copy, section, sections[mergeTargetIndex]);
            else
                section = mergeSections(copy, array, section, sections[mergeTargetIndex]);
            section.outputArray = !section.outputArray;
            if (section.outputArray)
                mergeTargetIndex = getNewTrueSectionToMergeInto(section.sectionIndex);
            else
                mergeTargetIndex = getNewFalseSectionToMergeInto(section.sectionIndex);
        }
        printSafe("Ending thead " + originalThread);
    }

}

    /**
     * @param output the array that should be sorted at between the indices given
     * @param input  the array with the given values
     * @param lower  the lower index to be sorted
     * @param upper  the upper index to be sorted
     */
    private void mergeSortRec(E[] output, E[] input, int lower, int upper) {
        if (upper - lower <= 0)
            return;
        mergeSortRec(input, output, lower, lower + ((upper - lower) / 2));
        mergeSortRec(input, output, lower + ((upper - lower) / 2) + 1, upper);
        merge(output, input, lower, lower + ((upper - lower) / 2), lower + ((upper - lower) / 2) + 1, upper);
    }

    private SortSection<E> mergeSections(E[] output, E[] input, SortSection<E> section1, SortSection<E> section2) {
        int section1Index = section1.sectionIndex;
        int section2Index = section2.sectionIndex;
        printSafe("Merging section " + section1.sectionIndex + " l=" + section1.sectionLength + " with section " + section2.sectionIndex + " l=" + section2.sectionLength);
        SortSection<E>[] newSectionOrder = new SortSection[section1.sectionLength + section2.sectionLength];
        getSectionOrder(newSectionOrder, section1, section2);
        int lhs = section1.lowerIndex, rhs = section2.lowerIndex;
        int newSectionOrderIndex = 0;
        int index = newSectionOrder[newSectionOrderIndex].lowerIndex;
        while (lhs <= section1.upperIndex && rhs <= section2.upperIndex) {
            if ((input[lhs]).compareTo(input[rhs]) <= 0) {
                output[index] = input[lhs];
                lhs++;
                if (lhs > section1.upperIndex && section1.merged != null) {
                    section1 = section1.merged;
                    lhs = section1.lowerIndex;
                }
            } else {
                output[index] = input[rhs];
                rhs++;
                if (rhs > section2.upperIndex && section2.merged != null) {
                    section2 = section2.merged;
                    rhs = section2.lowerIndex;
                }
            }
            index++;
            if (index > newSectionOrder[newSectionOrderIndex].upperIndex) {
                newSectionOrderIndex++;
                index = newSectionOrder[newSectionOrderIndex].lowerIndex;
            }
        }
        if (lhs <= section1.upperIndex) {
            while (true) {
                int length = Math.min(section1.upperIndex - lhs + 1, newSectionOrder[newSectionOrderIndex].upperIndex - index + 1);
                System.arraycopy(input, lhs, output, index, length);
                index += length;
                lhs += length;
                if (lhs > section1.upperIndex) {
                    if (section1.merged != null) {
                        section1 = section1.merged;
                        lhs = section1.lowerIndex;
                    } else {
                        break;
                    }
                }
                if (index > newSectionOrder[newSectionOrderIndex].upperIndex) {
                    newSectionOrderIndex++;
                    index = newSectionOrder[newSectionOrderIndex].lowerIndex;
                }
            }
        } else {
            while (true) {
                int length = Math.min(section2.upperIndex - rhs + 1, newSectionOrder[newSectionOrderIndex].upperIndex - index + 1);
                System.arraycopy(input, rhs, output, index, length);
                index += length;
                rhs += length;
                if (rhs > section2.upperIndex) {
                    if (section2.merged != null) {
                        section2 = section2.merged;
                        rhs = section2.lowerIndex;
                    } else {
                        break;
                    }
                }
                if (index > newSectionOrder[newSectionOrderIndex].upperIndex) {
                    newSectionOrderIndex++;
                    index = newSectionOrder[newSectionOrderIndex].lowerIndex;
                }
            }
        }
        for (int i = 0; i < newSectionOrder.length; i++) {
            if (i == newSectionOrder.length - 1)
                newSectionOrder[i].merged = null;
            else
                newSectionOrder[i].merged = newSectionOrder[i + 1];
        }
        newSectionOrder[0].sectionLength = newSectionOrder.length;
        printSafe("Finished merging section " + section1Index + " with " + section2Index);
        return newSectionOrder[0];
    }

    private void getSectionOrder(SortSection<E>[] newSectionOrder, SortSection<E> section1, SortSection<E> section2) {
        for (int index = 0; index < newSectionOrder.length; index++) {
            if (section2 == null || (section1 != null && section1.lowerIndex < section2.lowerIndex)) {
                newSectionOrder[index] = section1;
                section1 = section1.merged;
            } else {
                newSectionOrder[index] = section2;
                section2 = section2.merged;
            }
        }
    }

    private void merge(E[] output, E[] input, int lower, int upper, int lower2, int upper2) {
        int lhs = lower, rhs = lower2;
        int index = lower;
        while (lhs <= upper && rhs <= upper2) {
            if ((input[lhs]).compareTo(input[rhs]) <= 0) {
                output[index] = input[lhs];
                lhs++;
            } else {
                output[index] = input[rhs];
                rhs++;
            }
            index++;
        }
        if (lhs > upper) {
            System.arraycopy(input, rhs, output, index, upper2 - index + 1);
        } else {
            System.arraycopy(input, lhs, output, index, upper2 - index + 1);
        }
    }
}