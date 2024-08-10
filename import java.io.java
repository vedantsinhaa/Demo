import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MatrixCalculator {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Filename argument missing.");
            return;
        }

        String filepath = args[0];
        try (Scanner fileScanner = new Scanner(new File(filepath))) {
            int size = fileScanner.nextInt();
            long[][] matrixA = rm(fileScanner, size);
            long[][] matrixB = rm(fileScanner, size);
            long[][] matrixC = rm(fileScanner, size);

            long[][] intermediateProduct = MultithreadedMatrixMultiplier.multiply(matrixA, matrixB);
            long[][] finalProduct = MultithreadedMatrixMultiplier.multiply(intermediateProduct, matrixC);

            printMatrix(finalProduct, size);
        } catch (FileNotFoundException e) {
            System.out.println("Could not find the file: " + filepath);
        }
    }

    private static long[][] rm(Scanner scanner, int size) {
        long[][] matrix = new long[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = scanner.nextLong();
            }
        }
        return matrix;
    }

    private static void printMatrix(long[][] matrix, int size) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(matrix[i][j]);
                if (j < size - 1) {
                    System.out.print(" ");
                }
            }
            if (i < size - 1) {
                System.out.print(" ");
            }
        }
    }

    public static class MultithreadedMatrixMultiplier {

        public static long[][] multiply(long[][] matrixOne, long[][] matrixTwo) {
            int dimension = matrixOne.length;
            long[][] productMatrix = new long[dimension][dimension];

            ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    threadPool.execute(new MatrixMultiplicationTask(matrixOne, matrixTwo, productMatrix, i, j));
                }
            }
            threadPool.shutdown();

            try {
                threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return productMatrix;
        }

        private static class MatrixMultiplicationTask implements Runnable {
            private final long[][] matrixOne;
            private final long[][] matrixTwo;
            private final long[][] productMatrix;
            private final int rowIdx;
            private final int colIdx;

            MatrixMultiplicationTask(long[][] matrixOne, long[][] matrixTwo, long[][] productMatrix, int row, int col) {
                this.matrixOne = matrixOne;
                this.matrixTwo = matrixTwo;
                this.productMatrix = productMatrix;
                this.rowIdx = row;
                this.colIdx = col;
            }

            @Override
            public void run() {
                for (int k = 0; k < matrixOne.length; k++) {
                    productMatrix[rowIdx][colIdx] += matrixOne[rowIdx][k] * matrixTwo[k][colIdx];
                }
            }
        }
    }
}