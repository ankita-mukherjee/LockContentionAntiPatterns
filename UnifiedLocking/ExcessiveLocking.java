package UnifiedLocking;

public class ExcessiveLocking {
    private final Object lock = new Object();
    private int counter1 = 0;
    private int counter2 = 0;

    public void incrementCounters() {
        synchronized (lock) {
            // intensive task emulated by sleep
            try {
                Thread.sleep(100); // Simulating intensive task
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            counter1++;
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
        ExcessiveLocking example = new ExcessiveLocking();

        Thread[] threads = new Thread[numThreads];

        long startTime = System.nanoTime();

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    example.incrementCounters();
                }
            });
            threads[i].start();
        }

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
