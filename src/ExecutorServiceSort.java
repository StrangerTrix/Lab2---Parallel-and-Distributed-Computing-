import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.ArrayList;
import java.util.List;

public class ExecutorServiceSort implements Sorter {
        private final int threads;
        private final ExecutorService executor;
        private static int maxDepth;

        public ExecutorServiceSort(int threads) {
                this.threads = threads;
                this.executor = Executors.newFixedThreadPool(threads);
                this.maxDepth = this.giveMaxDepth();
        }
        private int giveMaxDepth() {
                int number = this.threads;
                int power = 0;
                while (number > 1) {
                        number >>= 1;
                        power++;
                }
                return power;
        }


        @Override
        public void sort(int[] arr) {
                if (arr == null || arr.length < 2) {
                        return;
                }
                try {
                        mergeSort(arr, 0, arr.length - 1, 0);
                } catch (Exception e) {
                        e.printStackTrace();
                } finally {
                        executor.shutdown();
                }
        }

        @Override
        public int getThreads() {
                return threads;
        }

        private void mergeSort(int[] array, int i, int j, int depth) throws Exception {
                if (i < j) {
                        int mid = i + (j - i) / 2;

                        if (depth < this.maxDepth) {

                                List<Future<Void>> futures = new ArrayList<>();
                                futures.add(executor.submit(new Worker(array, i, mid, depth + 1)));
                                futures.add(executor.submit(new Worker(array, mid + 1, j, depth + 1)));

                                for (Future<Void> future : futures) {
                                        future.get();
                                }

                        } else {

                                sequentialSort(array, i, mid);
                                sequentialSort(array, mid + 1, j);
                        }

                        merge(array, i, mid, j);
                }
        }

        private void sequentialSort(int[] array, int i, int j) {
                if (i < j) {
                        int mid = i + (j - i) / 2;
                        sequentialSort(array, i, mid);
                        sequentialSort(array, mid + 1, j);
                        merge(array, i, mid, j);
                }
        }

        private void merge(int[] arr, int left, int mid, int right) {
                int n1 = mid - left + 1;
                int n2 = right - mid;

                int[] leftArray = new int[n1];
                int[] rightArray = new int[n2];

                System.arraycopy(arr, left, leftArray, 0, n1);
                System.arraycopy(arr, mid + 1, rightArray, 0, n2);

                int i = 0, j = 0, k = left;

                while (i < n1 && j < n2) {
                        if (leftArray[i] <= rightArray[j]) {
                                arr[k++] = leftArray[i++];
                        } else {
                                arr[k++] = rightArray[j++];
                        }
                }

                while (i < n1) {
                        arr[k++] = leftArray[i++];
                }

                while (j < n2) {
                        arr[k++] = rightArray[j++];
                }
        }

        private class Worker implements Callable<Void> {
                private final int[] array;
                private final int leftIndex;
                private final int rightIndex;
                private final int depth;

                Worker(int[] array, int leftIndex, int rightIndex, int depth) {
                        this.array = array;
                        this.leftIndex = leftIndex;
                        this.rightIndex = rightIndex;
                        this.depth = depth;
                }

                @Override
                public Void call() throws Exception {
                        mergeSort(array, leftIndex, rightIndex, depth);
                        return null;
                }
        }

        public static void main(String [] args) {
                ExecutorServiceSort sorter = new ExecutorServiceSort(10);
                int[] array = Auxiliary.arrayGenerate(1, 1000);
                sorter.sort(array);
                for (int i = 0; i < array.length; i++) {
                        System.out.print(array[i] + " ");
                }
                System.out.println();
        }

}




