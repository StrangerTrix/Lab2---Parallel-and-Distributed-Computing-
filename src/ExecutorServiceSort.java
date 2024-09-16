import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorServiceSort implements Sorter
{
        private final int threads;
        private static final int threshold = 512;

        public ExecutorServiceSort(int threads)
        {
                this.threads = threads;
        }

        public void sort(int[] arr)
        {
                AtomicInteger tasks = new AtomicInteger(0); // Track number of active tasks
                ExecutorService executor = Executors.newFixedThreadPool(threads);
                executor.submit(new Worker(arr, 0, arr.length - 1, executor, tasks));
                while (tasks.get() > 0) {
                        // Wait for all tasks to finish
                }
                executor.shutdown();
        }

        public int getThreads()
        {
                return threads;
        }

        // Merge Sort Worker Class
        private static class Worker implements Runnable
        {
                private int[] arr;
                private int begin;
                private int end;
                private ExecutorService executor;
                private AtomicInteger tasks;

                Worker(int[] arr, int begin, int end, ExecutorService executor, AtomicInteger tasks)
                {
                        this.arr = arr;
                        this.begin = begin;
                        this.end = end;
                        this.executor = executor;
                        this.tasks = tasks;
                        tasks.incrementAndGet();
                }

                public void run()
                {
                        if (begin >= end)  {
                                tasks.decrementAndGet();
                                return;
                        }

                        int mid = (begin + end) / 2;

                        // If the subarray size is larger than the threshold, process in parallel
                        boolean processLeftInParallel = (mid - begin + 1) > threshold;
                        boolean processRightInParallel = (end - mid) > threshold;

                        if (processLeftInParallel) {
                                executor.submit(new Worker(arr, begin, mid, executor, tasks));
                        }
                        else {
                                new Worker(arr, begin, mid, executor, tasks).run();
                        }

                        if (processRightInParallel) {
                                executor.submit(new Worker(arr, mid + 1, end, executor, tasks));
                        }
                        else
                        {
                                new Worker(arr, mid + 1, end, executor, tasks).run();
                        }


                        merge(arr, begin, mid, end);

                        tasks.decrementAndGet();
                }


                private void merge(int[] arr, int left, int mid, int right)
                {
                        int n1 = mid - left + 1;
                        int n2 = right - mid;

                        int[] leftArray = new int[n1];
                        int[] rightArray = new int[n2];

                        System.arraycopy(arr, left, leftArray, 0, n1);
                        System.arraycopy(arr, mid + 1, rightArray, 0, n2);

                        int i = 0, j = 0;
                        int k = left;
                        while (i < n1 && j < n2)
                        {
                                if (leftArray[i] <= rightArray[j])
                                {
                                        arr[k] = leftArray[i];
                                        i++;
                                }
                                else
                                {
                                        arr[k] = rightArray[j];
                                        j++;
                                }
                                k++;
                        }

                        while (i < n1)
                        {
                                arr[k] = leftArray[i];
                                i++;
                                k++;
                        }

                        // Copy the remaining elements of rightArray
                        while (j < n2)
                        {
                                arr[k] = rightArray[j];
                                j++;
                                k++;
                        }
                }
        }


        public static void main(String[] args) {
                // Create an array of 10,000 random numbers
                Random rand = new Random();
                int[] randomNumbers = new int[1000000];
                for (int i = 0; i < randomNumbers.length; i++) {
                        randomNumbers[i] = rand.nextInt(100000);
                }

                System.out.println("Unsorted Array (first 10 elements): " + Arrays.toString(Arrays.copyOf(randomNumbers, 10)));

                // Initialize sorter with a specified number of threads
                ExecutorServiceSort sorter = new ExecutorServiceSort(4);

                // Sort the array
                sorter.sort(randomNumbers);

                System.out.println("Sorted Array (first 10 elements): " + Arrays.toString(Arrays.copyOf(randomNumbers, 10)));
        }
}
