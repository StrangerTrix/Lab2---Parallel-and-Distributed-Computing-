import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorServiceSort implements Sorter {
        private final int threads;
        private static final int threshold = 512;
    
        public ExecutorServiceSort(int threads) {
            this.threads = threads;
        }
    
        public void sort(int[] arr) {
            ExecutorService executor = Executors.newFixedThreadPool(threads);
            Worker initialTask = new Worker(arr, 0, arr.length - 1, executor);
            Future<?> future = executor.submit(initialTask);
            try {
                future.get(); // Wait for the sorting to complete
            } catch (Exception e) {
                e.printStackTrace();
            }
            executor.shutdown();
        }
    
        public int getThreads() {
            return threads;
        }
    
        private static class Worker implements Runnable {
            private int[] arr;
            private int begin;
            private int end;
            private ExecutorService executor;
    
            Worker(int[] arr, int begin, int end, ExecutorService executor) {
                this.arr = arr;
                this.begin = begin;
                this.end = end;
                this.executor = executor;
            }
    
            public void run() {
                if (begin >= end) {
                    return;
                }
    
                if (end - begin + 1 <= threshold) {
                    // Use sequential sort
                    sequentialSort(arr, begin, end);
                    return;
                }
    
                int mid = begin + (end - begin) / 2;
    
                Worker leftTask = new Worker(arr, begin, mid, executor);
                Worker rightTask = new Worker(arr, mid + 1, end, executor);
    
                Future<?> leftFuture = executor.submit(leftTask);
                Future<?> rightFuture = executor.submit(rightTask);
    
                try {
                    leftFuture.get();
                    rightFuture.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
    
                // Merge the sorted halves
                merge(arr, begin, mid, end);
            }
    
            private void sequentialSort(int[] arr, int begin, int end) {
                java.util.Arrays.sort(arr, begin, end + 1); // Arrays.sort is exclusive of the upper bound
            }
    
            private void merge(int[] arr, int begin, int mid, int end) {
                int[] temp = new int[end - begin + 1];
                int i = begin;
                int j = mid + 1;
                int k = 0;
    
                while (i <= mid && j <= end) {
                    if (arr[i] <= arr[j]) {
                        temp[k++] = arr[i++];
                    } else {
                        temp[k++] = arr[j++];
                    }
                }
    
                while (i <= mid) {
                    temp[k++] = arr[i++];
                }
    
                while (j <= end) {
                    temp[k++] = arr[j++];
                }
    
                System.arraycopy(temp, 0, arr, begin, temp.length);
            }
        }


        public static void main(String[] args) {
                // Create an array of 10,000 random numbers
                Random rand = new Random();
                int[] randomNumbers = new int[10000];
                for (int i = 0; i < randomNumbers.length; i++) {
                        randomNumbers[i] = rand.nextInt(10000);
                }

                System.out.println("Unsorted Array (first 10 elements): " + Arrays.toString(Arrays.copyOf(randomNumbers, 10)));

                // Initialize sorter with a specified number of threads
                ExecutorServiceSort sorter = new ExecutorServiceSort(4);

                // Sort the array
                sorter.sort(randomNumbers);

                System.out.println("Sorted Array (first 10 elements): " + Arrays.toString(Arrays.copyOf(randomNumbers, 10)));
        }
}
