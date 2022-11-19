package SortBenchmarks;

import MergeSortThreads.MergeSortThreads;
import MergeSortThreads.MergeSortThreadsDivide;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
//@Fork(value = 10)
//@Warmup(iterations = 2)
//@Measurement(iterations = 50, timeUnit = TimeUnit.NANOSECONDS, time = 1)
@Fork(value = 2)
@Warmup(iterations = 5)
@Measurement(iterations = 200, timeUnit = TimeUnit.NANOSECONDS, time = 1)
@Threads(12)
public class SpecificMergeSortThreadSorterBenchmarks {

    static final int forks = 10;
    static final String seedPath = "src/SortBenchmarks/seeds.txt";
    static final String forkIndexPath = "src/SortBenchmarks/forkIndex.txt";

    /**
     * Runs the Benchmark tests just for this class.
     * Uses 2 temporary files to manage consistent seed generation between sorters.
     * Each fork has a different seed.
     */
    public static void main(String[] args) throws Exception {
        for (int f = 0; f < 1; f++) {
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
            Random random = new Random(5125);
            for (int i = 0; i <= forks; i++) {
                writer.write(random.nextInt() + " ");
            }
            writer.close();
            FileWriter forkWriter = new FileWriter(forkFile);
            forkWriter.write(0 + "");
            forkWriter.close();
            new Runner(new OptionsBuilder()
//                    .exclude(SorterBenchmarks.class.getSimpleName())
//                    .exclude(MergeSortThreadsDivideBenchmarks.class.getSimpleName())
                    .include(SpecificMergeSortThreadSorterBenchmarks.class.getSimpleName())
                    .build()).run();

            seedFile.delete();
            forkFile.delete();
        }
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
            Random random = new Random(seed);
            Integer[] integers = new Integer[500000];
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
    public void mergeSortThreadsDivideBenchmark1(SorterState sorterState) {
        MergeSortThreadsDivide<Integer> mergeSortThreadsDivide = new MergeSortThreadsDivide<>(sorterState.copy, (int)((Runtime.getRuntime().availableProcessors() + 1) * 1.5));
        mergeSortThreadsDivide.start();
        mergeSortThreadsDivide.complete();
    }

    @Benchmark
    public void mergeSortThreadsDivideBenchmarkNeg1(SorterState sorterState) {
        MergeSortThreadsDivide<Integer> mergeSortThreadsDivide = new MergeSortThreadsDivide<>(sorterState.copy, (int)((Runtime.getRuntime().availableProcessors() - 1) * 1.5));
        mergeSortThreadsDivide.start();
        mergeSortThreadsDivide.complete();
    }

    @Benchmark
    public void mergeSortThreadsDivideBenchmark2(SorterState sorterState) {
        MergeSortThreadsDivide<Integer> mergeSortThreadsDivide = new MergeSortThreadsDivide<>(sorterState.copy, (int)((Runtime.getRuntime().availableProcessors()) * 1.5));
        mergeSortThreadsDivide.start();
        mergeSortThreadsDivide.complete();
    }

    @Benchmark
    public void mergeSortThreadsBenchmark(SorterState sorterState) {
        MergeSortThreads<Integer> mergeSortThreads = new MergeSortThreads<>(sorterState.copy);
        mergeSortThreads.start();
        mergeSortThreads.complete();
    }
}
