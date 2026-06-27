package org.example.task2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 5, unit = TimeUnit.SECONDS)
class NumberIntegrationTest {

    private static final int[] NUMBERS = NumberGenerator.generateArray(100_000, 10, 9999);

    private record Counts(int two, int three, int four) {
        int total() { return two + three + four; }
    }

    private static Counts runThreeThreads(int[] numbers) throws InterruptedException {
        AtomicInteger twoDigit   = new AtomicInteger();
        AtomicInteger threeDigit = new AtomicInteger();
        AtomicInteger fourDigit  = new AtomicInteger();

        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.invokeAll(List.of(
            Executors.callable(new NumberChecker(numbers, 10,   99,   twoDigit)),
            Executors.callable(new NumberChecker(numbers, 100,  999,  threeDigit)),
            Executors.callable(new NumberChecker(numbers, 1000, 9999, fourDigit))
        ));
        executor.shutdown();

        return new Counts(twoDigit.get(), threeDigit.get(), fourDigit.get());
    }

    @Test
    void threeThreads_sumEqualsArraySize() throws InterruptedException {
        Counts counts = runThreeThreads(NUMBERS);
        assertEquals(NUMBERS.length, counts.total(),
            "Every number must fall into exactly one category — no gaps, no overlaps");
    }

    @Test
    void threeThreads_countsArePositive() throws InterruptedException {
        Counts counts = runThreeThreads(NUMBERS);
        assertTrue(counts.two()   > 0, "Two-digit count should be > 0");
        assertTrue(counts.three() > 0, "Three-digit count should be > 0");
        assertTrue(counts.four()  > 0, "Four-digit count should be > 0");
    }

}
