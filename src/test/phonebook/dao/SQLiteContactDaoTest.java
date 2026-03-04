package phonebook.dao;

import phonebook.model.Contact;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

/**
 * Интеграционный тест DAO — работает с in-memory SQLite БД (:memory:).
 */
class SQLiteContactDaoTest {

    private SQLiteContactDao dao;

    @BeforeEach
    void setUp() {
        dao = new SQLiteContactDao(":memory:");
    }

    private Contact sample() {
        return new Contact("Петров", "Пётр", "Петрович",
                "9009009090", "", "Санкт-Петербург", null, "");
    }

    @Test
    void insert_assignsId() throws SQLException {
        var saved = dao.insert(sample());
        assertTrue(saved.getId() > 0);
    }

    @Test
    void findAll_returnsInserted() throws SQLException {
        dao.insert(sample());
        var list = dao.findAll();
        assertEquals(1, list.size());
        assertEquals("Петров", list.get(0).getSurname());
    }

    @Test
    void update_changesFields() throws SQLException {
        var saved   = dao.insert(sample());
        var updated = new Contact(saved.getId(), "Петров", "Пётр", "Петрович",
                "9991112233", "", "Москва", null, "обновлён");
        dao.update(updated);

        var list = dao.findAll();
        assertEquals("9991112233", list.get(0).getMobilePhone());
        assertEquals("Москва",     list.get(0).getAddress());
    }

    @Test
    void delete_removesContact() throws SQLException {
        var saved = dao.insert(sample());
        dao.delete(saved.getId());
        assertTrue(dao.findAll().isEmpty());
    }

    @Test
    void search_findsMatchingContacts() throws SQLException {
        dao.insert(sample());
        dao.insert(new Contact("Сидоров", "Сидор", "", "9001234567", "", "", null, ""));

        var result = dao.search("Пет");
        assertEquals(1, result.size());
        assertEquals("Петров", result.get(0).getSurname());
    }

    @Test
    void search_emptyQuery_returnsAll() throws SQLException {
        dao.insert(sample());
        dao.insert(new Contact("Сидоров", "Сидор", "", "9001234567", "", "", null, ""));
        assertEquals(2, dao.findAll().size());
    }

    @Test
    void insert_multipleContacts_sortedBySurname() throws SQLException {
        dao.insert(new Contact("Яковлев", "Яков", "", "1111111111", "", "", null, ""));
        dao.insert(sample()); // Петров
        dao.insert(new Contact("Антонов", "Антон", "", "2222222222", "", "", null, ""));

        var list = dao.findAll();
        assertEquals("Антонов", list.get(0).getSurname());
        assertEquals("Петров",  list.get(1).getSurname());
        assertEquals("Яковлев", list.get(2).getSurname());
    }
}
