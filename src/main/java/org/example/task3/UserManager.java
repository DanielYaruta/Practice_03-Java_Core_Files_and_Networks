package org.example.task3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UserManager {

    private static final Logger logger = Logger.getLogger(UserManager.class.getName());

    private final List<User> users = new ArrayList<>();
    private final Path filePath;

    // Package-private: allows direct construction in tests without triggering I/O.
    UserManager(Path filePath) {
        this.filePath = filePath;
    }

    public static UserManager load(Path filePath) {
        UserManager manager = new UserManager(filePath);
        manager.loadFromFile();
        return manager;
    }

    public void loadFromFile() {
        if (!Files.exists(filePath)) {
            return;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(filePath);
        } catch (IOException e) {
            logger.severe("Не удалось прочитать файл '" + filePath + "': " + e.getMessage());
            return;
        }

        int loaded = 0;
        int skipped = 0;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isBlank()) {
                continue;
            }
            String[] parts = line.split(",", 2);
            if (parts.length != 2) {
                logger.warning("Строка " + (i + 1) + " имеет неверный формат, пропущена: \"" + line + "\"");
                skipped++;
                continue;
            }
            String name = parts[0].trim();
            String city = parts[1].trim();
            try {
                UserValidator.validateField("Имя", name);
                UserValidator.validateField("Город", city);
                users.add(new User(name, city));
                loaded++;
            } catch (UserValidationException e) {
                logger.warning("Строка " + (i + 1) + " содержит некорректные данные, пропущена: " + e.getMessage());
                skipped++;
            }
        }

        logger.info("Загружено: " + loaded + " пользователей"
            + (skipped > 0 ? ", пропущено строк: " + skipped : "") + ".");
    }

    // FileLock prevents concurrent corruption when multiple processes write simultaneously.
    private void saveToFile(User user) throws IOException {
        String line = user.getName() + "," + user.getCity() + System.lineSeparator();
        byte[] bytes = line.getBytes(StandardCharsets.UTF_8);
        try (FileChannel channel = FileChannel.open(filePath,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND,
                StandardOpenOption.WRITE);
             FileLock lock = channel.lock()) {
            channel.write(ByteBuffer.wrap(bytes));
        }
    }

    public void addUser(User user) throws UserValidationException, IOException {
        UserValidator.validate(user);
        users.add(user);
        try {
            saveToFile(user);
        } catch (IOException e) {
            users.remove(users.size() - 1); // rollback: keep in-memory list consistent with file
            throw new IOException("Не удалось сохранить пользователя в файл: " + e.getMessage(), e);
        }
    }

    public List<User> getUsers() {
        return users;
    }
}
