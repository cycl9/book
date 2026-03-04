package phonebook.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ContactValidatorTest {

    private static String[] validFields() {
        return new String[]{"Иванов", "Иван", "Иванович", "9001234567", "", "Москва", "", "тест"};
    }

    @Test
    void validContact_createsSuccessfully() throws Exception {
        var c = ContactValidator.validate(validFields());
        assertEquals("Иванов", c.getSurname());
        assertEquals("Иван",   c.getName());
        assertEquals("9001234567", c.getMobilePhone());
    }

    @Test
    void emptySurname_throwsException() {
        var fields = validFields();
        fields[0] = "";
        var ex = assertThrows(ContactValidationException.class, () -> ContactValidator.validate(fields));
        assertTrue(ex.getBadFields().contains(0));
    }

    @Test
    void emptyName_throwsException() {
        var fields = validFields();
        fields[1] = "";
        var ex = assertThrows(ContactValidationException.class, () -> ContactValidator.validate(fields));
        assertTrue(ex.getBadFields().contains(1));
    }

    @Test
    void noPhone_throwsException() {
        var fields = validFields();
        fields[3] = "";
        fields[4] = "";
        var ex = assertThrows(ContactValidationException.class, () -> ContactValidator.validate(fields));
        assertTrue(ex.getBadFields().contains(3));
        assertTrue(ex.getBadFields().contains(4));
    }

    @Test
    void lettersInPhone_throwsException() {
        var fields = validFields();
        fields[3] = "abc123";
        var ex = assertThrows(ContactValidationException.class, () -> ContactValidator.validate(fields));
        assertTrue(ex.getBadFields().contains(3));
    }

    @Test
    void futureBirthday_throwsException() {
        var fields = validFields();
        fields[6] = "01.01.2099";
        var ex = assertThrows(ContactValidationException.class, () -> ContactValidator.validate(fields));
        assertTrue(ex.getBadFields().contains(6));
    }

    @Test
    void badDateFormat_throwsException() {
        var fields = validFields();
        fields[6] = "не-дата";
        var ex = assertThrows(ContactValidationException.class, () -> ContactValidator.validate(fields));
        assertTrue(ex.getBadFields().contains(6));
    }

    @Test
    void htmlInjection_throwsException() {
        var fields = validFields();
        fields[2] = "<script>";
        var ex = assertThrows(ContactValidationException.class, () -> ContactValidator.validate(fields));
        assertTrue(ex.getBadFields().contains(2));
    }

    @Test
    void onlyHomePhone_isValid() throws Exception {
        var fields = validFields();
        fields[3] = "";
        fields[4] = "84951234567";
        assertDoesNotThrow(() -> ContactValidator.validate(fields));
    }

    @Test
    void emptyBirthday_isValid() throws Exception {
        var fields = validFields();
        fields[6] = "";
        var c = ContactValidator.validate(fields);
        assertNull(c.getBirthday());
    }
}
