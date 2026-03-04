package phonebook.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Неизменяемая модель контакта телефонной книги.
 */
public final class Contact {

    private final long   id;
    private final String surname;
    private final String name;
    private final String patronymic;
    private final String mobilePhone;
    private final String homePhone;
    private final String address;
    private final Date   birthday;
    private final String comment;

    public static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("dd.MM.yyyy");

    /** Новый контакт (id = 0, будет присвоен после INSERT). */
    public Contact(String surname, String name, String patronymic,
                   String mobilePhone, String homePhone, String address,
                   Date birthday, String comment) {
        this(0, surname, name, patronymic, mobilePhone, homePhone, address, birthday, comment);
    }

    /** Контакт, загруженный из БД. */
    public Contact(long id, String surname, String name, String patronymic,
                   String mobilePhone, String homePhone, String address,
                   Date birthday, String comment) {
        this.id          = id;
        this.surname     = Objects.requireNonNullElse(surname,    "");
        this.name        = Objects.requireNonNullElse(name,       "");
        this.patronymic  = Objects.requireNonNullElse(patronymic, "");
        this.mobilePhone = Objects.requireNonNullElse(mobilePhone,"");
        this.homePhone   = Objects.requireNonNullElse(homePhone,  "");
        this.address     = Objects.requireNonNullElse(address,    "");
        this.birthday    = birthday;
        this.comment     = Objects.requireNonNullElse(comment,    "");
    }

    public long   getId()          { return id; }
    public String getSurname()     { return surname; }
    public String getName()        { return name; }
    public String getPatronymic()  { return patronymic; }
    public String getMobilePhone() { return mobilePhone; }
    public String getHomePhone()   { return homePhone; }
    public String getAddress()     { return address; }
    public Date   getBirthday()    { return birthday; }
    public String getComment()     { return comment; }

    /** Возвращает копию с новым id (используется после INSERT). */
    public Contact withId(long newId) {
        return new Contact(newId, surname, name, patronymic,
                mobilePhone, homePhone, address, birthday, comment);
    }

    /** Названия колонок таблицы — порядок совпадает с toRow(). */
    public static String[] columnNames() {
        return new String[]{
            "Фамилия", "Имя", "Отчество",
            "Мобильный", "Домашний",
            "Адрес", "День рождения", "Комментарий"
        };
    }

    /** Значения полей для отображения в JTable. */
    public Object[] toRow() {
        return new Object[]{
            surname, name, patronymic,
            mobilePhone, homePhone, address,
            birthday != null ? DATE_FMT.format(birthday) : "",
            comment
        };
    }

    /** Равенство по ФИО (бизнес-ключ). */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contact other)) return false;
        return surname.equals(other.surname)
            && name.equals(other.name)
            && patronymic.equals(other.patronymic);
    }

    @Override public int hashCode() { return Objects.hash(surname, name, patronymic); }
    @Override public String toString() { return surname + " " + name + " " + patronymic; }
}
