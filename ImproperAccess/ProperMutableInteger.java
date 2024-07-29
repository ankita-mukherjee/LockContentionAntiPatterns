package ImproperAccess;

public class ProperMutableInteger {
    private int value;

    public synchronized int get() {
        return value;
    }

    public synchronized void set(int value) {
        this.value = value;
    }

    public static void main(String[] args) {
        ProperMutableInteger mutableInteger = new ProperMutableInteger();

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                synchronized (mutableInteger) {
                    int currentValue = mutableInteger.get();
                    mutableInteger.set(currentValue + 1);
                }
            }
        };

        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Final value: " + mutableInteger.get());
    }
}
