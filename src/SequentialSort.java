public class SequentialSort implements Sorter {

        public SequentialSort() {
        }

        public void sort (int[] arr) {
                mergeSort(arr, 0, arr.length -1);
        }

        public void mergeSort(int[] arr, int l, int r) {
                if (l < r) {
                        // middile point
                        int m = l + (r - 1) / 2;

                        // sort left and right
                        mergeSort(arr, l, m);
                        mergeSort(arr, m+1, r);

                        merge(arr, l, m, r);

                }
        }

        public void merge(int arr[], int l, int m, int r) {
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

                for (int i = 0; i < n1; i++) {
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
                        }
                        k++;
                }

                while (x < n1) {
                        arr[k] = L[x];
                        x++;
                        k++;
                }

                while (y < n2) {
                        arr[k] = L[y];
                        y++;
                        k++;
                }

        }

        public int getThreads() {
                return 1;
        }
}
