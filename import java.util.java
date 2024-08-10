import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PrimeAndMultipleSum {

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a number: ");
        int n = scanner.nextInt();

        if (n < 10 || n > 500000000) {
            System.err.println("Invalid input: " + n);
            System.exit(1);
        }

        ExecutorService executor = Executors.newFixedThreadPool(2);
        PrimeSumThread primeSumThread = new PrimeSumThread(3, n);
        MultipleSumThread multipleSumThread = new MultipleSumThread(3, n);

        executor.execute(primeSumThread);
        executor.execute(multipleSumThread);

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        System.out.printf("%d%n ", primeSumThread.getSum());
        System.out.printf("%d%n", multipleSumThread.getSum());
    }

    private static class PrimeSumThread implements Runnable {
        public static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
        private final int start;
        private final int end;
        private long sum;

        public PrimeSumThread(int start, int end) {
            this.start = start;
            this.end = end;
            this.sum = 0;
        }

        @Override
        public void run() {
            for (int number = start; number <= end; number += THREAD_COUNT) {
                if (isPrime(number)) {
                    sum += number;
                }
            }
        }

        public long getSum() {
            return sum;
        }
    }

    private static class MultipleSumThread implements Runnable {
        public static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
        private final int start;
        private final int end;
        private long sum;

        public MultipleSumThread(int start, int end) {
            this.start = start;
            this.end = end;
            this.sum = 0;
        }

        @Override
        public void run() {
            for (int number = start; number <= end; number += THREAD_COUNT) {
                if (isMultipleOfThreeOrFiveOrSeven(number)) {
                    sum += number;
                }
            }
        }

        public long getSum() {
            return sum;
        }
    }

    private static boolean isPrime(int number) {
        if (number < 2) {
            return false;
        }

        for (int divisor = 2; divisor * divisor <= number; divisor++) {
            if (number % divisor == 0) {
                return false;
            }
        }

        return true;
    }

    private static boolean isMultipleOfThreeOrFiveOrSeven(int number) {
        return number % 3 == 0 || number % 5 == 0 || number % 7 == 0;
    }
}