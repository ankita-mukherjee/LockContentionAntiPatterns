package UnifiedLocking;

public class ReducedLocking {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();
    private int counter1 = 0;
    private int counter2 = 0;

    public void incrementCounters1() {
        synchronized (lock1) {
            counter1++;
        }
    }

    public void incrementCounters2() {
        synchronized (lock2) {
            counter2++;
        }
    }

    public int getCounter1() {
        return counter1;
    }

    public int getCounter2() {
        return counter2;
    }

    public static void main(String[] args) {
        final int numThreads = 100;
        final int incrementsPerThread = 10;
        ReducedLocking example = new ReducedLocking();

        Thread[] threads1 = new Thread[numThreads];
        Thread[] threads2 = new Thread[numThreads];

        long startTime = System.nanoTime();

        // Starting threads to increment counter1
        for (int i = 0; i < numThreads; i++) {
            threads1[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    example.incrementCounters1();
                }
            });
            threads1[i].start();
        }

        // Starting threads to increment counter2
        for (int i = 0; i < numThreads; i++) {
            threads2[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    example.incrementCounters2();
                }
            });
            threads2[i].start();
        }

        // Joining threads
        for (int i = 0; i < numThreads; i++) {
            try {
                threads1[i].join();
                threads2[i].join();
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
