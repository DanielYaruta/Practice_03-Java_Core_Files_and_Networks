package org.example.task2;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class NumberMain {

    private static final int COUNT = 100_000;
    private static final int MIN = 10;
    private static final int MAX = 9999;

    public static void main(String[] args) throws InterruptedException {
        int[] numbers = NumberGenerator.generateArray(COUNT, MIN, MAX);

        AtomicInteger twoDigit   = new AtomicInteger();
        AtomicInteger threeDigit = new AtomicInteger();
        AtomicInteger fourDigit  = new AtomicInteger();

        // invokeAll blocks until all tasks complete — no manual join needed.
        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.invokeAll(List.of(
            Executors.callable(new NumberChecker(numbers, 10,   99,   twoDigit)),
            Executors.callable(new NumberChecker(numbers, 100,  999,  threeDigit)),
            Executors.callable(new NumberChecker(numbers, 1000, 9999, fourDigit))
        ));
        executor.shutdown();

        System.out.println("Двузначных чисел:     " + twoDigit.get()   + " шт.");
        System.out.println("Трехзначных чисел:    " + threeDigit.get() + " шт.");
        System.out.println("Четырехзначных чисел: " + fourDigit.get()  + " шт.");
        System.out.println("Итого: " + (twoDigit.get() + threeDigit.get() + fourDigit.get()) + " шт.");
    }
}
