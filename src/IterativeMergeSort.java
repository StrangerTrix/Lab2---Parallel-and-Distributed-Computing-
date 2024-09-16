import java.util.Arrays;

public class IterativeMergeSort {
    public static void sort(int[] array) {
        int n = array.length;
        int[] temp = new int[n];

        for (int currentSize = 1; currentSize < n; currentSize *= 2) {
            for (int leftStart = 0; leftStart < n - currentSize; leftStart += 2 * currentSize) {
                int mid = leftStart + currentSize - 1;
                int rightEnd = Math.min(leftStart + 2 * currentSize - 1, n - 1);

                merge(array, temp, leftStart, mid, rightEnd);
            }
        }
    }


    private static void merge(int[] array, int[] temp, int leftStart, int mid, int rightEnd) {
        int i = leftStart;
        int j = mid + 1;
        int k = leftStart;

        // Step 1: Merge both subarrays into temp in sorted order
        while (i <= mid && j <= rightEnd) {
            if (array[i] <= array[j]) {
                temp[k++] = array[i++];
            } else {
                temp[k++] = array[j++];
            }
        }

        // Step 2: Copy any remaining elements of the left subarray
        while (i <= mid) {
            temp[k++] = array[i++];
        }

        // Step 3: Copy any remaining elements of the right subarray
        while (j <= rightEnd) {
            temp[k++] = array[j++];
        }

        // Step 4: Copy the merged elements back into the original array
        for (i = leftStart; i <= rightEnd; i++) {
            array[i] = temp[i];
        }
    }

    // Main function to test iterative merge sort
    public static void main(String[] args) {

        int[] array = Auxiliary.arrayGenerate(1, 1000);
        IterativeMergeSort.sort(array);
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i] + " ");
        }
        System.out.println();
    }
}
