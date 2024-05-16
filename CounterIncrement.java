import java.util.concurrent.atomic.AtomicInteger;

public class CounterIncrement {
    private int count = 0;
    private Object lock = new Object();

    // entire method is synchronized.
    public synchronized void increment1() {
        try {
            Thread.sleep(100); // intensive task
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ++count;
    }

    // intensive task inside lock.
    public void increment2() {
        synchronized (lock) {
            try {
                Thread.sleep(100); // intensive task
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ++count;
        }
    }

    // using lock only on count increment.
    public void increment3() {
        try {
            Thread.sleep(100); // intensive task
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (lock) {
            ++count;
        }
    }

    private final AtomicInteger atomicCount = new AtomicInteger(0);

    // change data type from int to AtomicInteger/AtomicLong
    public void increment4() {
        try {
            Thread.sleep(100); // intensive task
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        atomicCount.incrementAndGet();
    }

    public static void main(String[] args) {
        CounterIncrement counterIncrement = new CounterIncrement();
        Runnable[] functions = new Runnable[4];
        functions[0] = counterIncrement::increment1;
        functions[1] = counterIncrement::increment2;
        functions[2] = counterIncrement::increment3;
        functions[3] = counterIncrement::increment4;

        long[] durations = new long[4];
        for (int j = 0; j < 4; ++j) {
            Thread[] threadSet = new Thread[50];
            long startTime = System.nanoTime();
            for (int i = 0; i < 50; ++i) {
                threadSet[i] = new Thread(functions[j]);
                threadSet[i].start();
            }
            try {
                for (int i = 0; i < 50; ++i)
                    threadSet[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long endTime = System.nanoTime();
            durations[j] = (endTime - startTime) / 1_000_000;
        }

        for (int j = 0; j < 4; ++j) {
            System.out.println(
                    "Time taken by 50 threads for increment" + (j + 1) + " is " + durations[j] + " milliseconds.");
        }
    }
}