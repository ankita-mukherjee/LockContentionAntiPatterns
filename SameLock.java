import java.util.concurrent.atomic.*;

public class SameLock {
    private class SameLockSynchronizedAtomicInteger {
        public static AtomicInteger lock = new AtomicInteger(0);

        public void task1() {
            synchronized (lock) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void task2() {
            synchronized (lock) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class SameLockSynchronizedObject {
        public static Object lock = new Object();

        public void task1() {
            synchronized (lock) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void task2() {
            synchronized (lock) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class SameLockAtomicInteger {
        public static AtomicInteger atomicInteger = new AtomicInteger(0);

        public void task1() {
            while (!atomicInteger.compareAndSet(0, 1)) {
                // Busy wait until lock is acquired.
            }

            try {
                Thread.sleep(10); // intensive task.
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                atomicInteger.set(0); // Release.
            }
        }

        public void task2() {
            while (!atomicInteger.compareAndSet(0, 1)) {
                // Busy wait until lock is acquired.
            }

            try {
                Thread.sleep(10); // intensive task.
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                atomicInteger.set(0); // Release.
            }
        }
    }

    private SameLockSynchronizedObject sameLockSynchronizedObject;
    private SameLockSynchronizedAtomicInteger sameLockSynchronizedAtomicInteger;
    private SameLockAtomicInteger sameLockAtomicInteger;

    public static void main(String[] args) {
        SameLock sameLock = new SameLock();
        sameLock.sameLockSynchronizedObject = sameLock.new SameLockSynchronizedObject();
        sameLock.sameLockSynchronizedAtomicInteger = sameLock.new SameLockSynchronizedAtomicInteger();
        sameLock.sameLockAtomicInteger = sameLock.new SameLockAtomicInteger();

        int numChecks = 3;

        Object[] objects = new Object[numChecks];
        objects[0] = sameLock.sameLockSynchronizedObject;
        objects[1] = sameLock.sameLockSynchronizedAtomicInteger;
        objects[2] = sameLock.sameLockAtomicInteger;

        long[] durations = new long[numChecks];

        for (int j = 0; j < numChecks; ++j) {
            Object object = objects[j];

            Runnable task1;
            Runnable task2;

            if (object instanceof SameLockSynchronizedObject) {
                SameLockSynchronizedObject sameLockSynchronizedObject = (SameLockSynchronizedObject) object;
                task1 = sameLockSynchronizedObject::task1;
                task2 = sameLockSynchronizedObject::task2;
            } else if (object instanceof SameLockSynchronizedAtomicInteger) {
                SameLockSynchronizedAtomicInteger sameLockSynchronizedAtomicInteger = (SameLockSynchronizedAtomicInteger) object;
                task1 = sameLockSynchronizedAtomicInteger::task1;
                task2 = sameLockSynchronizedAtomicInteger::task2;
            } else {
                SameLockAtomicInteger sameLockAtomicInteger = (SameLockAtomicInteger) object;
                task1 = sameLockAtomicInteger::task1;
                task2 = sameLockAtomicInteger::task2;
            }

            Thread[] threadSet = new Thread[50];

            long startTime = System.nanoTime();

            for (int i = 0; i < 50; ++i) {
                if (i % 2 == 0) {
                    threadSet[i] = new Thread(task1);
                } else {
                    threadSet[i] = new Thread(task2);
                }
                threadSet[i].start();
            }

            try {
                for (Thread thread : threadSet)
                    thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long endTime = System.nanoTime();

            durations[j] = (endTime - startTime) / 1_000_000;
        }

        System.out.println(
                "Time taken by for SynchronizedObject task1 & task2 by 50 threads is "
                        + durations[0] + " milliseconds.");
        System.out.println(
                "Time taken by for SynchronizedAtomicInteger task1 & task2 by 50 threads is "
                        + durations[1] + " milliseconds.");
        System.out.println(
                "Time taken by for AtomicInteger task1 & task2 by 50 threads is "
                        + durations[2] + " milliseconds.");
    }
}
