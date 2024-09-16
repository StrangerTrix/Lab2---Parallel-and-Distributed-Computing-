import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.IntStream;

/**
 * Sort using Java's ParallelStreams and Lambda functions.
 *
 * Hints:
 * - Do not take advice from StackOverflow.
 * - Think outside the box.
 *      - Stream of threads?
 *      - Stream of function invocations?
 *
 * By default, the number of threads in parallel stream is limited by the
 * number of cores in the system. You can limit the number of threads used by
 * parallel streams by wrapping it in a ForkJoinPool.
 *      ForkJoinPool myPool = new ForkJoinPool(threads);
 *      myPool.submit(() -> "my parallel stream method / function");
 */

public class ParallelStreamSort implements Sorter {
        public final int threads;
        private final int threshold = 16;

        public ParallelStreamSort(int threads) {
                this.threads = threads;
        }

        public void sort(int[] arr) {
        
                try {
                ForkJoinPool pool = new ForkJoinPool(threads);
                    pool.submit(() -> {
                        mergeSort(arr, 0, arr.length -1 );
                    }).get(); // Wait for all tasks to finish
                    pool.shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private void mergeSort(int[] arr, int l, int r) {
                //System.out.println("Sorting range (" + l + ", " + r + ") on thread: " + Thread.currentThread().getName());
                /*try {
                        Thread.sleep(1000);
                } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }*/
                if (r <= l) return; // Base case for recursion
                if (r > l) {
                        if (r - l < threshold) {
                                new SequentialSort().mergeSort(arr, l, r);
                                return;
                            }
                        
                            int m = l + (r - l) / 2;
                        
                            // Process left and right halves in parallel using parallelStream
                            Arrays.asList(new int[][]{{l, m}, {m + 1, r}}).parallelStream().forEach(range -> {
                                mergeSort(arr, range[0], range[1]);
                            });
                        
                            new SequentialSort().merge(arr, l, m, r);
                }
                
            }

        public int getThreads() {
                return threads;
        }

        public static void main(String[] args) {
                // Create an array of 10,000 random numbers
                Random rand = new Random();
                int[] randomNumbers = new int[100];
                for (int i = 0; i < randomNumbers.length; i++) {
                    randomNumbers[i] = rand.nextInt(100);
                }
        
                System.out.println("Unsorted Array (first 10 elements): " + Arrays.toString(Arrays.copyOf(randomNumbers, 100)));
        
                // Initialize sorter with a specified number of threads
                ParallelStreamSort sorter = new ParallelStreamSort(4);
        
                // Sort the array
                sorter.sort(randomNumbers);
        
                System.out.println("Sorted Array (first 10 elements): " + Arrays.toString(Arrays.copyOf(randomNumbers, 100)));
            }

}
