# Lab 2 - Java Parallel Programming and Sorting Algorithms
- Group 6
- Dussud, Simon and Wang, Carl

## Task 1: Sequential Sort
We chose to implement MergeSort, the implementation is taken from the website GeeksForGeeks, considering the sequential sort implementation is only a small part in this lab and was already taught in previous courses. The link is: https://www.geeksforgeeks.org/merge-sort/

Source files:

- `SequentialSort.java`

## Task 2: Amdahl's Law

Our Amdahl's law ...

Here is a plot of our version of Amdahl's law ...

![amdahl's law plot](data/amdahl.png)

We see that ...

## Task 3: ExecutorServiceSort
Due to the reason that Java's executor service can not easily manage recursive calls. Our solution is to divide the sub tasks before calling the workers. Using the method splitArray(), an arraylist containing evenly-distributed subarrays' indexes in the unsorted array will be returned(based on the number of threads). This is further used to call workers to sort subarrays concurrently. After terminating the executor, the unsorted array is modified into an array with X(number of threads) sorted subrrays. In the end these subarrays will be merged one by one.

Source files:

- `ExecutorServiceSort.java`

## Task 4: ForkJoinPoolSort
ForkJoinPool() is the currently standard for such tasks that are meant to be broken into smaller pieces recursively. The implementation is pretty clear, the functionality of parallel sorting is simply achieved by distribute currently sorting subarrays into 2 pieces and submit the tasks of processing these 2 subarrays to the pool and the pool will use/re-use a thread to do the job. 

Source files:

- `ForkJoinPoolSort.java`

## Task 5: ParallelStreamSort
This task was a bit tricky and took some time for us to think about. Our solution is, like ExecutorServiceSort, make the indexes as parallel streams, each pair of the indexes, say (l, m) and (m+1, r), will be used to call merge sort concurrently. 

Source files:

- `ParallelStreamlSort.java`

## Task 6: Performance measurements with PDC

We decided to sort 10,000,000 integers ...

![pdc plot](data/pdc.png)

We see that ...
