package org.example.task3;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void constructor_setsFields() {
        User user = new User("Алиса", "Москва");
        assertEquals("Алиса", user.getName());
        assertEquals("Москва", user.getCity());
    }

    @Test
    void setters_updateFields() {
        User user = new User("Алиса", "Москва");
        user.setName("Борис");
        user.setCity("Киев");
        assertEquals("Борис", user.getName());
        assertEquals("Киев", user.getCity());
    }

    @Test
    void toString_containsNameAndCity() {
        User user = new User("Алиса", "Москва");
        String result = user.toString();
        assertTrue(result.contains("Алиса"));
        assertTrue(result.contains("Москва"));
    }
}
