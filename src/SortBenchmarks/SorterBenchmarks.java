package SortBenchmarks;

import MergeSortThreads.MergeSortThreads;
import OtherSorters.MergeSort;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 2)
@Warmup(iterations = 2)
@Measurement(iterations = 100, timeUnit = TimeUnit.MILLISECONDS, time = 1)
public class SorterBenchmarks {
    @Benchmark
    public void mergeSortThreadsBenchmark() {
        MergeSortThreads.sortArray(getRandomArray().clone());
    }

    @Benchmark
    public void mergeSortBenchmark() {
        MergeSortThreads.sortArray(getRandomArray().clone());
    }

    Comparable[] getRandomArray() {
        Random random = new Random();
        Integer[] integers = new Integer[random.nextInt(10,30)];
        for (int i = 0; i < integers.length; i++) {
            integers[i] = random.nextInt();
        }
        return integers;
    }
}
