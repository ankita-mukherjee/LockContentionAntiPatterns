package NestedLocking;

import java.util.concurrent.atomic.AtomicLong;

public class CompliantSolutionOrdered {

    public static void main(String[] args) {
        final BankAccount account1 = new BankAccount(1000, "Account1");
        final BankAccount account2 = new BankAccount(1000, "Account2");

        // Thread 1: Transfer from account1 to account2
        Thread thread1 = new Thread(() -> account1.initiateTransfer(account2, 100), "Thread1");

        // Thread 2: Transfer from account2 to account1
        Thread thread2 = new Thread(() -> account2.initiateTransfer(account1, 200), "Thread2");

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

final class BankAccount implements Comparable<BankAccount> {
    private double balanceAmount;
    private final Object lock = new Object();
    private final long id; // Unique for each BankAccount
    private final String name;
    private static final AtomicLong nextID = new AtomicLong(0); // Next unused ID

    BankAccount(double balance, String name) {
        this.balanceAmount = balance;
        this.name = name;
        this.id = nextID.getAndIncrement();
    }

    @Override
    public int compareTo(BankAccount ba) {
        return Long.compare(this.id, ba.id);
    }

    // Deposits the amount from this account to the given account
    public void depositAmount(BankAccount target, double amount) {
        BankAccount firstLock = this;
        BankAccount secondLock = target;

        // Ensure consistent lock ordering
        if (firstLock.compareTo(secondLock) > 0) {
            firstLock = target;
            secondLock = this;
        }

        synchronized (firstLock.lock) {
            System.out.println(Thread.currentThread().getName() + " acquired lock on " + firstLock.name);
            try {
                Thread.sleep(100); // Delay to increase chance of deadlock
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (secondLock.lock) {
                System.out.println(Thread.currentThread().getName() + " acquired lock on " + secondLock.name);
                if (amount > this.balanceAmount) {
                    throw new IllegalArgumentException("Transfer cannot be completed");
                }
                target.balanceAmount += amount;
                this.balanceAmount -= amount;
                System.out.println(Thread.currentThread().getName() + " completed transfer of " + amount +
                        " from " + this.name + " to " + target.name);
            }
        }
    }

    public void initiateTransfer(BankAccount target, double amount) {
        new Thread(() -> {
            try {
                depositAmount(target, amount);
            } catch (IllegalArgumentException e) {
                System.out.println(Thread.currentThread().getName() + " failed to transfer: " + e.getMessage());
            }
        }, Thread.currentThread().getName()).start();
    }

    @Override
    public String toString() {
        return "BankAccount{id=" + id + ", name='" + name + "', balanceAmount=" + balanceAmount + "}";
    }
}
