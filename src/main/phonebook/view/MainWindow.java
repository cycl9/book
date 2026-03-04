package phonebook.view;

import phonebook.controller.PhonebookController;
import phonebook.model.Contact;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Главное окно приложения «Телефонная адресная книга».
 */
public class MainWindow extends JFrame {

    private final ContactTableModel tableModel = new ContactTableModel();
    private final JTable table = new JTable(tableModel);

    private final JTextField searchField = new JTextField(20);
    private final JLabel statusLabel = new JLabel(" ");

    private PhonebookController controller;

    public MainWindow() {
        super("Телефонная адресная книга");
        applyLookAndFeel();
        buildUI();
        configureTable();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(860, 560));
        pack();
        setLocationRelativeTo(null);
    }

    public void setController(PhonebookController c) {
        this.controller = c;
    }

    // ── Построение UI ─────────────────────────────────────────────────────────

    private void buildUI() {
        var content = (JPanel) getContentPane();
        content.setLayout(new BorderLayout(6, 6));
        content.setBorder(new EmptyBorder(8, 10, 8, 10));

        content.add(buildToolbar(), BorderLayout.NORTH);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        content.add(buildStatusBar(), BorderLayout.SOUTH);

        setJMenuBar(buildMenuBar());
    }

    private JToolBar buildToolbar() {
        var tb = new JToolBar();
        tb.setFloatable(false);

        var btnAdd    = makeButton("Добавить",    "➕");
        var btnEdit   = makeButton("Изменить",    "✏️");
        var btnDelete = makeButton("Удалить",     "🗑");

        btnAdd.addActionListener(e    -> controller.onAdd());
        btnEdit.addActionListener(e   -> controller.onEdit(selectedContact()));
        btnDelete.addActionListener(e -> controller.onDelete(selectedContact()));

        // Поиск
        var btnSearch = new JButton("Найти");
        btnSearch.addActionListener(e -> controller.onSearch(searchField.getText()));
        searchField.addActionListener(e -> controller.onSearch(searchField.getText()));

        var btnReset = new JButton("Сброс");
        btnReset.addActionListener(e -> { searchField.setText(""); controller.onSearch(""); });

        tb.add(btnAdd);
        tb.add(btnEdit);
        tb.add(btnDelete);
        tb.addSeparator();
        tb.add(new JLabel(" Поиск: "));
        tb.add(searchField);
        tb.add(btnSearch);
        tb.add(btnReset);

        return tb;
    }

    private JMenuBar buildMenuBar() {
        var bar  = new JMenuBar();
        var file = new JMenu("Файл");
        var help = new JMenu("Справка");

        var miExit  = new JMenuItem("Выход");
        var miAbout = new JMenuItem("О программе");

        miExit.addActionListener(e  -> System.exit(0));
        miAbout.addActionListener(e -> showAbout());

        file.add(miExit);
        help.add(miAbout);
        bar.add(file);
        bar.add(help);
        return bar;
    }

    private JPanel buildStatusBar() {
        var p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setBorder(BorderFactory.createEtchedBorder());
        p.add(statusLabel);
        return p;
    }

    private void configureTable() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(22);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Двойной клик — открыть редактирование
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) controller.onEdit(selectedContact());
            }
        });
    }

    // ── Публичный API для контроллера ─────────────────────────────────────────

    /** Обновляет данные в таблице. */
    public void setContacts(java.util.List<Contact> contacts) {
        tableModel.setContacts(contacts);
        setStatus("Записей: " + contacts.size());
    }

    /** Показывает сообщение в строке статуса. */
    public void setStatus(String msg) {
        statusLabel.setText(" " + msg);
    }

    /** Открывает форму создания нового контакта и возвращает её. */
    public ContactForm openCreateForm() {
        var form = ContactForm.forCreate(this);
        form.setVisible(true);
        return form;
    }

    /** Открывает форму редактирования и возвращает её. */
    public ContactForm openEditForm(Contact c) {
        if (c == null) return null;
        var form = ContactForm.forEdit(this, c);
        form.setVisible(true);
        return form;
    }

    /** Показывает диалог подтверждения удаления. */
    public boolean confirmDelete(Contact c) {
        if (c == null) return false;
        return JOptionPane.showConfirmDialog(this,
                "Удалить контакт «" + c + "»?", "Подтверждение",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /** Показывает сообщение об ошибке. */
    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    // ── Вспомогательные ───────────────────────────────────────────────────────

    private Contact selectedContact() {
        int row = table.getSelectedRow();
        return row >= 0 ? tableModel.getContact(row) : null;
    }

    private static JButton makeButton(String text, String icon) {
        return new JButton(icon + " " + text);
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "<html><h2>Телефонная адресная книга</h2>" +
                "<p>Версия 1.0 · Java 21 · SQLite</p></html>",
                "О программе", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void applyLookAndFeel() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}
    }
}
