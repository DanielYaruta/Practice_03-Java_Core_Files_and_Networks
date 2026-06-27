package org.example.task3;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class UserMain {

    public static void main(String[] args) {
        UserManager manager = UserManager.load(Path.of("users.txt"));
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nВыберите действие:");
            System.out.println("1 - Добавить пользователя");
            System.out.println("2 - Показать список пользователей");
            System.out.println("3 - Выход");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addUser(scanner, manager);
                case "2" -> showUsers(manager);
                case "3" -> {
                    System.out.println("Выход из программы.");
                    return;
                }
                default -> System.out.println("Некорректный выбор. Введите 1, 2 или 3.");
            }
        }
    }

    private static void addUser(Scanner scanner, UserManager manager) {
        String name = readValidField(scanner, "Введите имя пользователя");
        String city = readValidField(scanner, "Введите город пользователя");
        try {
            manager.addUser(new User(name, city));
            System.out.println("Пользователь сохранён.");
        } catch (UserValidationException e) {
            System.out.println("Ошибка валидации: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Ошибка файла: " + e.getMessage());
        }
    }

    private static void showUsers(UserManager manager) {
        if (manager.getUsers().isEmpty()) {
            System.out.println("Список пуст.");
        } else {
            System.out.println("Список пользователей:");
            manager.getUsers().forEach(u ->
                System.out.println("Имя: " + u.getName() + ", Город: " + u.getCity()));
        }
    }

    private static String readValidField(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String value = scanner.nextLine().trim();
            try {
                UserValidator.validateField(prompt, value);
                return value;
            } catch (UserValidationException e) {
                System.out.println("Ошибка: " + e.getMessage() + ". Попробуйте снова.");
            }
        }
    }
}
