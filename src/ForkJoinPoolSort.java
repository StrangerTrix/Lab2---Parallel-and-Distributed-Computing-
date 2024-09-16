/**
 * Sort using Java's ForkJoinPool.
 */

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinPoolSort implements Sorter {
        public final int threads;

        public ForkJoinPoolSort(int threads) {
                this.threads = threads;
        }

        public void sort(int[] arr) {
                ForkJoinPool pool = new ForkJoinPool(threads);

                pool.invoke(new Worker(arr, 0, arr.length - 1));

                pool.shutdown();
        }

        public int getThreads() {
                return threads;
        }

        private static class Worker extends RecursiveAction {
                private final int[] arr;
                private final int l;
                private final int r;
                private static final int threshold = 16;
                // TODO: change to sequentialSort when threshold is met
                
                Worker(int[] arr, int l, int r) {
                        this.arr = arr;
                        this.l = l;
                        this.r = r;
                }

                protected void compute() {
                        if (l < r) {
                                //System.out.println("Sorting range (" + l + ", " + r + ") on thread: " + Thread.currentThread().getName());
                                /*try {
                                        Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                }*/
                                int m = l + (r - l) / 2;

                                if (r - l < 16) {
                                        new SequentialSort().mergeSort(arr, l, r);
                                        return;
                                }

                                Worker lTask = new Worker(arr, l , m);
                                Worker rTask = new Worker(arr, m + 1, r);

                                invokeAll(lTask, rTask);

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
                ForkJoinPoolSort sorter = new ForkJoinPoolSort(4);
        
                // Sort the array
                sorter.sort(randomNumbers);
        
                System.out.println("Sorted Array (first 10 elements): " + Arrays.toString(Arrays.copyOf(randomNumbers, 10)));
            }
}
