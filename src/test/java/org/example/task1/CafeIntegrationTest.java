package org.example.task1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
class CafeIntegrationTest {

    private static final int COOK_TIME_MS = 20;
    private static final int VISITOR_COUNT = 3;
    private static final int DISH_COUNT = 10;

    @Test
    void fullSimulation_allDishesConsumed() throws InterruptedException {
        Cafe cafe = new Cafe();
        AtomicInteger totalEaten = new AtomicInteger();

        ExecutorService executor = Executors.newFixedThreadPool(VISITOR_COUNT + 1);

        for (int i = 1; i <= VISITOR_COUNT; i++) {
            executor.submit(() -> {
                try {
                    while (true) {
                        String dish = cafe.takeDish();
                        if (dish == null) break;
                        totalEaten.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        executor.submit(new Chef(cafe, COOK_TIME_MS));

        executor.shutdown();
        assertTrue(executor.awaitTermination(9, TimeUnit.SECONDS),
            "Executor should finish within timeout");

        assertEquals(DISH_COUNT, totalEaten.get(),
            "All 10 dishes must be consumed — no dish lost, no dish double-counted");
    }

    @Test
    void fullSimulation_noVisitorHangsAfterChefFinishes() throws InterruptedException {
        Cafe cafe = new Cafe();

        Thread[] visitors = new Thread[VISITOR_COUNT];
        for (int i = 0; i < VISITOR_COUNT; i++) {
            visitors[i] = new Thread(new Visitor(i + 1, cafe), "Visitor-" + (i + 1));
            visitors[i].start();
        }
        Thread chef = new Thread(new Chef(cafe, COOK_TIME_MS));
        chef.start();
        chef.join(); // @Timeout(10) on the class aborts if chef deadlocks

        for (Thread v : visitors) {
            v.join(1000);
            assertFalse(v.isAlive(), v.getName() + " should not hang after chef closes cafe");
        }
    }
}
