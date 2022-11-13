package SortBenchmarks;

import MergeSortThreads.MergeSortThreads;
import MergeSortThreads.MergeSortThreadsDivide;
import OtherSorters.MergeSort;
import OtherSorters.MergeSortLessAlocc;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.*;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(value = 5)
@Warmup(iterations = 5)
@Measurement(iterations = 200, timeUnit = TimeUnit.NANOSECONDS, time = 1)
//@Fork(value = 3)
//@Warmup(iterations = 0)
//@Measurement(iterations = 1, timeUnit = TimeUnit.NANOSECONDS, time = 1)
public class SorterBenchmarks {
    static final int forks = 2;
    static final String seedPath = "src/SortBenchmarks/seeds.txt";
    static final String forkIndexPath = "src/SortBenchmarks/forkIndex.txt";

    /**
     * Runs the Benchmark tests just for this class.
     * Uses 2 temporary files to manage consistent seed generation between sorters.
     * Each fork has a different seed.
     */
    public static void main(String[] args) throws Exception {
        File seedFile = new File(seedPath);
        if (seedFile.exists()) {
            if (!seedFile.delete()) {
                throw new Exception("Problem deleting file!");
            }
        }
        if (!seedFile.createNewFile()) {
            throw new Exception("Problem creating file!");
        }
        File forkFile = new File(forkIndexPath);
        if (forkFile.exists()) {
            if (!forkFile.delete()) {
                throw new Exception("Problem deleting file!");
            }
        }
        if (!forkFile.createNewFile()) {
            throw new Exception("Problem creating file!");
        }
        FileWriter writer = new FileWriter(seedFile);
        for (int i = 0; i <= forks; i++) {
            writer.write(new Random().nextInt() + " ");
        }
        writer.close();
        FileWriter forkWriter = new FileWriter(forkFile);
        forkWriter.write(0 + "");
        forkWriter.close();
        new Runner(new OptionsBuilder()
                .exclude(SpecificMergeSortThreadSorterBenchmarks.class.getSimpleName())
                .exclude(MergeSortThreadsDivideBenchmarks.class.getSimpleName())
                .include(SorterBenchmarks.class.getSimpleName())
                .build()).run();

        seedFile.delete();
        forkFile.delete();
    }

    @State(Scope.Benchmark)
    public static class SorterState {
        public Integer[] reference;
        public Integer[] copy;

        @Setup(Level.Trial)
        public void setupArray() throws FileNotFoundException {
            try (Scanner scanner = new Scanner(new File(seedPath));
                 Scanner forkScanner = new Scanner(new File(forkIndexPath))) {
                int forkIndex = forkScanner.nextInt();
                for (int i = 0; i < forkIndex; i++) {
                    scanner.nextInt();
                }
                reference = getRandomArray(scanner.nextInt());
                if (!scanner.hasNextInt()) {
                    forkIndex = 0;
                } else {
                    forkIndex++;
                }
                try (FileWriter writer = new FileWriter(forkIndexPath)) {
                    writer.write(forkIndex + "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Setup(Level.Iteration)
        public void copyArray() {
            copy = reference.clone();
        }

        static <E extends Comparable<? super E>> E[] getRandomArray(int seed) {
            System.out.println(seed);
            Random random = new Random(seed);
            Integer[] integers = new Integer[1000000];
            for (int i = 0; i < integers.length; i++) {
                integers[i] = random.nextInt();
            }
            return (E[]) integers;
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
}