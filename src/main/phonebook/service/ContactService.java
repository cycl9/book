package phonebook.service;

import phonebook.dao.ContactDao;
import phonebook.model.Contact;
import phonebook.model.ContactValidationException;
import phonebook.model.ContactValidator;

import java.sql.SQLException;
import java.util.List;

/**
 * Сервисный слой: связывает DAO с валидацией.
 * Контроллеры и UI работают только через этот класс.
 */
public class ContactService {

    private final ContactDao dao;

    public ContactService(ContactDao dao) {
        this.dao = dao;
    }

    /** Создаёт и сохраняет новый контакт. */
    public Contact create(String[] fields) throws ContactValidationException, SQLException {
        var contact = ContactValidator.validate(fields);
        return dao.insert(contact);
    }

    /** Обновляет существующий контакт. */
    public Contact update(long id, String[] fields) throws ContactValidationException, SQLException {
        var validated = ContactValidator.validate(fields);
        var updated   = validated.withId(id);
        dao.update(updated);
        return updated;
    }

    /** Удаляет контакт по id. */
    public void delete(long id) throws SQLException {
        dao.delete(id);
    }

    /** Возвращает все контакты. */
    public List<Contact> getAll() throws SQLException {
        return dao.findAll();
    }

    /** Возвращает контакты, соответствующие поисковому запросу. */
    public List<Contact> search(String query) throws SQLException {
        if (query == null || query.isBlank()) return dao.findAll();
        return dao.search(query.trim());
    }
}
