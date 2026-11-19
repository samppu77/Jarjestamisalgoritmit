
import java.util.*;
import java.util.function.Consumer;

public class SortTest {

    public static void insertionSort(int[] data) {
        int n = data.length;
        for (int i = 1; i < n; ++i) {
            int key = data[i];
            int j = i - 1;

            while (j >= 0 && data[j] > key) {
                data[j + 1] = data[j];
                j = j - 1;
            }
            data[j + 1] = key;
        }
    }

    public static void bubbleSort(int[] data) {
        int n = data.length;

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (data[j] > data[j + 1]) {
                    int temp = data[j];
                    data[j] = data[j + 1];
                    data[j + 1] = temp;
                }
            }
        }
    }

    public static void shellSort(int[] data) {
        int n = data.length;

        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                int temp = data[i];
                int j = i;

                while (j >= gap && data[j - gap] > temp) {
                    data[j] = data[j - gap];
                    j -= gap;
                }

                data[j] = temp;
            }
        }
    }

    public static void quickSort(int[] data, int low, int high) {
        while (low < high) {
            int pivotIndex = partition(data, low, high);
            int pivot = data[pivotIndex];
            int equalEnd = pivotIndex;

            // Partition put values smaller than the pivot on the left.
            // Gather equal values next to the pivot so they need no further sorting.
            for (int i = pivotIndex + 1; i <= high; i++) {
                if (data[i] == pivot) {
                    equalEnd++;
                    data[i] = data[equalEnd];
                    data[equalEnd] = pivot;
                }
            }

            // Recursing on the smaller side keeps stack depth logarithmic.
            if (pivotIndex - low < high - equalEnd) {
                quickSort(data, low, pivotIndex - 1);
                low = equalEnd + 1;
            } else {
                quickSort(data, equalEnd + 1, high);
                high = pivotIndex - 1;
            }
        }
    }

    public static int partition(int[] data, int low, int high) {
        int middle = low + (high - low) / 2;
        int pivot = data[middle];
        data[middle] = data[high];
        data[high] = pivot;

        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (data[j] < pivot) {
                i++;

                int temp = data[i];
                data[i] = data[j];
                data[j] = temp;
            }
        }

        int temp = data[i + 1];
        data[i + 1] = data[high];
        data[high] = temp;

        return i + 1;
    }

    public static void mergeSort(int[] data, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;

            mergeSort(data, left, mid);
            mergeSort(data, mid + 1, right);

            merge(data, left, mid, right);
        }
    }

    public static void merge(int[] data, int left, int mid, int right) {
        int[] temp = new int[right - left + 1];

        int i = left;
        int j = mid + 1;
        int k = 0;

        while (i <= mid && j <= right) {
            if (data[i] <= data[j]) {
                temp[k++] = data[i++];
            } else {
                temp[k++] = data[j++];
            }
        }

        while (i <= mid) {
            temp[k++] = data[i++];
        }

        while (j <= right) {
            temp[k++] = data[j++];
        }

        for (int x = 0; x < temp.length; x++) {
            data[left + x] = temp[x];
        }
    }

    public static void optimizedMergeSort(int[] data) {
        int[] temp = new int[data.length];
        optimizedMergeSort(data, temp, 0, data.length - 1);
    }

    private static void optimizedMergeSort(int[] data, int[] temp, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;

            optimizedMergeSort(data, temp, left, mid);
            optimizedMergeSort(data, temp, mid + 1, right);

            optimizedMerge(data, temp, left, mid, right);
        }
    }

    private static void optimizedMerge(int[] data, int[] temp, int left, int mid, int right) {
        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            if (data[i] <= data[j]) {
                temp[k++] = data[i++];
            } else {
                temp[k++] = data[j++];
            }
        }

        while (i <= mid) {
            temp[k++] = data[i++];
        }

        while (j <= right) {
            temp[k++] = data[j++];
        }

        for (int x = left; x <= right; x++) {
            data[x] = temp[x];
        }
    }

    public static void printData(int[] data) {
        for (int x : data) {
            System.out.print(x + " ");
        }
        System.out.println("--------------------------");
        System.out.println();
    }

    public static int[] createAscendingData(int n) {
        int[] data = new int[n];

        for (int i = 0; i < data.length; i++) {
            data[i] = i;
        }

        return data;
    }

    public static int[] createDescendingData(int n) {
        int[] data = new int[n];

        for (int i = 0; i < data.length; i++) {
            data[i] = n - 1 - i;
        }

        return data;
    }

    public static int[] createRandomData(int n) {
        int[] data = new int[n];

        Random gen = new Random();

        for (int i = 0; i < data.length; i++) {
            data[i] = gen.nextInt(n);
        }

        return data;
    }

    public static void main(String[] args) {
        int size = 10000;

        testSorts("Random order", createRandomData(size));
        testSorts("Ascending order", createAscendingData(size));
        testSorts("Descending order", createDescendingData(size));
    }

    public static double sortMethodTester(int[] data, Consumer<int[]> sortMethod) {
        int[] expected = data.clone();
        Arrays.sort(expected);
        return sortMethodTester(data, sortMethod, expected);
    }

    static double sortMethodTester(int[] data, Consumer<int[]> sortMethod, int[] expected) {
        int[] copy = data.clone();

        long start = System.nanoTime();
        sortMethod.accept(copy);
        long stop = System.nanoTime();

        if (!Arrays.equals(copy, expected)) {
            throw new IllegalStateException("Sorting result differs from Arrays.sort");
        }

        return (stop - start) / 1000000.0;
    }

    static Map<String, Consumer<int[]>> sortMethods() {
        Map<String, Consumer<int[]>> sortMethods = new LinkedHashMap<>();
        sortMethods.put("Insertion sort", SortTest::insertionSort);
        sortMethods.put("Bubble sort", SortTest::bubbleSort);
        sortMethods.put("Shell sort", SortTest::shellSort);
        sortMethods.put("Quick sort", a -> quickSort(a, 0, a.length - 1));
        sortMethods.put("Merge sort", a -> mergeSort(a, 0, a.length - 1));
        sortMethods.put("Optimized Merge sort", SortTest::optimizedMergeSort);
        sortMethods.put("Arrays.sort", Arrays::sort);
        return sortMethods;
    }

    private static void testSorts(String heading, int[] data) {

        int[] expected = data.clone();
        Arrays.sort(expected);

        System.out.println(heading);
        for (Map.Entry<String, Consumer<int[]>> method : sortMethods().entrySet()) {
            double time = sortMethodTester(data, method.getValue(), expected);
            System.out.println(method.getKey() + " " + time + " ms");
        }
        System.out.println();
    }
}
