# A Mergesort space/time efficiency experiment
Mergesort is one of the primary sorting algorithms with an efficiency of time = O(nLogn) and space = O(n).
Where n is the length of the array to sort.
O() is Big O Notation, the worst-case efficiency as count (n) goes towards infinity.
The purpose of this project is to explore two potential efficiencies that can improve
the performance of the Mergesort algorithm in both space and time complexity.

Four separate classes where made during this experiment:
* Mergesort, the control sorting algorithm
* MergeSortLessAlocc, uses only 2 arrays rather than n count of arrays
* MergeSortThreads, uses 2 arrays and multithreading, partitioning arrays in a pyramid style
* MergeSortThreadsDivide, uses 2 arrays and multithreading, partitioning arrays by dividing

Note: The values in this file come from benchmark tests,
these tests are not very accurate and are just an idea of the efficiency.

---
### Space Complexity: MergeSortLessAlocc
Space complexity is the highest amount of storage the algorithm takes at any time during its operation.

The first question someone might raise about the space efficiency of Mergesort is if all the extra arrays are required.
The basic Mergesort uses a space efficiency of O(n), that is a lot of space being taken up.
Not to mention the extra values being copied from array to array.
My initial approach to this problem was to replace the creation of multiple of different lengths to
a few arrays using upper and lower indices.

---
#### Can we contain the space used to the original array?
Taking a first look at how Mergesort method is usually set up makes it seem unnecessary to create any additional arrays.
We could section off the array and sort each section with the one next to it.
This would take a space complexity of O(1). 

However, a problem occurs in the Mergesort method which is merging the two sections of the array together.
Both sections of the array will need to be sorted into the same space that they are currently in.
Meaning the Mergesort method will have to process into a different array.

---
#### Can we contain the space used to the original array and a copy?
The next best thing is to have a copy of the array to store the Mergesort method results in.
Then we would have a space complexity of 2, or O(1)

This works by alternating passing the copy and the output array in the mergeSortRec method.
The copy array provides the Mergesort method the space to sort from without having to do any extra copying.

---
#### Space efficiency conclusion
By using an output array and a copy array with indices rather than creating new arrays
the Mergesort algorithm doesn't have to allocate as much space as before.
The algorithm now requires the space of only twice the value of n or O(1).

Results with sorting 500,000 integers:
* The mergeSortLessAlloc algorithm takes anywhere from 87.44%
  to 86.41% as long as the original Mergesort.

---
### Time Complexity: MergeSortThreads, MergeSortThreadsDivide
Mergesort, unlike some other sorting algorithms partitions its array into smaller pieces.
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

Two methods for dividing the array into parts is by a pyramid scheme just like regular Mergesort,
or by dividing it into mostly equal sections.

---
#### Divide the array by levels of halves: MergeSortThreads
The first way to designate tasks is by partitioning the tasks into squares of the array.
It divides the array by squares into mostly equal length parts.
The recursive idea behind this method makes it easier to implement how threads will merge the sorted arrays back together.

#### What happens when two neighboring parts of the array are finished?
When two processors have finished their work sorting parts of the array adjacent to each other, 
they need to Mergesort together.
But what should spend the time to Mergesort them together?
Some options are: 
* The main thread which started the sort operation
* The second thread of the two parts in to finish sorting its section
* Create a new thread for merging them together

The second tread is ideal to Mergesort the two parts after it completes its work,
it allows MergeSortThreads to use half the threads previously used to Mergesort the next two section together.
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

Note: unlike Mergesort and MergeSortLessAlloc,
MergeSortThreads and MergeSortThreadsDivide use multiple processors.
This means that in addition to the processor speed on the computer,
it's time also **varies greatly based on the number of processors** that the computer has.

Results with sorting 500,000 integers with 11 processors (the main thread was not doing any work):
* The mergeSortThreads algorithm takes anywhere from 27.05%
  to 26.47% as long as Mergesort.
* The mergeSortThreadsDivide algorithm takes anywhere from 27.28%
    to 26.81% as long as Mergesort.

---
#### Comparing mergeSortThreads with mergeSortThreadsDivide
Looking at the data above indicates that mergeSortThreads is slightly quicker than mergeSortThreadsDivide.
However, the implementation design suggests that mergeSortThreadsDivide should be quicker than mergeSortThreads.

MergeSortThreads uses the same pyramid based partitioning as the recursive mergeSort method. 
This makes the algorithm not unspecialized to the number of threads that the computer can handle.

For example, if the hardware supports 12 threads running at the same time (one for the main thread), 
leaving 11 threads for sorting. MergeSortThreads will divide the array into a total of 16 sections. 
Generally 11 of these section will complete at the same time, 5 now free threads will work on the remaining sections,
while 5 more threads will merge 10 sections together.

What most likely is happening is, 
that the chaining sections together feature in MergeSortThreadsDivide is slowing down the mergeSections method. 
When MergeSortThreads has to merge two sections together and one section has been completely copied,
it uses System.arraycopy over the other section to the output array.
MergeSortThreadsDivide, however needs to pause at the end of each section in both the output array 
and the input section chain.
System.arraycopy is a native method, meaning that it is running on a separate computer timescale,
along with copying more bytes in one operation. 
The result is that it is much more efficient than manually looping through the array.
The benefit from having an unbroken up System.arraycopy call seems to be much more beneficial 
than the improvements in the section chaining feature in MergeSortThreadsDivide.

MergeSortThreadsDivide might also give better performance when comparing each object takes a variable amount of time,
resulting in some arrays to be sorted quicker than others. 
Essentially maximising the chaining feature's ability to keep the threads running when they can.
The chaining feature also comes to play with smaller arrays. 
As the array size decreases the time that each thread takes to complete, along with the thread start times,
is more volatile. 
Making it more likely that a thread will merge with another section instead of waiting for its adjacent section.


#### What happens if we have one twice as large array? MergeSortThreadsDivideOneArray
While preforming Mergesort, the sorting needs to be put in another space at least the size of the original unsorted section.
Usually another array is created and sorting is done between the original and the copy.
But would it be faster if we had an array that was twice the size of the original and sorted only on that array.
The array would be divided into a left and a right section instead of alternating between two arrays.

This version of MergeSort tests if the processors have any inefficiencies with jumping around from array to array,
and tries to solve them by storing them in one array instead of two.

#### Time efficiency conclusion
There wasn't any benefit to the time efficiency, although the sort could be improved more this test.
It is also important to take into account that the two arrays in mergeSortThreads where created in a relatively
simple environment and their location in memory is already very close to each other.
If there is a negative impact from the distance of the arrays in memory these tests would not have fully captured it.

Results with sorting 500,000 integers with 11 processors (the main thread was not doing any work):
* The mergeSortThreadsDivideOneArray algorithm takes anywhere from 145.56%
  to 137.28% as long as mergeSortThreadsDivide.

#### Arrays.Sort note
Another test is to do the primary sorting using Arrays.Sort instead of the custom merge sort.
The test ended up slower than MergeSortThreadsDivide.

---
### Benchmark Data
In SortBenchmarks/BenchmarkResults.txt is the data used for each of the time efficiency percentages with an 
array of random integers and 11 free logical processors.

    Here is the data used for all the values above sorting 500,000 integers. 

    Benchmark                        Mode   Cnt          Score        Error  Units
    mergeSortBenchmark               avgt  1000  125309254.200 ± 434331.902  ns/op
    mergeSortLessAloccBenchmark      avgt  1000  108928685.000 ± 273208.505  ns/op
    mergeSortThreadsBenchmark        avgt  1000   33541854.500 ± 245840.179  ns/op
    mergeSortThreadsDivideBenchmark  avgt  1000   33894834.100 ± 172906.242  ns/op
    referenceSortBenchmark           avgt  1000  106692850.600 ± 334179.873  ns/op

    Here is the data for comparing mergeSortThreads with mergeSortThreadsDivide, 
    with varying ammount of thread partitions for me mergeSortThreadsDivide.
    Benchmark                            Mode  Cnt          Score         Error  Units
    mergeSortThreadsBenchmark            avgt  400  223721030.778 ± 2851374.754  ns/op
    mergeSortThreadsDivideBenchmark11    avgt  400  258820320.813 ± 2714623.932  ns/op
    mergeSortThreadsDivideBenchmark19    avgt  400  285389804.312 ± 3159131.363  ns/op
    mergeSortThreadsDivideBenchmark18    avgt  400  271974930.896 ± 3123130.281  ns/op
    mergeSortThreadsDivideBenchmarkNeg15 avgt  400  250692269.708 ± 3478338.671  ns/op

    Benchmark                                Mode   Cnt         Score        Error  Units
    mergeSortThreadsDivideBenchmark          avgt  1000  36016176.400 ± 477750.032  ns/op
    mergeSortThreadsDivideOneArrayBenchmark  avgt  1000  50938524.000 ± 839397.484  ns/op
---

### Conclusion
The traditional way the Mergesort algorithm partitions arrays into two halves is inefficient, 
but allows for some simple improvements to be made.

While these algorithms show great improvements on the sorting time,
it is important to note that benchmarks have a large range of error and results should not be taken for granted.
The relative sorting time between each sorting method also depends on the machine and size of the array to sort.

The first improvement is to reduce the space requirement to twice of the given array to sort.
Recursively switching which arrays are the input and output 
guarantees that the completely sorted array will end up on the wanted output array.
This puts much less stress on memory, garbage collection, and copying values around.

The second improvement separates the work out to multiple threads, reducing the total sorting time.
Once two threads are done one thread will stop and the other will merge and sort the two sections together.
It is also important to understand that while the array is being sorted in less time, it takes more resources total to
sort the array with multiple threads.
In theory if the computer would be using all of its processors 100% efficiently
it would be better to sort the array a single thread.
However, with most programs being run on only one thread the decrease in time the main thread has to wait outweighs
the extra cost.

Another interesting idea to create one array that is twice as large did not reduce the sorting time.
However, the experiment does provide extra insight into how the memory works.

For future research a new MergeSortThreadsDivide class should be made without the chaining feature to see if 
performance can be farther improved.
It may also be quicker to use the built-in sort method instead of the regular mergesort once the array has been
partition out to the threads.
While it may not be quicker, a third array for MergeSortThreadsDivide might allow the section chains to merge while
not on the same array.


## Authors
[tuvus](https://github.com/tuvus/) -
**Oskar Niesen** <<oskar-github@niesens.com>> (he/him)

## License
MergeSortThreads is licensed under [MIT](https://github.com/tuvus/MergeSortThreads/blob/master/LICENSE.md).