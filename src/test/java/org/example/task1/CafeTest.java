package org.example.task1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class CafeTest {

    // Spins (5ms steps) until the expected number of threads are blocked in wait().
    // Fails fast after 1 second — much more reliable than Thread.sleep(50).
    private static void awaitWaiting(Cafe cafe, int expected) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 1000;
        while (cafe.getWaitingCount() < expected) {
            assertTrue(System.currentTimeMillis() < deadline,
                "Timed out waiting for " + expected + " thread(s) to enter wait()");
            Thread.sleep(5);
        }
    }

    // ── single-threaded: basic contract ─────────────────────────────────────

    @Test
    void takeDish_afterAddDish_returnsThatDish() throws InterruptedException {
        Cafe cafe = new Cafe();
        cafe.addDish("Бургер");
        assertEquals("Бургер", cafe.takeDish());
    }

    @Test
    void takeDish_preservesFIFOOrder() throws InterruptedException {
        Cafe cafe = new Cafe();
        cafe.addDish("Суп");
        cafe.addDish("Борщ");
        assertEquals("Суп",  cafe.takeDish());
        assertEquals("Борщ", cafe.takeDish());
    }

    @Test
    void takeDish_closedAndEmpty_returnsNull() throws InterruptedException {
        Cafe cafe = new Cafe();
        cafe.close();
        assertNull(cafe.takeDish());
    }

    @Test
    void takeDish_dishPresentThenClose_returnsDish() throws InterruptedException {
        Cafe cafe = new Cafe();
        cafe.addDish("Паста");
        cafe.close();
        assertEquals("Паста", cafe.takeDish());
    }

    // ── two-threaded: wait / notify ──────────────────────────────────────────

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void takeDish_blocksUntilDishAdded() throws InterruptedException {
        Cafe cafe = new Cafe();
        AtomicReference<String> result = new AtomicReference<>();

        Thread visitor = new Thread(() -> {
            try { result.set(cafe.takeDish()); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "Visitor-1");
        visitor.start();

        awaitWaiting(cafe, 1); // reliable: visitor is definitely in wait()

        cafe.addDish("Бургер");
        visitor.join(1000);

        assertFalse(visitor.isAlive(), "Visitor should have terminated");
        assertEquals("Бургер", result.get());
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void takeDish_blocksUntilCafeClosedThenReturnsNull() throws InterruptedException {
        Cafe cafe = new Cafe();
        AtomicReference<String> result = new AtomicReference<>("sentinel");

        Thread visitor = new Thread(() -> {
            try { result.set(cafe.takeDish()); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "Visitor-1");
        visitor.start();

        awaitWaiting(cafe, 1);

        cafe.close();
        visitor.join(1000);

        assertFalse(visitor.isAlive(), "Visitor should have terminated after close");
        assertNull(result.get());
    }

    // ── multiple threads: correctness ────────────────────────────────────────

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void addDish_oneDish_exactlyOneVisitorGetsIt() throws InterruptedException {
        Cafe cafe = new Cafe();
        AtomicInteger received = new AtomicInteger();

        List<Thread> visitors = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Thread t = new Thread(() -> {
                try {
                    if (cafe.takeDish() != null) received.incrementAndGet();
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }, "Visitor-" + i);
            visitors.add(t);
            t.start();
        }

        awaitWaiting(cafe, 3); // all three are in wait()

        cafe.addDish("Бургер");
        awaitWaiting(cafe, 2); // one woke up and took the dish; two are back in wait()
        cafe.close();

        for (Thread v : visitors) v.join(1000);

        assertEquals(1, received.get());
    }

    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    void addDish_nDishes_nVisitorsEachGetExactlyOne() throws InterruptedException {
        Cafe cafe = new Cafe();
        int n = 5;
        AtomicInteger received = new AtomicInteger();

        List<Thread> visitors = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            Thread t = new Thread(() -> {
                try {
                    if (cafe.takeDish() != null) received.incrementAndGet();
                } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }, "Visitor-" + i);
            visitors.add(t);
            t.start();
        }

        awaitWaiting(cafe, n);

        for (int i = 1; i <= n; i++) cafe.addDish("Блюдо-" + i);
        cafe.close();

        for (Thread v : visitors) v.join(1000);

        assertEquals(n, received.get());
    }
}
