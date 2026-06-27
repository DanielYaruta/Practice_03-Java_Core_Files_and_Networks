package org.example.task1;

public class Chef implements Runnable {

    private static final String[] MENU = {
        "Бургер", "Суп", "Борщ", "Пицца", "Паста",
        "Салат", "Роллы", "Стейк", "Омлет", "Блины"
    };
    private static final int DEFAULT_COOK_TIME_MS = 3000;

    private final Cafe cafe;
    private final int cookTimeMs;

    public Chef(Cafe cafe) {
        this(cafe, DEFAULT_COOK_TIME_MS);
    }

    public Chef(Cafe cafe, int cookTimeMs) {
        this.cafe = cafe;
        this.cookTimeMs = cookTimeMs;
    }

    @Override
    public void run() {
        System.out.println("Повар начал работу");
        for (String dish : MENU) {
            try {
                Thread.sleep(cookTimeMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            cafe.addDish(dish);
        }
        cafe.close();
        System.out.println("Повар закончил работу и закрыл кафе");
    }
}
