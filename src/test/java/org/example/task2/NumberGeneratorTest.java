package org.example.task2;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumberGeneratorTest {

    @RepeatedTest(20)
    void generateNumber_alwaysWithinRange() {
        int result = NumberGenerator.generateNumber(10, 9999);
        assertTrue(result >= 10 && result <= 9999,
            "Expected value in [10, 9999] but got: " + result);
    }

    @Test
    void generateNumber_minEqualsMax_returnsMin() {
        assertEquals(42, NumberGenerator.generateNumber(42, 42));
    }

    @Test
    void generateNumber_consecutiveRange_returnsBothBoundaries() {
        // Run enough times to hit both ends of a [1,2] range
        boolean sawOne = false, sawTwo = false;
        for (int i = 0; i < 200; i++) {
            int v = NumberGenerator.generateNumber(1, 2);
            if (v == 1) sawOne = true;
            if (v == 2) sawTwo = true;
            if (sawOne && sawTwo) break;
        }
        assertTrue(sawOne, "Lower boundary 1 should appear");
        assertTrue(sawTwo, "Upper boundary 2 should appear");
    }

    @Test
    void generateArray_hasCorrectSize() {
        int[] arr = NumberGenerator.generateArray(100, 10, 9999);
        assertEquals(100, arr.length);
    }

    @Test
    void generateArray_allElementsWithinRange() {
        int[] arr = NumberGenerator.generateArray(10_000, 10, 9999);
        for (int v : arr) {
            assertTrue(v >= 10 && v <= 9999,
                "Element out of range: " + v);
        }
    }

    @Test
    void generateArray_sizeZero_returnsEmptyArray() {
        assertEquals(0, NumberGenerator.generateArray(0, 10, 9999).length);
    }
}
