package OverlySplit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LockCoarsening {
    private static final List<Integer> buffer = new ArrayList<>();
    private static int foo;
    private static int bar = 42;

    public static void main(String[] args) {
        // Create a thread pool with 10 threads
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // Record the start time
        long startTime = System.currentTimeMillis();

        // Submit 50 tasks to the executor service
        for (int i = 0; i < 50; i++) {
            int x = i;
            int y = i + 100;

            executorService.submit(() -> addToBuffer(x, y));
        }

        // Shutdown the executor service and wait for tasks to complete
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt(); // Preserve interrupt status
        }

        // Record the end time
        long endTime = System.currentTimeMillis();

        // Calculate and print the total duration
        long duration = endTime - startTime;
        System.out.println("Total duration for 50 threads: " + duration + " milliseconds");
    }

    private static void addToBuffer(int x, int y) {
        try {
            synchronized (buffer) {
                buffer.add(x);
                foo = bar;
                buffer.add(y);
                if (!buffer.isEmpty()) {
                    buffer.remove(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
