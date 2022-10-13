package SortBenchmarks;

import MergeSortThreads.MergeSortThreads;
import OtherSorters.MergeSort;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(value = 2)
@Warmup(iterations = 2)
@Measurement(iterations = 100, timeUnit = TimeUnit.NANOSECONDS, time = 1)
public class SorterBenchmarks {
    @Benchmark
    public void mergeSortThreadsBenchmark() {
        MergeSortThreads.sortArray(getRandomArray().clone());
    }

    @Benchmark
    public void mergeSortBenchmark() {
        MergeSort.sortArray(getRandomArray().clone());
    }

    Comparable[] getRandomArray() {
        Random random = new Random(2);
        Integer[] integers = new Integer[500000];
        for (int i = 0; i < integers.length; i++) {
            integers[i] = random.nextInt();
        }
        return integers;
    }
}
