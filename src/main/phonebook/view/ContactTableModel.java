package phonebook.view;

import phonebook.model.Contact;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель таблицы для JTable.
 * Хранит текущий отображаемый список контактов.
 */
public class ContactTableModel extends AbstractTableModel {

    private List<Contact> contacts = new ArrayList<>();
    private final String[] columns = Contact.columnNames();

    public void setContacts(List<Contact> list) {
        this.contacts = new ArrayList<>(list);
        fireTableDataChanged();
    }

    /** Возвращает контакт по индексу строки. */
    public Contact getContact(int row) {
        return contacts.get(row);
    }

    @Override public int getRowCount()    { return contacts.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int col) { return columns[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        return contacts.get(row).toRow()[col];
    }

    @Override public boolean isCellEditable(int row, int col) { return false; }
}
