import java.util.Arrays;
import java.util.Random;

public class SequentialSort implements Sorter {

        public SequentialSort() {

        }

        public void sort (int[] arr) {
                mergeSort(arr, 0, arr.length -1);
        }

        public void mergeSort(int[] arr, int l, int r) {
                if (l < r) {
                        // middile point
                        int m = l + (r - l) / 2;

                        // sort left and right
                        mergeSort(arr, l, m);
                        mergeSort(arr, m+1, r);

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
                return 1;
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
                SequentialSort sorter = new SequentialSort();
        
                // Sort the array
                sorter.sort(randomNumbers);
        
                System.out.println("Sorted Array (first 10 elements): " + Arrays.toString(Arrays.copyOf(randomNumbers, 10)));
            }
}
