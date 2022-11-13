package SortBenchmarks;

import MergeSortThreads.MergeSortThreads;
import MergeSortThreads.MergeSortThreadsDivide;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
//@Fork(value = 10)
//@Warmup(iterations = 2)
//@Measurement(iterations = 50, timeUnit = TimeUnit.NANOSECONDS, time = 1)
@Fork(value = 0)
@Warmup(iterations = 0)
@Measurement(iterations = 10, timeUnit = TimeUnit.NANOSECONDS, time = 1)
public class SpecificMergeSortThreadSorterBenchmarks {

    @State(Scope.Benchmark)
    public static class SorterState {
        public Integer[] reference;
        public Integer[] copy;

        @Setup(Level.Trial)
        public void setupArray() {
            reference = getRandomArray();
        }

        @Setup(Level.Iteration)
        public void copyArray() {
            copy = reference.clone();
        }

        static <E extends Comparable<? super E>> E[] getRandomArray() {
            Random random = new Random();
            Integer[] integers = new Integer[Integer.MAX_VALUE];
            for (int i = 0; i < integers.length; i++) {
                integers[i] = random.nextInt();
            }
            return (E[]) integers;
        }
    }

    @Benchmark
    public void mergeSortThreadsDivideBenchmark(SortBenchmarks.SorterBenchmarks.SorterState sorterState) {
        MergeSortThreadsDivide<Integer> mergeSortThreadsDivide = new MergeSortThreadsDivide<>(sorterState.copy);
        mergeSortThreadsDivide.start();
        mergeSortThreadsDivide.complete();
    }

    @Benchmark
    public void mergeSortThreadsBenchmark(SortBenchmarks.SorterBenchmarks.SorterState sorterState) {
        MergeSortThreads<Integer> mergeSortThreads = new MergeSortThreads<>(sorterState.copy);
        mergeSortThreads.start();
        mergeSortThreads.complete();
    }
}
