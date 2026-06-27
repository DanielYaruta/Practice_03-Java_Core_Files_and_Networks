package org.example.task3;

import java.util.regex.Pattern;

public class UserValidator {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 50;

    // Unicode letters, spaces, hyphens — no commas (would break CSV format)
    private static final Pattern VALID_PATTERN = Pattern.compile("[\\p{L} -]+");

    public static void validateField(String label, String value) throws UserValidationException {
        if (value == null || value.isBlank()) {
            throw new UserValidationException(label + " не может быть пустым");
        }
        if (value.length() < MIN_LENGTH) {
            throw new UserValidationException(
                label + " должно содержать минимум " + MIN_LENGTH + " символа, введено: " + value.length()
            );
        }
        if (value.length() > MAX_LENGTH) {
            throw new UserValidationException(
                label + " не должно превышать " + MAX_LENGTH + " символов, введено: " + value.length()
            );
        }
        if (!VALID_PATTERN.matcher(value).matches()) {
            throw new UserValidationException(
                label + " может содержать только буквы, пробелы и дефисы"
            );
        }
    }

    public static void validate(User user) throws UserValidationException {
        validateField("Имя", user.getName());
        validateField("Город", user.getCity());
    }
}
