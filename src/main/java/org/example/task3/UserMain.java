package org.example.task3;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class UserMain {

    public static void main(String[] args) {
        Path filePath = Path.of("users.txt");
        UserManager manager = UserManager.load(filePath);

        System.out.println("=== Загруженные пользователи ===");
        if (manager.getUsers().isEmpty()) {
            System.out.println("Список пуст.");
        } else {
            manager.getUsers().forEach(System.out::println);
        }

        System.out.println("\n=== Добавление новых пользователей ===");
        System.out.println("Введите 'exit' в поле имени, чтобы выйти.\n");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String name = readValidField(scanner, "Имя");
            if (name == null) break;

            String city = readValidField(scanner, "Город");
            if (city == null) break;

            try {
                User user = new User(name, city);
                manager.addUser(user);
                System.out.println("Сохранено: " + user + "\n");
            } catch (UserValidationException e) {
                System.out.println("[Ошибка валидации] " + e.getMessage() + "\n");
            } catch (IOException e) {
                System.out.println("[Ошибка файла] " + e.getMessage() + "\n");
            }
        }

        System.out.println("\n=== Итоговый список ===");
        if (manager.getUsers().isEmpty()) {
            System.out.println("Список пуст.");
        } else {
            manager.getUsers().forEach(System.out::println);
        }
    }

    private static String readValidField(Scanner scanner, String label) {
        while (true) {
            System.out.print(label + ": ");
            String value = scanner.nextLine().trim();

            if (value.equalsIgnoreCase("exit")) {
                return null;
            }

            try {
                UserValidator.validateField(label, value);
                return value;
            } catch (UserValidationException e) {
                System.out.println("[Ошибка] " + e.getMessage() + ". Попробуйте снова.");
            }
        }
    }
}
