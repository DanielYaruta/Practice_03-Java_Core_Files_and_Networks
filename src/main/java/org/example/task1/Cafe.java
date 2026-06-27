package org.example.task1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Cafe {

    private final List<String> orders = new ArrayList<>();
    private boolean isDone = false;

    // Tracks threads currently blocked inside wait() — used by tests to avoid Thread.sleep.
    private final AtomicInteger waitingCount = new AtomicInteger();

    public void addDish(String dish) {
        synchronized (this) {
            orders.add(dish);
            notifyAll();
        }
        // Print outside the synchronized block so I/O doesn't hold the lock.
        System.out.println("Повар приготовил " + dish);
    }

    public synchronized void close() {
        isDone = true;
        notifyAll();
    }

    // Returns dish name, or null if chef is done and no food remains.
    // Uses Thread.currentThread().getName() so Cafe doesn't need to know the visitor id.
    public synchronized String takeDish() throws InterruptedException {
        while (orders.isEmpty() && !isDone) {
            System.out.println("Еды нет, " + Thread.currentThread().getName() + " ждет");
            waitingCount.incrementAndGet();
            wait();
            waitingCount.decrementAndGet();
        }
        if (!orders.isEmpty()) {
            return orders.remove(0);
        }
        return null;
    }

    // Package-private: lets tests spin-wait until N threads are in wait()
    // instead of relying on Thread.sleep().
    int getWaitingCount() {
        return waitingCount.get();
    }
}
