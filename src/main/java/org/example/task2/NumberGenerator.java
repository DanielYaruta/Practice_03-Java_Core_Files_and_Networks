package org.example.task2;

import java.util.concurrent.ThreadLocalRandom;

public class NumberGenerator {

    public static int generateNumber(int min, int max) {
        // ThreadLocalRandom avoids lock contention when called from multiple threads.
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static int[] generateArray(int count, int min, int max) {
        int[] numbers = new int[count];
        for (int i = 0; i < count; i++) {
            numbers[i] = generateNumber(min, max);
        }
        return numbers;
    }
}
