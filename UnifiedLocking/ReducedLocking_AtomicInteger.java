package UnifiedLocking;

import java.util.concurrent.atomic.AtomicInteger;

public class ReducedLocking_AtomicInteger {
    private final AtomicInteger counter1 = new AtomicInteger(0);
    private final AtomicInteger counter2 = new AtomicInteger(0);

    public void incrementCounters() {
        counter1.incrementAndGet();
        counter2.incrementAndGet();
    }

    public int getCounter1() {
        return counter1.get();
    }

    public int getCounter2() {
        return counter2.get();
    }

    public static void main(String[] args) {
        final int numThreads = 100;
        final int incrementsPerThread = 10;
        ReducedLocking_AtomicInteger example = new ReducedLocking_AtomicInteger();

        Thread[] threads = new Thread[numThreads];

        long startTime = System.nanoTime();

        // Starting threads to increment counters
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    example.incrementCounters();
                }
            });
            threads[i].start();
        }

        // Joining threads
        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.nanoTime();

        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        System.out.println("Total time taken: " + duration + " ms");
        System.out.println("Counter1: " + example.getCounter1());
        System.out.println("Counter2: " + example.getCounter2());
    }
}
