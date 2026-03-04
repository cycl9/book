package phonebook;

import phonebook.controller.PhonebookController;
import phonebook.dao.SQLiteContactDao;
import phonebook.service.ContactService;
import phonebook.view.MainWindow;

import javax.swing.*;

/**
 * Точка входа в приложение «Телефонная адресная книга».
 *
 * Запуск:
 *   mvn package
 *   java -jar target/phonebook.jar
 *
 * База данных сохраняется в файл phonebook.db рядом с jar.
 */
public class Main {

    public static void main(String[] args) {
        // Путь к БД: рядом с jar или в рабочей директории при запуске из IDE
        var dbPath = "phonebook.db";

        // Собираем слои: DAO → Service → Controller → View
        var dao        = new SQLiteContactDao(dbPath);
        var service    = new ContactService(dao);
        var view       = new MainWindow();
        var controller = new PhonebookController(service, view);

        // Запускаем UI в потоке Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            controller.init();
            view.setVisible(true);
        });
    }
}
