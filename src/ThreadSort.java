import java.util.ArrayList;
import java.util.List;

public class ThreadSort implements Sorter {
        private final int threads;
        private static int maxDepth;

        public ThreadSort(int threads) {
                this.threads = threads;
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
                } catch (InterruptedException e) {
                        e.printStackTrace();
                }
        }

        @Override
        public int getThreads() {
                return threads;
        }

        private void mergeSort(int[] array, int i, int j, int depth) throws InterruptedException {
                if (i < j) {
                        int mid = i + (j - i) / 2;

                        if (depth < this.maxDepth) {
                                List<Thread> threads = new ArrayList<>();
                                // Créer et démarrer les threads
                                Thread leftThread = new Thread(new Worker(array, i, mid, depth + 1));
                                Thread rightThread = new Thread(new Worker(array, mid + 1, j, depth + 1));
                                threads.add(leftThread);
                                threads.add(rightThread);

                                leftThread.start();
                                rightThread.start();

                                // Attendre que les threads se terminent
                                for (Thread thread : threads) {
                                        thread.join();
                                }
                        } else {
                                // Trier de manière séquentielle si la profondeur maximale est atteinte
                                sequentialSort(array, i, mid);
                                sequentialSort(array, mid + 1, j);
                        }

                        // Fusionner les sous-tableaux triés
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

        private class Worker implements Runnable {
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
                public void run() {
                        try {
                                mergeSort(array, leftIndex, rightIndex, depth);
                        } catch (InterruptedException e) {
                                e.printStackTrace();
                        }
                }
        }

        public static void main(String[] args) {
                ThreadSort sorter = new ThreadSort(10);
                int[] array = Auxiliary.arrayGenerate(1, 1000);
                sorter.sort(array);
                for (int i = 0; i < array.length; i++) {
                        System.out.print(array[i] + " ");
                }
                System.out.println();
        }
}
