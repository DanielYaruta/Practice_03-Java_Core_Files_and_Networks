package org.example.task3;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    // --- validateField: valid inputs ---

    @Test
    void validateField_validValue_passes() {
        assertDoesNotThrow(() -> UserValidator.validateField("Имя", "Алиса"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Ли", "Анна-Мария", "Ван дер Берг"})
    void validateField_edgeCases_pass(String value) {
        assertDoesNotThrow(() -> UserValidator.validateField("Имя", value));
    }

    // --- validateField: blank ---

    @Test
    void validateField_null_throws() {
        UserValidationException ex = assertThrows(
            UserValidationException.class,
            () -> UserValidator.validateField("Имя", null)
        );
        assertTrue(ex.getMessage().contains("пустым"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void validateField_blankString_throws(String value) {
        assertThrows(UserValidationException.class,
            () -> UserValidator.validateField("Имя", value));
    }

    // --- validateField: length ---

    @Test
    void validateField_tooShort_throws() {
        UserValidationException ex = assertThrows(
            UserValidationException.class,
            () -> UserValidator.validateField("Имя", "А")
        );
        assertTrue(ex.getMessage().contains("минимум"));
    }

    @Test
    void validateField_exactMinLength_passes() {
        assertDoesNotThrow(() -> UserValidator.validateField("Имя", "Ли"));
    }

    @Test
    void validateField_tooLong_throws() {
        String longValue = "А".repeat(51);
        UserValidationException ex = assertThrows(
            UserValidationException.class,
            () -> UserValidator.validateField("Имя", longValue)
        );
        assertTrue(ex.getMessage().contains("превышать"));
    }

    @Test
    void validateField_exactMaxLength_passes() {
        String maxValue = "А".repeat(50);
        assertDoesNotThrow(() -> UserValidator.validateField("Имя", maxValue));
    }

    // --- validateField: invalid characters ---

    @ParameterizedTest
    @ValueSource(strings = {"Алиса1", "Алиса,Борис", "Alice@Mail", "Иван!"})
    void validateField_invalidChars_throws(String value) {
        assertThrows(UserValidationException.class,
            () -> UserValidator.validateField("Имя", value));
    }

    @Test
    void validateField_comma_throws() {
        UserValidationException ex = assertThrows(
            UserValidationException.class,
            () -> UserValidator.validateField("Имя", "Иван,Петров")
        );
        assertTrue(ex.getMessage().contains("буквы"));
    }

    // --- validate(User) ---

    @Test
    void validate_validUser_passes() {
        assertDoesNotThrow(() -> UserValidator.validate(new User("Алиса", "Москва")));
    }

    @Test
    void validate_invalidName_throwsWithNameLabel() {
        UserValidationException ex = assertThrows(
            UserValidationException.class,
            () -> UserValidator.validate(new User("А", "Москва"))
        );
        assertTrue(ex.getMessage().contains("Имя"));
    }

    @Test
    void validate_invalidCity_throwsWithCityLabel() {
        UserValidationException ex = assertThrows(
            UserValidationException.class,
            () -> UserValidator.validate(new User("Алиса", ""))
        );
        assertTrue(ex.getMessage().contains("Город"));
    }
}
