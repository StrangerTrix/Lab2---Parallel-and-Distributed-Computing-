import java.util.*;
import java.util.concurrent.*;

public class ExecutorServiceSort implements Sorter {
    public final int threads;

    public ExecutorServiceSort(int threads) {
        this.threads = threads;
    }

    @Override
    public void sort(int[] arr) {
        if (arr.length <= 1) return;

        int[][] indexes = splitArray(arr);
        ExecutorService executor = Executors.newFixedThreadPool(threads);

        // List to hold Futures for synchronization
        List<Future<?>> futures = new ArrayList<>();

        for (int[] index : indexes) {
            futures.add(executor.submit(new Worker(arr, index)));
        }

        executor.shutdown();
        try {
            // Wait for all tasks to complete
            for (Future<?> future : futures) {
                future.get();  // This will throw an exception if the thread encountered an error
            }
        } catch (InterruptedException | ExecutionException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted or execution error: " + e.getMessage());
        }

        // Merge the sorted subarrays
        for (int i = 1; i < indexes.length; i++) {
            new SequentialSort().merge(arr, indexes[0][0], indexes[i - 1][1], indexes[i][1]);
        }
    }

    public int getThreads() {
        return threads;
    }

    private int[][] splitArray(int[] arr) {
        int chunkSize = arr.length / threads;
        int remainder = arr.length % threads;

        int[][] indexes = new int[threads][2];
        int startIndex = 0;

        for (int i = 0; i < threads; i++) {
            int size = chunkSize + (i < remainder ? 1 : 0);
            indexes[i][0] = startIndex;
            indexes[i][1] = (startIndex + size) -1;
            startIndex += size;
        }

        return indexes;
    }

    private static class Worker implements Runnable {
        private final int[] arr;
        private final int[] index;

        Worker(int[] arr, int[] index) {
            this.arr = arr;
            this.index = index;
        }

        @Override
        public void run() {
            //System.out.println("Thread " + Thread.currentThread().getName()  + " started sorting indices " + index[0] + " to " + (index[1] - 1));
            new SequentialSort().mergeSort(arr, index[0], index[1]);
            //System.out.println("Thread " + Thread.currentThread().getName() + " finished sorting");
        }
    }

    public static void main(String[] args) {
        Random rand = new Random();
        int[] randomNumbers = new int[25];
        for (int i = 0; i < randomNumbers.length; i++) {
            randomNumbers[i] = rand.nextInt(100);
        }

        System.out.println("Unsorted Array (first 10 elements): "
                + Arrays.toString(Arrays.copyOf(randomNumbers, 25)));

        ExecutorServiceSort sorter = new ExecutorServiceSort(4);

        sorter.sort(randomNumbers);

        System.out.println("Sorted Array (first 10 elements): "
                + Arrays.toString(Arrays.copyOf(randomNumbers, 25)));
    }
}
