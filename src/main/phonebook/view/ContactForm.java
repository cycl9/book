package phonebook.view;

import org.jdatepicker.impl.DateComponentFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import phonebook.model.Contact;
import phonebook.model.ContactValidationException;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

/**
 * Диалог создания и редактирования контакта.
 */
public class ContactForm extends JDialog {

    private static final int DATE_INDEX = 6;

    private final JTextField[] textFields = new JTextField[8];
    private JDatePickerImpl datePicker;
    private final JTextArea commentArea = new JTextArea(3, 20);

    private boolean confirmed = false;

    private ContactForm(Frame owner, String title) {
        super(owner, title, true);
        buildUI();
        pack();
        setMinimumSize(new Dimension(420, 500));
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        var content = (JPanel) getContentPane();
        content.setLayout(new BorderLayout(8, 8));
        content.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        var form  = new JPanel(new GridLayout(0, 2, 8, 6));
        var names = Contact.columnNames();

        for (int i = 0; i < names.length; i++) {
            // Звёздочка для обязательных полей
            var label = (i == 0 || i == 1)
                    ? "<html>" + names[i] + " <font color='red'>*</font></html>"
                    : names[i];
            form.add(new JLabel(label));

            if (i == DATE_INDEX) {
                datePicker = createDatePicker();
                form.add(datePicker);
            } else if (i == 7) {
                textFields[i] = null; // комментарий отдельно
                form.add(new JScrollPane(commentArea));
            } else {
                textFields[i] = new JTextField();
                // Фильтр цифр для телефонов
                if (i == 3 || i == 4) addPhoneFilter(textFields[i]);
                form.add(textFields[i]);
            }
        }

        // Кнопки
        var btnSave   = new JButton("Сохранить");
        var btnCancel = new JButton("Отмена");
        btnSave.addActionListener(e -> { confirmed = true; close(); });
        btnCancel.addActionListener(e -> close());

        var buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(btnCancel);
        buttons.add(btnSave);

        content.add(form,    BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);
    }

    // ── Публичный API ─────────────────────────────────────────────────────────

    /** Открывает форму создания нового контакта. */
    public static ContactForm forCreate(Frame owner) {
        return new ContactForm(owner, "Новый контакт");
    }

    /** Открывает форму редактирования существующего контакта. */
    public static ContactForm forEdit(Frame owner, Contact c) {
        var form = new ContactForm(owner, "Редактирование: " + c);
        form.populate(c);
        return form;
    }

    /** true если пользователь нажал «Сохранить». */
    public boolean isConfirmed() { return confirmed; }

    /**
     * Возвращает введённые значения в виде String[8].
     * Порядок совпадает с Contact.columnNames().
     */
    public String[] getFields() {
        var fields = new String[8];
        for (int i = 0; i < 8; i++) {
            if (i == DATE_INDEX) {
                fields[i] = datePickerValue();
            } else if (i == 7) {
                fields[i] = commentArea.getText().trim();
            } else {
                fields[i] = textFields[i].getText().trim();
            }
        }
        // Очищаем форматирование телефонов
        fields[3] = stripPhone(fields[3]);
        fields[4] = stripPhone(fields[4]);
        return fields;
    }

    /** Подсвечивает некорректные поля красной рамкой. */
    public void highlightErrors(ContactValidationException ex) {
        // Сброс всех рамок
        for (int i = 0; i < 8; i++) {
            if (i != DATE_INDEX && i != 7 && textFields[i] != null)
                textFields[i].setBorder(UIManager.getLookAndFeel()
                        .getDefaults().getBorder("TextField.border"));
        }
        var red = new LineBorder(Color.RED, 2);
        for (var idx : ex.getBadFields()) {
            if (idx == DATE_INDEX) {
                datePicker.setBorder(red);
            } else if (idx != 7 && textFields[idx] != null) {
                textFields[idx].setBorder(red);
            }
        }
    }

    // ── Вспомогательные ───────────────────────────────────────────────────────

    private void populate(Contact c) {
        var row = c.toRow();
        for (int i = 0; i < 8; i++) {
            if (i == DATE_INDEX) {
                if (c.getBirthday() != null) {
                    var cal = new GregorianCalendar();
                    cal.setTime(c.getBirthday());
                    datePicker.getModel().setDate(
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH));
                    datePicker.getModel().setSelected(true);
                }
            } else if (i == 7) {
                commentArea.setText(row[i].toString());
            } else if (textFields[i] != null) {
                textFields[i].setText(row[i].toString());
            }
        }
    }

    private String datePickerValue() {
        if (!datePicker.getModel().isSelected()) return "";
        var date = (Date) datePicker.getModel().getValue();
        return date == null ? "" : Contact.DATE_FMT.format(date);
    }

    private static String stripPhone(String s) {
        return s.replaceAll("[^0-9]", "");
    }

    private static void addPhoneFilter(JTextField field) {
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) {
                var t = field.getText();
                if (!t.matches("[0-9+()\\- ]*")) {
                    field.setText(t.replaceAll("[^0-9+()\\- ]", ""));
                }
            }
        });
    }

    private static JDatePickerImpl createDatePicker() {
        var model = new UtilDateModel();
        var panel = new JDatePanelImpl(model, new Properties());
        return new JDatePickerImpl(panel, new DateComponentFormatter());
    }

    private void close() {
        processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}
