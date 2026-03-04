package phonebook.dao;

import phonebook.model.Contact;

import java.sql.SQLException;
import java.util.List;

/**
 * Интерфейс доступа к данным (Data Access Object).
 * Абстрагирует хранилище от остального приложения.
 * Реализация: {@link SQLiteContactDao}.
 */
public interface ContactDao {

    /** Сохраняет новый контакт и возвращает его с проставленным id. */
    Contact insert(Contact contact) throws SQLException;

    /** Обновляет существующий контакт по id. */
    void update(Contact contact) throws SQLException;

    /** Удаляет контакт по id. */
    void delete(long id) throws SQLException;

    /** Возвращает все контакты, отсортированные по фамилии. */
    List<Contact> findAll() throws SQLException;

    /**
     * Ищет контакты, у которых фамилия, имя или отчество содержат строку query
     * (регистронезависимо).
     */
    List<Contact> search(String query) throws SQLException;
}
