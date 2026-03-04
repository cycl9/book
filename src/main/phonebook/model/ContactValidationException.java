package phonebook.model;

import java.util.List;

/**
 * Исключение, выбрасываемое при ошибках валидации контакта.
 * Содержит индексы некорректных полей и текстовое описание.
 */
public class ContactValidationException extends Exception {

    private final List<Integer> badFields;

    public ContactValidationException(List<Integer> badFields, String message) {
        super(message);
        this.badFields = badFields;
    }

    /** Индексы полей (по порядку из Contact.columnNames()), не прошедших проверку. */
    public List<Integer> getBadFields() { return badFields; }

    // ── Тексты ошибок ─────────────────────────────────────────────────────────
    public static final String SURNAME_EMPTY  = "Фамилия обязательна для заполнения.";
    public static final String NAME_EMPTY     = "Имя обязательно для заполнения.";
    public static final String NO_PHONE       = "Укажите хотя бы один номер телефона.";
    public static final String PHONE_DIGITS   = "Номер телефона должен содержать только цифры.";
    public static final String BAD_DATE       = "Дата рождения указана в неверном формате (дд.ММ.гггг).";
    public static final String FUTURE_DATE    = "Дата рождения не может быть в будущем.";
    public static final String HTML_INJECTION = "Поле содержит недопустимые символы (< или >).";
}
