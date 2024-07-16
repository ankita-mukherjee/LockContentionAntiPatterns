import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class SlowArrayList extends ArrayList<String> {
    @Override
    public boolean add(String e) {
        try {
            Thread.sleep(10); // Sleep for 10 milliseconds
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        return super.add(e);
    }

    @Override
    public void add(int index, String element) {
        try {
            Thread.sleep(10); // Sleep for 10 milliseconds
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        super.add(index, element);
    }

    @Override
    public String remove(int index) {
        try {
            Thread.sleep(10); // Sleep for 10 milliseconds
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        return super.remove(index);
    }
}

public class CoarseGrained_Ex3 {

    private final SlowArrayList fineFruits = new SlowArrayList();
    private final SlowArrayList fineVegetables = new SlowArrayList();

    public void addFineFruit(String fruit) {
        synchronized (fineFruits) {
            fineFruits.add(fruit);
        }
    }

    public void removeFineFruit(int index) {
        synchronized (fineFruits) {
            if (!fineFruits.isEmpty() && index < fineFruits.size()) {
                fineFruits.remove(index);
            }
        }
    }

    public void addFineVegetable(String vegetable) {
        synchronized (fineVegetables) {
            fineVegetables.add(vegetable);
        }
    }

    public void removeFineVegetable(int index) {
        synchronized (fineVegetables) {
            if (!fineVegetables.isEmpty() && index < fineVegetables.size()) {
                fineVegetables.remove(index);
            }
        }
    }

    public int getFineFruitsSize() {
        synchronized (fineFruits) {
            return fineFruits.size();
        }
    }

    public int getFineVegetablesSize() {
        synchronized (fineVegetables) {
            return fineVegetables.size();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final int NUM_THREADS = 200;
        final int OPERATIONS = 50;

        CoarseGrained_Ex3 example = new CoarseGrained_Ex3();
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(NUM_THREADS);
        AtomicInteger fruitIndex = new AtomicInteger(0);
        AtomicInteger vegetableIndex = new AtomicInteger(0);

        // Start threads to add fruits
        for (int i = 0; i < NUM_THREADS / 2; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await(); // Wait for the signal to start
                    for (int j = 0; j < OPERATIONS; j++) {
                        example.addFineFruit("Apple-" + fruitIndex.getAndIncrement());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // Start threads to add vegetables
        for (int i = 0; i < NUM_THREADS / 2; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await(); // Wait for the signal to start
                    for (int j = 0; j < OPERATIONS; j++) {
                        example.addFineVegetable("Carrot-" + vegetableIndex.getAndIncrement());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        long startTime = System.currentTimeMillis();

        // Signal all threads to start
        startLatch.countDown();

        // Wait for all threads to complete
        endLatch.await();

        long endTime = System.currentTimeMillis();

        // Shutdown executor
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // Print the results
        System.out.println("Time taken: " + (endTime - startTime) + " ms");
    }
}
