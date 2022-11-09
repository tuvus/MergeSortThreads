package SortBenchmarks;

import MergeSortThreads.MergeSortThreads;
import MergeSortThreads.MergeSortThreadsDivide;
import OtherSorters.MergeSort;
import OtherSorters.MergeSortLessAlocc;
import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(value = 2)
@Warmup(iterations = 3)
@Measurement(iterations = 500, timeUnit = TimeUnit.NANOSECONDS, time = 1)
//@Fork(value = 0)
//@Warmup(iterations = 0)
//@Measurement(iterations = 10, timeUnit = TimeUnit.NANOSECONDS, time = 1)
public class SorterBenchmarks {

    @State(Scope.Benchmark)
    public static class SorterState {
        public Integer[] reference;
        public Integer[] copy;

        @Setup(Level.Trial)
        public void setupArray() {
            reference = (Integer[]) getRandomArray();
        }

        @Setup(Level.Iteration)
        public void copyArray() {
            copy = reference.clone();
        }
    }

    @Benchmark
    public void mergeSortThreadsDivideBenchmark(SorterState sorterState) {
        MergeSortThreadsDivide<Integer> mergeSortThreadsDivide = new MergeSortThreadsDivide<>(sorterState.copy);
        mergeSortThreadsDivide.start();
        mergeSortThreadsDivide.complete();
    }

    @Benchmark
    public void mergeSortThreadsBenchmark(SorterState sorterState) {
        MergeSortThreads<Integer> mergeSortThreads = new MergeSortThreads<>(sorterState.copy);
        mergeSortThreads.start();
        mergeSortThreads.complete();
    }

    @Benchmark
    public void mergeSortLessAloccBenchmark(SorterState sorterState) {
        MergeSortLessAlocc.sortArray(sorterState.copy);
    }

    @Benchmark
    public void mergeSortBenchmark(SorterState sorterState) {
        MergeSort.sortArray(sorterState.copy);
    }

    @Benchmark
    public void referenceSortBenchmark(SorterState sorterState) {
        Arrays.sort(sorterState.copy);
    }

    static Comparable[] getRandomArray() {
        Random random = new Random(2);
        Integer[] integers = new Integer[500000];
        for (int i = 0; i < integers.length; i++) {
            integers[i] = random.nextInt();
        }
        return integers;
    }
}