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
@Warmup(iterations = 2)
@Measurement(iterations = 100, timeUnit = TimeUnit.NANOSECONDS, time = 1)
public class SorterBenchmarks {
    @Benchmark
    public void mergeSortThreadsDivideBenchmark() {
        MergeSortThreadsDivide<Integer> mergeSortThreadsDivide = new MergeSortThreadsDivide<>((Integer[]) getRandomArray().clone());
        mergeSortThreadsDivide.startSort();
    }

    @Benchmark
    public void mergeSortThreadsBenchmark() {
        MergeSortThreads.sortArray(getRandomArray().clone());
    }

    @Benchmark
    public void mergeSortLessAloccBenchmark() {
        MergeSortLessAlocc.sortArray(getRandomArray().clone());
    }

    @Benchmark
    public void mergeSortBenchmark() {
        MergeSort.sortArray(getRandomArray().clone());
    }

    @Benchmark
    public void referenceSortBenchmark() {
        Arrays.sort(getRandomArray().clone());
    }

    Comparable[] getRandomArray() {
        Random random = new Random(2);
        Integer[] integers = new Integer[50000];
        for (int i = 0; i < integers.length; i++) {
            integers[i] = random.nextInt();
        }
        return integers;
    }
}