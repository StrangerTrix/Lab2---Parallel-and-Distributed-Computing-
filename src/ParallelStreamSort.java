import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
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

        public ParallelStreamSort(int threads) {
                this.threads = threads;
        }

        public void sort(int[] arr) {
                ForkJoinPool pool = new ForkJoinPool(threads);

                try {
                        pool.submit(() -> {
                            mergeSort(pool, arr, 0, arr.length - 1);
                        }).get();


                } catch (Exception e) {
                        e.printStackTrace();
                } finally {
                        pool.shutdown();
                }
        }

        private void mergeSort(ForkJoinPool pool, int[] arr, int l, int r) {
                if (l < r) {
                        int m = l + (r - l) / 2;
                        
                        try {
                                System.out.println("Sorting range (" + l + ", " + r + ") on thread: " + Thread.currentThread().getName());
                                Thread.sleep(1000);
                                pool.submit(() -> {
                                        IntStream.range(l,r).parallel().forEach(i -> {
                                            mergeSort(pool, arr, l, m);
                                            //mergeSort(pool, arr, m + 1, r);
                                        });
                                }).get();        
                        } catch (Exception e) {
                                e.printStackTrace();
                        }

                        /*IntStream.of(1).parallel().forEach(i -> {
                                System.out.println("Sorting range (" + l + ", " + r + ") on thread: " + Thread.currentThread().getName());
                                mergeSort(pool, arr, l, m);
                                mergeSort(pool, arr, m + 1, r);
                        });*/

                        merge(arr, l, m, r);
                }
        } 

        private static void merge(int[] arr, int l, int m, int r) {
                        
                // sizes of 2 to-be-merged subarrays
                int n1 = m - l + 1;
                int n2 = r - m; 

                // creating arrays
                int L[] = new int[n1];
                int R[] = new int[n2];


                // filling the arrays
                for (int i = 0; i < n1; i++) {
                        L[i] = arr[l + i];
                }

                for (int i = 0; i < n2; i++) {
                        R[i] = arr[m + 1 + i];
                }

                int x = 0, y = 0;
                int k = l;

                while(x < n1 && y < n2) {
                        if (L[x] <= R[y]) {
                                arr[k] = L[x];
                                x++;
                        } else {
                                arr[k] = R[y];
                                y++;
                        }
                        k++;
                }

                while (x < n1) {
                        arr[k] = L[x];
                        x++;
                        k++;
                }

                while (y < n2) {
                        arr[k] = R[y];
                        y++;
                        k++;
                }
        }

        public int getThreads() {
                return threads;
        }

        public static void main(String[] args) {
                // Create an array of 10,000 random numbers
                Random rand = new Random();
                int[] randomNumbers = new int[10000];
                for (int i = 0; i < randomNumbers.length; i++) {
                    randomNumbers[i] = rand.nextInt(100000);
                }
        
                System.out.println("Unsorted Array (first 10 elements): " + Arrays.toString(Arrays.copyOf(randomNumbers, 10)));
        
                // Initialize sorter with a specified number of threads
                ParallelStreamSort sorter = new ParallelStreamSort(4);
        
                // Sort the array
                sorter.sort(randomNumbers);
        
                System.out.println("Sorted Array (first 10 elements): " + Arrays.toString(Arrays.copyOf(randomNumbers, 10)));
            }

}
