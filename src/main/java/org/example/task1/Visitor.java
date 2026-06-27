package org.example.task1;

public class Visitor implements Runnable {

    private final int id;
    private final Cafe cafe;

    public Visitor(int id, Cafe cafe) {
        this.id = id;
        this.cafe = cafe;
    }

    @Override
    public void run() {
        System.out.println("Посетитель " + id + " зашел в кафе");
        try {
            while (true) {
                String dish = cafe.takeDish();
                if (dish == null) {
                    break;
                }
                System.out.println("Посетитель " + id + " съел " + dish);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Посетитель " + id + " ушел из кафе");
    }
}
