public class SameLock_Self {
    public static Object lock = new Object();

    public void task1() {
        synchronized (lock) {
            // System.out.println(Thread.currentThread().getName() + " acquired the lock in
            // task1");
            try {
                Thread.sleep(1000); // Simulating intensive task with sleep
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // System.out.println(Thread.currentThread().getName() + " released the lock in
            // task1");
        }
    }

    public void task2() {
        synchronized (lock) {
            // System.out.println(Thread.currentThread().getName() + " acquired the lock in
            // task2");
            try {
                Thread.sleep(1000); // Simulating intensive task with sleep
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // System.out.println(Thread.currentThread().getName() + " released the lock in
            // task2");
        }
    }

    public static void main(String[] args) {
        SameLock_Self samelock = new SameLock_Self();
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
