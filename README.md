# A mergesort space/time efficiency experiment
mergesort is one of the primary sorting algorithms with an efficiency of time = O(nLogn) and space = O(n).
Where n is the length of the array to sort.
O() is Big O Notation, the worst-case efficiency as count (n) goes towards infinity.
The purpose of this project is to explore two potential efficiencies that can improve
the performance of the mergesort algorithm in both space and time complexity.

Four separate classes where made during this experiment:
* mergesort, the control sorting algorithm
* MergeSortLessAlocc, uses only 2 arrays rather than n count of arrays
* MergeSortThreads, uses 2 arrays and multithreading, partitioning arrays in a pyramid style
* MergeSortThreadsDivide, uses 2 arrays and multithreading, partitioning arrays by dividing

Note: The values in this file come from benchmark tests,
these tests are not very accurate and are just an idea of the efficiency.

---
### Space Complexity: MergeSortLessAlocc
Space complexity is the highest amount of storage the algorithm takes at any time during its operation.

The first question someone might raise about the space efficiency of mergesort is if all the extra arrays are required.
The basic mergesort uses a space efficiency of O(n), that is a lot of space being taken up.
Not to mention the extra values being copied from array to array.
My initial approach to this problem was to replace the creation of multiple of different lengths to
a few arrays using upper and lower indices.

---
#### Can we contain the space used to the original array?
Taking a first look at how mergesort method is usually set up makes it seem unnecessary to create any additional arrays.
We could section off the array and sort each section with the one next to it.
This would take a space complexity of O(1). 

However, a problem occurs in the mergesort method which is merging the two sections of the array together.
Both sections of the array will need to be sorted into the same space that they are currently in.
Meaning the mergesort method will have to process into a different array.

---
#### Can we contain the space used to the original array and a copy?
The next best thing is to have a copy of the array to store the mergesort method results in.
Then we would have a space complexity of 2, or O(1)

This works by alternating passing the copy and the output array in the mergeSortRec method.
The copy array provides the mergesort method the space to sort from without having to do any extra copying.

---
#### Space efficiency conclusion
By using an output array and a copy array with indices rather than creating new arrays
the mergesort algorithm doesn't have to allocate as much space as before.
The algorithm now requires the space of only twice the value of n or O(1).

Results:
* The mergeSortLessAlloc algorithm takes anywhere from 95.9184720835%
  to 86.4908427325% as long as the original mergesort.

---
### Time Complexity: MergeSortThreads, MergeSortThreadsDivide
mergesort, unlike some other sorting algorithms partitions its array into smaller pieces.
Hence, why it uses O(nLogn) for its time complexity.
This means that it would be relatively simple to use multithreading to speed up the algorithm.
After approaching the task the first question comes up on how to initially partition the array to each thread.

Too much or too little work for the threads will increase the amount of time the algorithm takes.
* If you divide the array into too few sections, some of your processors won't be working on the algorithm.
* If you divide the array into a few more sections than processors,
  the first threads to complete a little before the others and pick up the new work.
  Which will take twice as long as they already have.
* If you divide the array into way too many sections,
  the extra overhead required will slow down the algorithm and add extra complexity.

Two methods for dividing the array into parts is by a pyramid scheme just like regular mergesort,
or by dividing it into mostly equal sections.

---
#### Divide the array by levels of halves: MergeSortThreads
The first way to designate tasks is by partitioning the tasks into squares of the array.
It divides the array by squares into mostly equal length parts.
The recursive idea behind this method makes it easier to implement how threads will merge the sorted arrays back together.

#### What happens when two neighboring parts of the array are finished?
When two processors have finished their work sorting parts of the array adjacent to each other, 
they need to mergesort together.
But what should spend the time to mergesort them together?
Some options are: 
* The main thread which started the sort operation
* The second thread of the two parts in to finish sorting its section
* Create a new thread for merging them together

The second tread is ideal to mergesort the two parts after it completes its work,
it allows MergeSortThreads to use half the threads previously used to mergesort the next two section together.
This leaves the main thread free to do its own business, scales along with the amount of threads used, 
and doesn't require the algorithm to wait for new thread task to be made.
Not to mention creating a new thread at this level would be complex for the developer.

---
#### Divide the array by the number of available cores: MergeSortThreadsDivide
This idea of dividing the array into parts in this way ensures that 
all processors are doing just the right amount of work.

Rather than dividing the array into a few more parts than there are threads, MergeSortThreadsDivide makes sure
that each thread will finish sorting its individual section at roughly the same time.
This minimises the amount of time before merging two sections together.

#### What happens when two parts of the array are finished on the same array?
Because MergeSortThreadsDivide does not approach partitioning the array recursively, 
each section of the array is not attached to another section.
This allows non-adjacent sections on the same array to be merged together.

When each section is merged, it creates a section chain.
The sections in the chain contain parts of the array in a sorted order.
All sections in a chain are on the same array.
A section chain can have a length of 1 to the total number of sections.
Sections in a section chain are sorted by in order from least to greatest.

Allowing sections to form a section chain adds in extra development complexities.
All sections need to balance out when merging to end up sorted on the input array.
If any section is left out of sorting by being on the wrong array the algorithm would fail.
As a solution, each section starts off on the output array or copy 
depending on whether the number of threads is odd or not.

---
#### Time efficiency conclusion
By partitioning the array and assigning processors the time it takes to complete the algorithm is greatly reduced.
However, because of the nature of Big O Notation and there are a finite number of threads, 
the efficiency remains at O(nLogn).
In this case Big O Notation doesn't describe the efficiencies taken place.
This is not a problem with Big O, it isn't made to describe any small efficiencies,
instead it compares and classifies the complexities of algorithms.
How much time this new algorithm saves compared to n is a complicated question.

Note: unlike mergesort and MergeSortLessAlloc,
MergeSortThreads and MergeSortThreadsDivide uses multiple processors.
Meaning that in addition to the processor speed on the computer,
it's time also **varies greatly based on the number of processors** that the computer has.

Results with 11 processors (the main thread was not doing any work):
* The mergeSortThreads algorithm takes anywhere from 88.1397274772%
  to 73.8346224065% as long as mergesort.
* The mergeSortThreads algorithm takes anywhere from 96.7430380377%
  to 81.253655388% as long as MergeSortLessAlocc.

---
### Benchmark Data
Below is the data used for each of the time efficiency percentages with an array of random integers 
and 11 free logical processors.

    With 500,000 integers
    Benchmark                        Mode   Cnt          Score        Error  Units
    mergeSortBenchmark               avgt  1000  137061322.888 ± 409579.631  ns/op
    mergeSortLessAloccBenchmark      avgt  1000  111756663.718 ± 610055.018  ns/op
    mergeSortThreadsBenchmark        avgt  1000   37237146.617 ± 295364.049  ns/op
    mergeSortThreadsDivideBenchmark  avgt  1000   40014773.594 ± 631713.388  ns/op
    referenceSortBenchmark           avgt  1000  114204377.476 ± 570910.511  ns/op

    With 500,000 integers
    Benchmark                        Mode   Cnt          Score        Error  Units
    mergeSortBenchmark               avgt  1000  130818612.977 ± 729811.076  ns/op
    mergeSortLessAloccBenchmark      avgt  1000  111388992.322 ± 556410.826  ns/op
    mergeSortThreadsBenchmark        avgt  1000   34016566.808 ± 273445.018  ns/op
    mergeSortThreadsDivideBenchmark  avgt  1000   39175946.606 ± 497280.066  ns/op
    referenceSortBenchmark           avgt  1000  112806591.695 ± 725552.210  ns/op

    With 1,000,000 integers
    Benchmark                        Mode   Cnt          Score         Error  Units
    mergeSortBenchmark               avgt  1000  296869844.700 ± 2081476.766  ns/op
    mergeSortLessAloccBenchmark      avgt  1000  346286170.300 ± 2221817.364  ns/op
    mergeSortThreadsBenchmark        avgt  1000  113014873.300 ±  904190.210  ns/op
    mergeSortThreadsDivideBenchmark  avgt  1000  128888370.400 ± 1127988.080  ns/op
    referenceSortBenchmark           avgt  1000  291443285.900 ± 1194862.967  ns/op

    With 1,000,000 integers
    Benchmark                        Mode   Cnt          Score         Error  Units
    mergeSortBenchmark               avgt  1000  296388663.175 ± 2094190.700  ns/op
    mergeSortLessAloccBenchmark      avgt  1000  303615751.697 ± 1351574.766  ns/op
    mergeSortThreadsBenchmark        avgt  1000  103530281.082 ±  632464.859  ns/op
    mergeSortThreadsDivideBenchmark  avgt  1000  119935034.454 ± 1033022.645  ns/op
    referenceSortBenchmark           avgt  1000  291532680.102 ± 1635428.685  ns/op

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