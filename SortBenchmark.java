import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

public class SortBenchmark {
    private static final int[] SIZES = {10_000, 100_000, 1_000_000};
    private static final int WARMUP_ROUNDS = 4;
    private static final int MEASUREMENT_ROUNDS = 7;
    private static final long SEED = 20261119L;
    private static final String QUICK_SORT = "Quick sort";

    private enum Order {
        RANDOM, ASCENDING, DESCENDING;

        int[] create(int size, int round) {
            return switch (this) {
                case RANDOM -> randomData(size, size, round);
                case ASCENDING -> SortTest.createAscendingData(size);
                case DESCENDING -> SortTest.createDescendingData(size);
            };
        }
    }

    public static void main(String[] args) {
        List<Map.Entry<String, Consumer<int[]>>> methods =
                new ArrayList<>(SortTest.sortMethods().entrySet());

        for (int round = 0; round < WARMUP_ROUNDS; round++) {
            measureRound(SIZES[0], round, false, methods);
        }

        System.out.println("size;order;algorithm;trial;time_ms;status");
        for (int size : SIZES) {
            System.err.println("Measuring " + size + " elements");
            for (int round = 0; round < MEASUREMENT_ROUNDS; round++) {
                measureRound(size, round, true, methods);
            }
        }
        checkQuickSortWithDuplicates();
    }

    private static void measureRound(int size, int round, boolean record,
            List<Map.Entry<String, Consumer<int[]>>> methods) {
        Order[] orders = Order.values();
        for (int offset = 0; offset < orders.length; offset++) {
            Order order = orders[(round + offset) % orders.length];
            int[] data = order.create(size, round + (record ? WARMUP_ROUNDS : 0));
            int[] expected = data.clone();
            Arrays.sort(expected);

            // Rotate the order so no algorithm always runs first.
            for (int i = 0; i < methods.size(); i++) {
                Map.Entry<String, Consumer<int[]>> method =
                        methods.get((round + order.ordinal() + i) % methods.size());
                String name = method.getKey();
                if (skip(size, order, name)) {
                    if (record && round == 0) {
                        System.out.printf(Locale.ROOT, "%d;%s;%s;;;skipped%n", size, order, name);
                    }
                    continue;
                }

                double elapsed = SortTest.sortMethodTester(data, method.getValue(), expected);
                if (record) {
                    printMeasurement(size, order.name(), name, round + 1, elapsed);
                }
            }
        }
    }

    private static boolean skip(int size, Order order, String algorithm) {
        return size == 1_000_000 && (
                algorithm.equals("Bubble sort")
                || (algorithm.equals("Insertion sort") && order != Order.ASCENDING));
    }

    private static int[] randomData(int size, int bound, int round) {
        Random random = new Random(SEED + 31L * size + round);
        int[] data = new int[size];
        for (int i = 0; i < size; i++) {
            data[i] = random.nextInt(bound);
        }
        return data;
    }

    private static void checkQuickSortWithDuplicates() {
        int size = 1_000_000;
        Consumer<int[]> quickSort = SortTest.sortMethods().get(QUICK_SORT);
        System.err.println("Checking QuickSort with one and eight distinct values");
        for (int round = 0; round < MEASUREMENT_ROUNDS; round++) {
            int[] equal = new int[size];
            reportQuickSortStress(equal, "ONE_VALUE", round, quickSort);
            int[] repeated = new int[size];
            for (int i = 0; i < repeated.length; i++) {
                repeated[i] = i % 8;
            }
            reportQuickSortStress(repeated, "EIGHT_VALUES", round, quickSort);
        }
    }

    private static void reportQuickSortStress(int[] data, String order, int round,
            Consumer<int[]> quickSort) {
        double elapsed = SortTest.sortMethodTester(data, quickSort);
        printMeasurement(data.length, order, QUICK_SORT, round + 1, elapsed);
    }

    private static void printMeasurement(int size, String order, String algorithm, int trial,
            double elapsed) {
        System.out.printf(Locale.ROOT, "%d;%s;%s;%d;%.6f;ok%n",
                size, order, algorithm, trial, elapsed);
    }
}
