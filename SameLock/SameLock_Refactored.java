import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SameLock_Refactored {
    private final AtomicInteger counter = new AtomicInteger(0);

    public void task1() {
        while (!counter.compareAndSet(0, 1)) {
            // Busy-wait until the counter is 0 and we can set it to 1
        }
        try {
            Thread.sleep(1000); // Simulating intensive task with sleep
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            counter.set(0); // Reset the counter to 0
        }
    }

    public void task2() {
        while (!counter.compareAndSet(0, 1)) {
            // Busy-wait until the counter is 0 and we can set it to -1
        }
        try {
            Thread.sleep(1000); // Simulating intensive task with sleep
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            counter.set(0); // Reset the counter to 0
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SameLock_Refactored samelock = new SameLock_Refactored();
        Runnable runTask1 = samelock::task1;
        Runnable runTask2 = samelock::task2;

        ExecutorService executor = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(100);

        // Create 50 tasks for task1 and 50 tasks for task2
        for (int i = 0; i < 50; i++) {
            executor.submit(() -> {
                try {
                    runTask1.run();
                } finally {
                    latch.countDown();
                }
            });
        }
        for (int i = 50; i < 100; i++) {
            executor.submit(() -> {
                try {
                    runTask2.run();
                } finally {
                    latch.countDown();
                }
            });
        }

        long startTime = System.currentTimeMillis();

        // Wait for all tasks to complete
        latch.await();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        executor.shutdown();

        System.out.println("Total duration: " + duration + " milliseconds");
    }
}
