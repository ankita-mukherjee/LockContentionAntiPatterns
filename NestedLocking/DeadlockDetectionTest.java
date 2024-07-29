package NestedLocking;

public class DeadlockDetectionTest {

    public static void main(String[] args) {
        final BankAccount account1 = new BankAccount(1000, "Account1");
        final BankAccount account2 = new BankAccount(1000, "Account2");

        // Thread 1: Transfer from account1 to account2
        Thread thread1 = new Thread(new Runnable() {
            public void run() {
                account1.transfer(account2, 100);
            }
        });

        // Thread 2: Transfer from account2 to account1
        Thread thread2 = new Thread(new Runnable() {
            public void run() {
                account2.transfer(account1, 200);
            }
        });

        // Start both threads
        thread1.start();
        thread2.start();

        try {
            // Wait for both threads to complete
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Transfer completed");
    }
}

class BankAccount {
    private double balanceAmount;
    private String name;

    BankAccount(double balance, String name) {
        this.balanceAmount = balance;
        this.name = name;
    }

    public void transfer(BankAccount target, double amount) {
        synchronized (this) {
            System.out.println(Thread.currentThread().getName() + " acquired lock on " +
                    this.name);
            try {
                Thread.sleep(100); // Delay to increase chance of deadlock
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (target) {
                System.out.println(Thread.currentThread().getName() + " acquired lock on " + target.name);
                if (amount > balanceAmount) {
                    throw new IllegalArgumentException("Transfer cannot be completed");
                }
                target.balanceAmount += amount;
                this.balanceAmount -= amount;
            }
        }
        System.out.println(Thread.currentThread().getName() + " completed transfer");
    }
}