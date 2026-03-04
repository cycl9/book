# Телефонная адресная книга

Десктопное Java-приложение для хранения и управления контактами.

## Стек

| Компонент | Технология |
|-----------|-----------|
| Язык      | Java 21   |
| GUI       | Swing     |
| База данных | SQLite (через JDBC) |
| Виджет даты | JDatePicker 1.3.4 |
| Тесты     | JUnit 5.10 |
| Сборка    | Maven 3   |

## Архитектура (MVC)

```
Main
 └─► PhonebookController          ← Controller
       ├─► ContactService          ← Service (бизнес-логика)
       │     ├─► ContactValidator  ← валидация
       │     └─► ContactDao        ← интерфейс доступа к данным
       │           └─► SQLiteContactDao  ← реализация (SQLite)
       └─► MainWindow              ← View (Swing)
             ├─► ContactTableModel
             └─► ContactForm
```

## Структура проекта

```
src/
  main/phonebook/
    model/     — Contact, ContactValidator, ContactValidationException
    dao/       — ContactDao (интерфейс), SQLiteContactDao (реализация)
    service/   — ContactService
    controller/— PhonebookController
    view/      — MainWindow, ContactForm, ContactTableModel
    Main.java  — точка входа
  test/phonebook/
    model/     — ContactValidatorTest
    dao/       — SQLiteContactDaoTest
```

## Сборка и запуск

### Требования
- JDK 21+
- Maven 3.6+

### Сборка
```bash
mvn package
```

Создаётся `target/phonebook.jar` — fat-jar со всеми зависимостями.

### Запуск
```bash
java -jar target/phonebook.jar
```

База данных сохраняется в `phonebook.db` рядом с jar-файлом.

### Запуск тестов
```bash
mvn test
```

## Функциональность

- Добавление, редактирование, удаление контактов
- Поля: Фамилия, Имя, Отчество, Мобильный телефон, Домашний телефон, Адрес, День рождения, Комментарий
- Поиск по ФИО (в реальном времени)
- Валидация полей с подсветкой ошибок
- Постоянное хранение в SQLite (файл `phonebook.db`)
- Сортировка по фамилии
