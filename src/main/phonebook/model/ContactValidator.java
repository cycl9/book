package phonebook.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Валидатор контакта.
 * Принимает массив строк из формы и возвращает объект Contact
 * либо выбрасывает ContactValidationException со списком ошибок.
 */
public final class ContactValidator {

    private ContactValidator() {}

    /**
     * Индексы полей (совпадают с Contact.columnNames()):
     *   0-surname, 1-name, 2-patronymic, 3-mobile, 4-home, 5-address, 6-birthday, 7-comment
     */
    public static Contact validate(String[] fields) throws ContactValidationException {
        var errors  = new ArrayList<Integer>();
        var message = new StringBuilder();

        // HTML-инъекция
        for (int i = 0; i < fields.length; i++) {
            var v = fields[i].replace(" ", "");
            if (v.contains("<") || v.contains(">")) {
                errors.add(i);
                append(message, ContactValidationException.HTML_INJECTION);
            }
        }

        // Обязательные текстовые поля
        if (fields[0].isBlank()) { errors.add(0); append(message, ContactValidationException.SURNAME_EMPTY); }
        if (fields[1].isBlank()) { errors.add(1); append(message, ContactValidationException.NAME_EMPTY); }

        // Телефон
        if (fields[3].isBlank() && fields[4].isBlank()) {
            errors.add(3); errors.add(4);
            append(message, ContactValidationException.NO_PHONE);
        }
        if (!fields[3].isBlank() && !fields[3].matches("^[0-9]+$")) {
            errors.add(3); append(message, ContactValidationException.PHONE_DIGITS);
        }
        if (!fields[4].isBlank() && !fields[4].matches("^[0-9]+$")) {
            errors.add(4); append(message, ContactValidationException.PHONE_DIGITS);
        }

        // Дата (необязательная)
        Date birthday = null;
        if (!fields[6].isBlank()) {
            try {
                Contact.DATE_FMT.setLenient(false);
                birthday = Contact.DATE_FMT.parse(fields[6]);
                if (birthday.after(new Date())) {
                    errors.add(6);
                    append(message, ContactValidationException.FUTURE_DATE);
                }
            } catch (ParseException e) {
                errors.add(6);
                append(message, ContactValidationException.BAD_DATE);
            }
        }

        if (!errors.isEmpty()) {
            throw new ContactValidationException(errors, message.toString().trim());
        }

        return new Contact(
                fields[0], fields[1], fields[2],
                fields[3], fields[4], fields[5],
                birthday,  fields[7]
        );
    }

    private static void append(StringBuilder sb, String msg) {
        sb.append("• ").append(msg).append("\n");
    }
}
