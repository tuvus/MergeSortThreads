# A mergesort space/time efficiency experiment
Mergesort is one of the primary sorting algorithms with an efficiency of time = O(nLogn) and space = O(n).
Where n is the length of the array to sort.
O() is Big O Notation, the worst-case efficiency as count (n) goes towards infinity.
The purpose of this project is to explore two potential efficiencies that can improve
the performance of the mergesort algorithm in both space and time complexity.

Three separate classes where made during this experiment:
* MergeSort, the control sorting algorithm
* MergeSortLessAlocc, uses only 2 arrays rather than n count of arrays
* MergeSortThreads, uses 2 array and multithreading

Note: The values in this file come from benchmark tests,
these tests are not very accurate and are just an idea of the efficiency.

---
### Space Complexity
The first question someone might raise about mergesort's space efficiency is if all the extra arrays are required.
My initial approach to this problem was to replace the creation of multiple arrays with lower and upper indices.

MergeSortLessAlocc is the class with the efficiencies described bellow.

#### Can we contain the space used to the original array?
Taking a first look at how mergeSort method is usually set up makes it seem unnecessary to create any additional arrays.

However, a problem occurs in the mergeSort method which is merging the two sections of the array together.
Both sections of the array will need to be sorted into the same space that they are currently in.
Meaning the mergeSort method will have to process into a different array.

#### Can we contain the space used to the original array and a copy?
The next best thing is to have a copy of the array to store the mergeSort method results in.

This works by alternating passing the copy and the output array in the mergeSortRec method.
The copy array provides the mergeSort method the space to sort from without having to do any extra copying.

#### Space efficiency conclusion
By using an output array and a copy array with indices rather than creating new arrays
the mergeSort algorithm doesn't have to allocate as much space as before.
The algorithm now requires the space of only twice the value of n or O(1).

Results:
* The mergeSortLessAlloc algorithm takes anywhere from 95.9184720835%
  to 86.4908427325% as long as the original mergeSort.

---
### Time Complexity
Mergesort, unlike other sorting algorithms partitions its array into smaller pieces.
Hence, why it uses O(nlogn) for its time complexity.
This means that it would be relatively simple to use multithreading to speed up the algorithm.
After approaching the task the first question comes up on how to initially partition the array to each thread.

Too many or too little work for the threads will increase the amount of time the algorithm takes.
* If you divide the array into too few sections, some of your processors won't be working on the algorithm.
* If you divide the array into a few more sections than processors,
  the first threads to complete a little before the others and pick up the new work.
  Which will take twice as long as they already have.
* If you divide the array into way too many sections,
  the extra overhead required will slow down the algorithm and add extra complexity.

MergeSortThreads is the class with one of the efficiencies described bellow.

#### Divide the array by the number of available cores
This idea of dividing the array into parts in this way ensures that all processors are doing just the right amount of work.

#### Divide the array by levels of halves
Another way to designate tasks is by partitioning the tasks into squares of the array.
It divides the array by squares into mostly equal length parts.
This idea makes it easier to implement how threads will merge the sorted arrays back together.

#### What happens when two neighboring parts of the array are finished?
When two processors have finished their work on sorting parts of the array, they need to be mergesorted together.
But what should spend the time to mergesort them together?
Some options are: the main, second or last thread.

The second processor is ideal to mergesort the two parts after it completes its work.
This leaves the main thread free to do its own business,
scales along with the amount of threads used and doesn't require a new thread to take over.

MergeSortThreads uses this efficiency because it seems easier to implement.

#### Time efficiency conclusion
By partitioning the array and assigning processors the time it takes to complete the algorithm is reduced.
However, because of the nature of Big O Notation, the efficiency remains at O(nLogn).
How much time this new algorithm made is a very complicated question.
Below are comparisons to mergeSort and mergeSortLessAlloc.

Note: mergeSortThreads incorporates mergeSortLessAlloc's space efficiency.
Therefore, it may be better to compare them together, rather than with mergeSort.

Also note: unlike mergeSort and mergeSortLessAlloc,
mergeSortThreads uses multiple processors.
Meaning that in addition to the processor speed on the computer,
it's time also **varies greatly based on the number of processors** that the computer has.

Results with 11 processors (the main thread was not doing any work):
* The mergeSortThreads algorithm takes anywhere from 88.1397274772%
  to 73.8346224065% as long as MergeSort.
* The mergeSortThreads algorithm takes anywhere from 96.7430380377%
  to 81.253655388% as long as MergeSortLessAlocc.

---
### Benchmark Data
Below is the data used for each of the time efficiency percentages with an array of random integers and 11 free logical processors.

    With 500,000 integers
    Benchmark                        Mode   Cnt          Score         Error  Units
    mergeSortBenchmark               avgt  1000  126441454.400 ±  653519.755  ns/op
    mergeSortLessAloccBenchmark      avgt  1000  187137170.600 ± 2679979.258  ns/op
    mergeSortThreadsBenchmark        avgt  1000   64718159.800 ±  806328.428  ns/op
    mergeSortThreadsDivideBenchmark  avgt  1000   55104145.800 ±  679983.045  ns/op
    referenceSortBenchmark           avgt  1000  151988780.700 ± 1399434.387  ns/op

    With 500,000 integers
    Benchmark                        Mode   Cnt          Score         Error  Units
    mergeSortBenchmark               avgt  1000  136197938.300 ±  705244.269  ns/op
    mergeSortLessAloccBenchmark      avgt  1000  116266659.500 ±  761437.478  ns/op
    mergeSortThreadsBenchmark        avgt  1000   38331051.900 ±  416338.849  ns/op
    mergeSortThreadsDivideBenchmark  avgt  1000   36110493.900 ±  416208.001  ns/op
    referenceSortBenchmark           avgt  1000  110546845.800 ±  600905.478  ns/op

    With 1,000,000 integers
    Benchmark                        Mode   Cnt          Score         Error  Units
    mergeSortBenchmark               avgt  1000  296869844.700 ± 2081476.766  ns/op
    mergeSortLessAloccBenchmark      avgt  1000  346286170.300 ± 2221817.364  ns/op
    mergeSortThreadsBenchmark        avgt  1000  113014873.300 ±  904190.210  ns/op
    mergeSortThreadsDivideBenchmark  avgt  1000  128888370.400 ± 1127988.080  ns/op
    referenceSortBenchmark           avgt  1000  291443285.900 ± 1194862.967  ns/op

---
### Conclusion
The way the mergesort algorithm partitions arrays makes it inefficient,
but allows for some beneficial additions to be made to it.
The first addition reduced the space requirement to twice of the given array to sort.
This puts much less stress on memory, garbage collection, and copying values around.
The second addition seperated the work and gives it to multiple processors to reduce the sorting time.
While these algorithms show great improvements on the sorting time,
it is important to note that benchmarks have a large range of error and results should not be taken for granted.


## Authors
[tuvus](https://github.com/tuvus/) -
**Oskar Niesen** <<oskar-github@niesens.com>> (he/him)

## License
The project is not yet licensed.