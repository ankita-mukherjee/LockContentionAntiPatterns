import java.util.concurrent.atomic.AtomicInteger;

public class SameLock_Paper {

    public static AtomicInteger lock = new AtomicInteger(0);

    public void task1() {
        synchronized (lock) {
            try {
                Thread.sleep(1000); // Simulating intensive task with sleep
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void task2() {
        synchronized (lock) {
            try {
                Thread.sleep(1000); // Simulating intensive task with sleep
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SameLock_Paper samelock = new SameLock_Paper();
        Runnable runTask1 = samelock::task1;
        Runnable runTask2 = samelock::task2;

        Thread[] threads = new Thread[100];

        // Create 50 threads for task1 and 50 threads for task2
        for (int i = 0; i < 50; i++) {
            threads[i] = new Thread(runTask1, "Thread-task1-" + i);
        }
        for (int i = 50; i < 100; i++) {
            threads[i] = new Thread(runTask2, "Thread-task2-" + i);
        }

        long startTime = System.currentTimeMillis();

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("Total duration: " + duration + " milliseconds");
    }
}
