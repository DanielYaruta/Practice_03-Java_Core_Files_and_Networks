package org.example.task3;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserManagerTest {

    // --- loadFromFile via UserManager.load() ---

    @Test
    void load_noFile_emptyList(@TempDir Path dir) {
        UserManager manager = UserManager.load(dir.resolve("users.txt"));
        assertTrue(manager.getUsers().isEmpty());
    }

    @Test
    void load_validFile_loadsAllUsers(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("users.txt");
        Files.writeString(file, "Алиса,Москва\nБорис,Киев\n");

        UserManager manager = UserManager.load(file);

        List<User> users = manager.getUsers();
        assertEquals(2, users.size());
        assertEquals("Алиса",  users.get(0).getName());
        assertEquals("Москва", users.get(0).getCity());
        assertEquals("Борис",  users.get(1).getName());
        assertEquals("Киев",   users.get(1).getCity());
    }

    @Test
    void load_malformedLine_skipsItLoadsRest(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("users.txt");
        Files.writeString(file, "Алиса,Москва\nсломаная_строка\nБорис,Киев\n");

        assertEquals(2, UserManager.load(file).getUsers().size());
    }

    @Test
    void load_lineWithInvalidData_skipsIt(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("users.txt");
        Files.writeString(file, ",Москва\nБорис,Киев\n");

        UserManager manager = UserManager.load(file);

        assertEquals(1, manager.getUsers().size());
        assertEquals("Борис", manager.getUsers().get(0).getName());
    }

    @Test
    void load_emptyFile_emptyList(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("users.txt");
        Files.writeString(file, "");

        assertTrue(UserManager.load(file).getUsers().isEmpty());
    }

    // --- constructor without load (no I/O on construction) ---

    @Test
    void constructor_doesNotLoadFile(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("users.txt");
        Files.writeString(file, "Алиса,Москва\n");

        // Direct constructor must NOT trigger I/O — list stays empty.
        UserManager manager = new UserManager(file);

        assertTrue(manager.getUsers().isEmpty(),
            "Constructor must not call loadFromFile(); use UserManager.load() for that");
    }

    // --- addUser ---

    @Test
    void addUser_validUser_addsToList(@TempDir Path dir) throws Exception {
        UserManager manager = new UserManager(dir.resolve("users.txt"));
        manager.addUser(new User("Алиса", "Москва"));
        assertEquals(1, manager.getUsers().size());
        assertEquals("Алиса", manager.getUsers().get(0).getName());
    }

    @Test
    void addUser_validUser_writesLineToFile(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("users.txt");
        UserManager manager = new UserManager(file);
        manager.addUser(new User("Алиса", "Москва"));

        List<String> lines = Files.readAllLines(file);
        assertEquals(1, lines.size());
        assertEquals("Алиса,Москва", lines.get(0));
    }

    @Test
    void addUser_multipleUsers_appendsEachLine(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("users.txt");
        UserManager manager = new UserManager(file);
        manager.addUser(new User("Алиса", "Москва"));
        manager.addUser(new User("Борис", "Киев"));

        List<String> lines = Files.readAllLines(file);
        assertEquals(2, lines.size());
        assertEquals("Борис,Киев", lines.get(1));
    }

    @Test
    void addUser_invalidUser_throwsValidationException(@TempDir Path dir) {
        UserManager manager = new UserManager(dir.resolve("users.txt"));
        assertThrows(UserValidationException.class,
            () -> manager.addUser(new User("А", "Москва")));
    }

    @Test
    void addUser_invalidUser_listUnchanged(@TempDir Path dir) throws Exception {
        UserManager manager = new UserManager(dir.resolve("users.txt"));
        manager.addUser(new User("Алиса", "Москва"));

        assertThrows(UserValidationException.class,
            () -> manager.addUser(new User("", "Киев")));

        assertEquals(1, manager.getUsers().size());
    }

    @Test
    void addUser_ioException_rollsBackList(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("users.txt");
        Files.writeString(file, "");
        file.toFile().setWritable(false);

        try {
            UserManager manager = new UserManager(file);
            assertThrows(IOException.class,
                () -> manager.addUser(new User("Алиса", "Москва")));
            assertTrue(manager.getUsers().isEmpty());
        } finally {
            file.toFile().setWritable(true);
        }
    }

    // --- persistence across instances ---

    @Test
    void addUser_persistedAndReloadedByNewInstance(@TempDir Path dir) throws Exception {
        Path file = dir.resolve("users.txt");

        UserManager writer = new UserManager(file);
        writer.addUser(new User("Алиса", "Москва"));
        writer.addUser(new User("Борис", "Киев"));

        UserManager reader = UserManager.load(file);
        assertEquals(2, reader.getUsers().size());
        assertEquals("Алиса", reader.getUsers().get(0).getName());
        assertEquals("Борис", reader.getUsers().get(1).getName());
    }
}
