package org.example.task1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CafeMain {

    private static final int VISITOR_COUNT = 3;

    public static void main(String[] args) throws InterruptedException {
        Cafe cafe = new Cafe();

        ExecutorService executor = Executors.newFixedThreadPool(VISITOR_COUNT + 1);

        executor.submit(new Chef(cafe));
        for (int i = 1; i <= VISITOR_COUNT; i++) {
            executor.submit(new Visitor(i, cafe));
        }

        executor.shutdown();
        if (!executor.awaitTermination(2, TimeUnit.MINUTES)) {
            executor.shutdownNow();
        }

        System.out.println("Кафе закрыто. Все посетители обслужены.");
    }
}
