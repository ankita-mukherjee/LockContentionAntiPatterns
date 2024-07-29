package NestedLocking;

public class NestedLockExample {

    private final Object lock1 = new Object();
    private final Object lock2 = new Object();

    public void method1() {
        synchronized (lock1) {
            try {
                System.out.println("method1 acquired lock1");
                Thread.sleep(1000); // Simulate intensive task
                synchronized (lock2) {
                    System.out.println("method1 acquired lock2");
                    Thread.sleep(1000); // Simulate intensive task
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void method2() {
        synchronized (lock2) {
            try {
                System.out.println("method2 acquired lock2");
                Thread.sleep(1000); // Simulate intensive task
                synchronized (lock1) {
                    System.out.println("method2 acquired lock1");
                    Thread.sleep(1000); // Simulate intensive task
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        NestedLockExample example = new NestedLockExample();

        Thread thread1 = new Thread(() -> {
            example.method1();
            System.out.println("method1 completed");
        });

        Thread thread2 = new Thread(() -> {
            example.method2();
            System.out.println("method2 completed");
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join(5000); // Wait for 5 seconds for thread1 to complete
            thread2.join(5000); // Wait for 5 seconds for thread2 to complete
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (thread1.isAlive() || thread2.isAlive()) {
            System.out.println("Deadlock detected!");
        } else {
            System.out.println("No deadlock detected.");
        }
    }
}
