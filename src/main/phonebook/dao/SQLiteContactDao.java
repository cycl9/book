package phonebook.dao;

import phonebook.model.Contact;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Реализация ContactDao поверх SQLite.
 * При первом запуске автоматически создаёт таблицу contacts.
 *
 * Файл базы данных: phonebook.db (рядом с jar-файлом).
 */
public class SQLiteContactDao implements ContactDao {

    private final String url;

    public SQLiteContactDao(String dbPath) {
        this.url = "jdbc:sqlite:" + dbPath;
        initSchema();
    }

    // ── DDL ───────────────────────────────────────────────────────────────────

    private void initSchema() {
        var sql = """
                CREATE TABLE IF NOT EXISTS contacts (
                    id           INTEGER PRIMARY KEY AUTOINCREMENT,
                    surname      TEXT    NOT NULL,
                    name         TEXT    NOT NULL,
                    patronymic   TEXT    NOT NULL DEFAULT '',
                    mobile_phone TEXT    NOT NULL DEFAULT '',
                    home_phone   TEXT    NOT NULL DEFAULT '',
                    address      TEXT    NOT NULL DEFAULT '',
                    birthday     INTEGER,          -- Unix-timestamp (мс), NULL если не указана
                    comment      TEXT    NOT NULL DEFAULT ''
                )
                """;
        try (var conn = connect(); var st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось инициализировать схему БД: " + e.getMessage(), e);
        }
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    @Override
    public Contact insert(Contact c) throws SQLException {
        var sql = """
                INSERT INTO contacts
                    (surname, name, patronymic, mobile_phone, home_phone, address, birthday, comment)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (var conn = connect();
             var ps   = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bind(ps, c);
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) return c.withId(keys.getLong(1));
            }
        }
        throw new SQLException("INSERT не вернул ключ.");
    }

    @Override
    public void update(Contact c) throws SQLException {
        var sql = """
                UPDATE contacts SET
                    surname=?, name=?, patronymic=?,
                    mobile_phone=?, home_phone=?,
                    address=?, birthday=?, comment=?
                WHERE id=?
                """;
        try (var conn = connect(); var ps = conn.prepareStatement(sql)) {
            bind(ps, c);
            ps.setLong(9, c.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(long id) throws SQLException {
        try (var conn = connect(); var ps = conn.prepareStatement("DELETE FROM contacts WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Contact> findAll() throws SQLException {
        return query("SELECT * FROM contacts ORDER BY surname, name", null);
    }

    @Override
    public List<Contact> search(String q) throws SQLException {
        var like = "%" + q + "%";
        var sql  = """
                SELECT * FROM contacts
                WHERE  surname    LIKE ? COLLATE NOCASE
                    OR name       LIKE ? COLLATE NOCASE
                    OR patronymic LIKE ? COLLATE NOCASE
                ORDER BY surname, name
                """;
        return query(sql, ps -> {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
    }

    /** Привязывает поля контакта к позициям 1–8 PreparedStatement. */
    private static void bind(PreparedStatement ps, Contact c) throws SQLException {
        ps.setString(1, c.getSurname());
        ps.setString(2, c.getName());
        ps.setString(3, c.getPatronymic());
        ps.setString(4, c.getMobilePhone());
        ps.setString(5, c.getHomePhone());
        ps.setString(6, c.getAddress());
        if (c.getBirthday() != null) ps.setLong(7, c.getBirthday().getTime());
        else                          ps.setNull(7, Types.INTEGER);
        ps.setString(8, c.getComment());
    }

    @FunctionalInterface
    private interface Binder { void bind(PreparedStatement ps) throws SQLException; }

    private List<Contact> query(String sql, Binder binder) throws SQLException {
        var list = new ArrayList<Contact>();
        try (var conn = connect(); var ps = conn.prepareStatement(sql)) {
            if (binder != null) binder.bind(ps);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    private static Contact map(ResultSet rs) throws SQLException {
        var ts  = rs.getObject("birthday");
        Date bd = ts != null ? new Date(rs.getLong("birthday")) : null;
        return new Contact(
                rs.getLong("id"),
                rs.getString("surname"),
                rs.getString("name"),
                rs.getString("patronymic"),
                rs.getString("mobile_phone"),
                rs.getString("home_phone"),
                rs.getString("address"),
                bd,
                rs.getString("comment")
        );
    }
}
