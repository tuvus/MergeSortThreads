package MergeSortThreads;
import java.util.ArrayList;
public class MergeSortThreadsDivide<E extends Comparable<? super E>> {
    private E[] array;
    private E[] copy;
    private ArrayList<SortSection> sections;
    private boolean completed;

    private MergeSortThreadsDivide() {
    }

    public MergeSortThreadsDivide(E[] array) {
        this(array, Runtime.getRuntime().availableProcessors() - 1);
    }

    public MergeSortThreadsDivide(E[] array, int threadCount) {
        this.array = array;
        this.copy = array.clone();
        completed = false;
        setupSections(threadCount);
    }

    private void setupSections(int threadCount) {
        sections = new ArrayList<>(threadCount);
        int size = array.length / threadCount;
        int remainder = array.length % threadCount;

        boolean outputArray = threadCount % 2 == 1;
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
            sections.add(new SortSection(i, lowerIndex, upperIndex, !outputArray, new MergeSortThread()));
            outputArray = !outputArray;
            sections.get(i).mergeSortThread.section = sections.get(i);
        }
        sections.add(new SortSection(threadCount - 1, upperIndex + 1, array.length - 1, !even, new MergeSortThread()));

        sections.get(threadCount - 1).mergeSortThread.section = sections.get(threadCount - 1);
    }

    public void startSort() {
        for (int i = 0; i < sections.size(); i++) {
            sections.get(i).mergeSortThread.start();
        }
    }

    public void runSort() {
        for (int i = 0; i < sections.size(); i++) {
            sections.get(i).mergeSortThread.run();
        }
    }

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

    public boolean isCompleted() {
        return completed;
    }

    private class SortSection {
        public final int sectionIndex;
        public SortSection merged;
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

    /**
     * Gets a new target section index on the True array (array) to merge to.
     * If the target section index is found it sets that section to not sorted.
     * If no section is found it sets the given section to sorted.
     *
     * @param sectionIndex the given section
     * @return -1 if no target section is found, 0-sections.length - 1 if a section is found
     */
    private synchronized int getNewTrueSectionToMergeInto(int sectionIndex) {
        for (int i = 0; i < sections.size(); i++) {
            if (i == sectionIndex)
                continue;
            if (sections.get(i).sorted && sections.get(i).outputArray) {
                sections.get(i).sorted = false;
                return i;
            }
        }
        sections.get(sectionIndex).sorted = true;
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
        for (int i = 0; i < sections.size(); i++) {
            if (i == sectionIndex)
                continue;
            if (sections.get(i).sorted && !sections.get(i).outputArray) {
                sections.get(i).sorted = false;
                return i;
            }
        }
        sections.get(sectionIndex).sorted = true;
        return -1;
    }

    private class MergeSortThread extends Thread {
        SortSection section;

        @Override
        public void run() {
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
                    section = mergeSections(array, copy, section, sections.get(mergeTargetIndex));
                else
                    section = mergeSections(copy, array, section, sections.get(mergeTargetIndex));
                section.outputArray = !section.outputArray;
                if (section.outputArray)
                    mergeTargetIndex = getNewTrueSectionToMergeInto(section.sectionIndex);
                else
                    mergeTargetIndex = getNewFalseSectionToMergeInto(section.sectionIndex);
            }
            if (section.sectionLength == sections.size())
                completed = true;
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

    private SortSection mergeSections(E[] output, E[] input, SortSection section1, SortSection section2) {
        int[] newSectionOrder = new int[section1.sectionLength + section2.sectionLength];
        getSectionOrder(newSectionOrder, section1, section2);
        int lhs = section1.lowerIndex, rhs = section2.lowerIndex;
        int newSectionOrderIndex = 0;
        int index = sections.get(newSectionOrder[newSectionOrderIndex]).lowerIndex;
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
            if (index > sections.get(newSectionOrder[newSectionOrderIndex]).upperIndex) {
                newSectionOrderIndex++;
                index = sections.get(newSectionOrder[newSectionOrderIndex]).lowerIndex;
            }
        }
        if (lhs <= section1.upperIndex) {
            while (true) {
                int length = Math.min(section1.upperIndex - lhs + 1, sections.get(newSectionOrder[newSectionOrderIndex]).upperIndex - index + 1);
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
                if (index > sections.get(newSectionOrder[newSectionOrderIndex]).upperIndex) {
                    newSectionOrderIndex++;
                    index = sections.get(newSectionOrder[newSectionOrderIndex]).lowerIndex;
                }
            }
        } else {
            while (true) {
                int length = Math.min(section2.upperIndex - rhs + 1, sections.get(newSectionOrder[newSectionOrderIndex]).upperIndex - index + 1);
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
                if (index > sections.get(newSectionOrder[newSectionOrderIndex]).upperIndex) {
                    newSectionOrderIndex++;
                    index = sections.get(newSectionOrder[newSectionOrderIndex]).lowerIndex;
                }
            }
        }
        for (int i = 0; i < newSectionOrder.length; i++) {
            if (i == newSectionOrder.length - 1)
                sections.get(newSectionOrder[i]).merged = null;
            else
                sections.get(newSectionOrder[i]).merged = sections.get(newSectionOrder[i + 1]);
        }
        sections.get(newSectionOrder[0]).sectionLength = newSectionOrder.length;
        return sections.get(newSectionOrder[0]);
    }

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