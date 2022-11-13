package SortBenchmarks;

import MergeSortThreads.MergeSortThreadsDivide;
import org.openjdk.jmh.annotations.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(value = 1)
@Warmup(iterations = 2)
@Measurement(iterations = 100, timeUnit = TimeUnit.NANOSECONDS, time = 1)
public class MergeSortThreadsDivideBenchmarks {

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
            Random random = new Random(2);
            Integer[] integers = new Integer[500000];
            for (int i = 0; i < integers.length; i++) {
                integers[i] = random.nextInt();
            }
            return (E[])integers;
        }
    }

    @Benchmark
    public void mergeSortThreadsDivideBenchmark7(SorterBenchmarks.SorterState sorterState) {
        MergeSortThreadsDivide<Integer> mergeSortThreadsDivide = new MergeSortThreadsDivide<>(sorterState.copy, 7);
        mergeSortThreadsDivide.start();
        mergeSortThreadsDivide.complete();
    }


    @Benchmark
    public void mergeSortThreadsDivideBenchmark8(SorterBenchmarks.SorterState sorterState) {
        MergeSortThreadsDivide<Integer> mergeSortThreadsDivide = new MergeSortThreadsDivide<>(sorterState.copy, 8);
        mergeSortThreadsDivide.start();
        mergeSortThreadsDivide.complete();
    }


    @Benchmark
    public void mergeSortThreadsDivideBenchmark9(SorterBenchmarks.SorterState sorterState) {
        MergeSortThreadsDivide<Integer> mergeSortThreadsDivide = new MergeSortThreadsDivide<>(sorterState.copy, 9);
        mergeSortThreadsDivide.start();
        mergeSortThreadsDivide.complete();
    }

    @Benchmark
    public void mergeSortThreadsDivideBenchmark10(SorterBenchmarks.SorterState sorterState) {
        MergeSortThreadsDivide<Integer> mergeSortThreadsDivide = new MergeSortThreadsDivide<>(sorterState.copy, 10);
        mergeSortThreadsDivide.start();
        mergeSortThreadsDivide.complete();
    }

    @Benchmark
    public void mergeSortThreadsDivideBenchmark11(SorterBenchmarks.SorterState sorterState) {
        MergeSortThreadsDivide<Integer> mergeSortThreadsDivide = new MergeSortThreadsDivide<>(sorterState.copy,11);
        mergeSortThreadsDivide.start();
        mergeSortThreadsDivide.complete();
    }

    @Benchmark
    public void mergeSortThreadsDivideBenchmark12(SorterBenchmarks.SorterState sorterState) {
        MergeSortThreadsDivide<Integer> mergeSortThreadsDivide = new MergeSortThreadsDivide<>(sorterState.copy,12);
        mergeSortThreadsDivide.start();
        mergeSortThreadsDivide.complete();
    }
}
