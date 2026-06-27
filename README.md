# Многопоточность и работа с файлами на Java

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.6+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit-5-25A162?style=for-the-badge&logo=junit5&logoColor=white)

## Задачи

### Задача 1 — Синхронизация потоков в кафе (`task1`)

Моделирование кафе по схеме «производитель — потребитель».

- **Chef** (поток-производитель) готовит 10 блюд с паузой 3 секунды между каждым
- **Visitor** (потоки-потребители) ждут блюдо и забирают его, как только оно готово
- Синхронизация реализована через `wait()` / `notifyAll()` без `BlockingQueue`

```
Повар начал работу
Посетитель 1 зашел в кафе
Еды нет, Посетитель-1 ждет
Повар приготовил Бургер
Посетитель 1 съел Бургер
...
Повар закончил работу и закрыл кафе
```

### Задача 2 — Генерация и подсчёт чисел (`task2`)

Параллельный подсчёт случайных чисел по категориям.

- Генерируется массив из 100 000 чисел в диапазоне `[10, 9999]`
- Три потока одновременно подсчитывают двузначные, трёхзначные и четырёхзначные числа
- Счётчики — `AtomicInteger`, пул потоков — `ExecutorService` + `invokeAll`

```
Двузначных чисел:      910 шт.
Трехзначных чисел:    8943 шт.
Четырехзначных чисел: 90147 шт.
Итого: 100000 шт.
```

### Задача 3 — Сохранение пользователей через NIO (`task3`)

Хранение пользователей в CSV-файле с валидацией и блокировкой.

- Чтение и запись через `java.nio.file.Files` и `FileChannel`
- `FileLock` исключает одновременную запись несколькими процессами
- Валидация полей (`UserValidator`) с кастомным `UserValidationException`
- Откат in-memory состояния при ошибке записи в файл
- Логирование через `java.util.logging`

```
=== Загруженные пользователи ===
User{name='Алиса', city='Москва'}

=== Добавление новых пользователей ===
Имя: [Ошибка] Имя должно содержать минимум 2 символа. Попробуйте снова.
Имя: Борис
Город: Киев
Сохранено: User{name='Борис', city='Киев'}
```

## Структура проекта

```
src/
├── main/java/org/example/
│   ├── task1/
│   │   ├── Cafe.java          # общий ресурс (wait/notifyAll, waitingCount)
│   │   ├── Chef.java          # поток-производитель
│   │   ├── Visitor.java       # поток-потребитель
│   │   └── CafeMain.java      # точка входа
│   ├── task2/
│   │   ├── NumberGenerator.java  # генерация через ThreadLocalRandom
│   │   ├── NumberChecker.java    # Runnable-счётчик по диапазону
│   │   └── NumberMain.java       # точка входа
│   └── task3/
│       ├── User.java
│       ├── UserValidator.java
│       ├── UserValidationException.java
│       ├── UserManager.java      # load() + FileLock + Logger
│       └── UserMain.java         # консольный интерфейс
└── test/java/org/example/
    ├── task1/
    │   ├── CafeTest.java              # юнит-тесты Cafe (wait/notify)
    │   └── CafeIntegrationTest.java   # полный сценарий Chef + Visitor
    ├── task2/
    │   ├── NumberGeneratorTest.java
    │   ├── NumberCheckerTest.java     # параметризованные boundary-тесты
    │   └── NumberIntegrationTest.java
    └── task3/
        ├── UserTest.java
        ├── UserValidatorTest.java
        └── UserManagerTest.java
```
