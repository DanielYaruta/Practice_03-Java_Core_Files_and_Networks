package org.example.task2;

import java.util.concurrent.atomic.AtomicInteger;

public class NumberChecker implements Runnable {

    private final int[] numbers;
    private final int min;
    private final int max;
    private final AtomicInteger counter;

    public NumberChecker(int[] numbers, int min, int max, AtomicInteger counter) {
        this.numbers = numbers;
        this.min = min;
        this.max = max;
        this.counter = counter;
    }

    @Override
    public void run() {
        for (int number : numbers) {
            if (number >= min && number <= max) {
                counter.incrementAndGet();
            }
        }
    }
}
