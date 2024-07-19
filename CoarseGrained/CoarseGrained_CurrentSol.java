import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class CoarseGrained_CurrentSol {

    // Coarse-Grained Locking solu from FautLLama
    private final ArrayList<String> solFruits = new ArrayList<>();
    private final ArrayList<String> solVegetables = new ArrayList<>();

    public synchronized void addSolFruit(int index, String fruit) {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        try {
            solFruits.add(index, fruit);
        } finally {
            lock.unlock();
        }
    }

    public synchronized void removeSolFruit(int index) {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        try {
            solFruits.remove(index);
        } finally {
            lock.unlock();
        }
    }

    public synchronized void addSolVegetable(int index, String vegetable) {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        try {
            solVegetables.add(index, vegetable);
        } finally {
            lock.unlock();
        }
    }

    public synchronized void removeSolVegetable(int index) {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        try {
            solVegetables.remove(index);
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        final int NUM_THREADS = 2000;
        final int OPERATIONS = 100;

        CoarseGrained_CurrentSol example = new CoarseGrained_CurrentSol();
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        CountDownLatch latch = new CountDownLatch(1);

        // Start threads to add fruits
        for (int i = 0; i < NUM_THREADS / 2; i++) {
            executor.submit(() -> {
                try {
                    latch.await(); // Wait for the signal to start
                    for (int j = 0; j < OPERATIONS; j++) {
                        example.addSolFruit(j % (OPERATIONS / 2), "Apple");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // Start threads to add vegetables
        for (int i = 0; i < NUM_THREADS / 2; i++) {
            executor.submit(() -> {
                try {
                    latch.await(); // Wait for the signal to start
                    for (int j = 0; j < OPERATIONS; j++) {
                        example.addSolVegetable(j % (OPERATIONS / 2), "Carrot");
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        long startTime = System.currentTimeMillis();

        // Signal all threads to start
        latch.countDown();

        // Shutdown executor and wait for all tasks to complete
        executor.shutdown();
        while (!executor.isTerminated()) {
            // Busy-waiting for all tasks to complete
        }

        long endTime = System.currentTimeMillis();

        // Print the results
        System.out.println("Time taken: " + (endTime - startTime) + " ms");

    }
}
