package org.example.task2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class NumberCheckerTest {

    // Helper: run checker synchronously on the calling thread.
    private static int count(int[] numbers, int min, int max) {
        AtomicInteger counter = new AtomicInteger();
        new NumberChecker(numbers, min, max, counter).run();
        return counter.get();
    }

    // ── counting logic ───────────────────────────────────────────────────────

    @Test
    void counter_matchingNumbers_incrementedCorrectly() {
        int[] numbers = {10, 50, 99, 100, 500};
        assertEquals(3, count(numbers, 10, 99));
    }

    @Test
    void counter_noMatchingNumbers_remainsZero() {
        int[] numbers = {100, 500, 999};
        assertEquals(0, count(numbers, 10, 99));
    }

    @Test
    void counter_emptyArray_remainsZero() {
        assertEquals(0, count(new int[0], 10, 99));
    }

    // ── each category independently ──────────────────────────────────────────

    @Test
    void twoDigit_countsOnlyTwoDigitNumbers() {
        int[] numbers = {9, 10, 55, 99, 100, 9999};
        assertEquals(3, count(numbers, 10, 99));
    }

    @Test
    void threeDigit_countsOnlyThreeDigitNumbers() {
        int[] numbers = {99, 100, 500, 999, 1000};
        assertEquals(3, count(numbers, 100, 999));
    }

    @Test
    void fourDigit_countsOnlyFourDigitNumbers() {
        int[] numbers = {999, 1000, 5000, 9999, 10000};
        assertEquals(3, count(numbers, 1000, 9999));
    }

    // ── boundary values: parameterized across all three categories ───────────

    @ParameterizedTest(name = "диапазон [{0}, {1}]: границы включительно")
    @CsvSource({
        "10,   99,    9,  100",
        "100,  999,  99, 1000",
        "1000, 9999, 999, 10000"
    })
    void boundaries_lowerIncluded_upperIncluded_outsideExcluded(
            int min, int max, int belowMin, int aboveMax) {
        assertEquals(1, count(new int[]{min},      min, max), "lower boundary must be counted");
        assertEquals(1, count(new int[]{max},      min, max), "upper boundary must be counted");
        assertEquals(0, count(new int[]{belowMin}, min, max), "one below lower must not be counted");
        assertEquals(0, count(new int[]{aboveMax}, min, max), "one above upper must not be counted");
    }

    // ── threaded: AtomicInteger is safe under concurrent access ─────────────

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void run_viaThread_updatesCounterCorrectly() throws InterruptedException {
        int[] numbers = {10, 50, 99};
        AtomicInteger counter = new AtomicInteger();

        Thread t = new Thread(new NumberChecker(numbers, 10, 99, counter));
        t.start();
        t.join(1000);

        assertFalse(t.isAlive());
        assertEquals(3, counter.get());
    }
}
