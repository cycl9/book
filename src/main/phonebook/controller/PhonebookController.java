package phonebook.controller;

import phonebook.model.Contact;
import phonebook.model.ContactValidationException;
import phonebook.service.ContactService;
import phonebook.view.MainWindow;

import java.sql.SQLException;

/**
 * Контроллер приложения (паттерн MVC).
 * Получает события от View, вызывает Service, обновляет View.
 */
public class PhonebookController {

    private final ContactService service;
    private final MainWindow     view;
    private String currentQuery = "";

    public PhonebookController(ContactService service, MainWindow view) {
        this.service = service;
        this.view    = view;
        view.setController(this);
    }

    /** Начальная загрузка данных. */
    public void init() {
        refresh();
    }

    // ── Обработчики событий ───────────────────────────────────────────────────

    public void onAdd() {
        var form = view.openCreateForm();
        if (!form.isConfirmed()) return;

        try {
            service.create(form.getFields());
            refresh();
            view.setStatus("Контакт добавлен.");
        } catch (ContactValidationException ex) {
            form.highlightErrors(ex);
            view.showError(ex.getMessage());
        } catch (SQLException ex) {
            view.showError("Ошибка БД: " + ex.getMessage());
        }
    }

    public void onEdit(Contact selected) {
        if (selected == null) { view.setStatus("Выберите контакт для редактирования."); return; }

        var form = view.openEditForm(selected);
        if (form == null || !form.isConfirmed()) return;

        try {
            service.update(selected.getId(), form.getFields());
            refresh();
            view.setStatus("Контакт обновлён.");
        } catch (ContactValidationException ex) {
            form.highlightErrors(ex);
            view.showError(ex.getMessage());
        } catch (SQLException ex) {
            view.showError("Ошибка БД: " + ex.getMessage());
        }
    }

    public void onDelete(Contact selected) {
        if (selected == null) { view.setStatus("Выберите контакт для удаления."); return; }
        if (!view.confirmDelete(selected)) return;

        try {
            service.delete(selected.getId());
            refresh();
            view.setStatus("Контакт «" + selected + "» удалён.");
        } catch (SQLException ex) {
            view.showError("Ошибка БД: " + ex.getMessage());
        }
    }

    public void onSearch(String query) {
        currentQuery = query == null ? "" : query.trim();
        refresh();
        if (currentQuery.isBlank()) view.setStatus("Фильтр сброшен.");
        else view.setStatus("Поиск: «" + currentQuery + "»");
    }

    // ── Вспомогательные ───────────────────────────────────────────────────────

    private void refresh() {
        try {
            var list = service.search(currentQuery);
            view.setContacts(list);
        } catch (SQLException ex) {
            view.showError("Не удалось загрузить данные: " + ex.getMessage());
        }
    }
}
