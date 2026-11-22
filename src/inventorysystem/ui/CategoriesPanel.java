package inventorysystem.ui;

import inventorysystem.dao.CategoryDAO;
import inventorysystem.model.Category;
import java.awt.Color;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * Categories screen: search the category list and add, update or delete categories.
 * The layout is built with the NetBeans GUI builder (Design tab); this file holds the logic.
 */
public class CategoriesPanel extends javax.swing.JPanel implements Refreshable {

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final DefaultTableModel categoryModel = Theme.readOnlyModel("Category", "Description");
    private List<Category> shown = new ArrayList<>();

    public CategoriesPanel() {
        initComponents();
        styleScreen();
        Theme.columnWidths(categoryTable, 150, 330);
    }

    @Override
    public void refresh() {
        statusLabel.setText(" ");
        load();
    }

    private void load() {
        try {
            shown = categoryDAO.search(searchField.getText());
            categoryModel.setRowCount(0);
            for (Category c : shown) {
                categoryModel.addRow(new Object[] { c.getName(), c.getDescription() == null ? "" : c.getDescription() });
            }
            statusLabel.setText(" ");
        } catch (SQLException ex) {
            statusLabel.setText("Database problem: " + ex.getMessage());
        }
    }

    private void showSelected() {
        int row = categoryTable.getSelectedRow();
        if (row < 0) {
            setEditing(false);
            return;
        }
        Category c = shown.get(categoryTable.convertRowIndexToModel(row));
        nameField.setText(c.getName());
        descriptionField.setText(c.getDescription() == null ? "" : c.getDescription());
        setEditing(true);
    }

    private void clearForm() {
        categoryTable.clearSelection();
        nameField.setText("");
        descriptionField.setText("");
        setEditing(false);
        nameField.requestFocusInWindow();
    }

    private Category readForm() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        if (name.isEmpty()) {
            warn("Category name is required.");
            return null;
        }
        if (name.length() > 60 || description.length() > 200) {
            warn("The name can be 60 characters at most, and the description 200.");
            return null;
        }
        return new Category(0, name, description);
    }

    private void add() {
        Category c = readForm();
        if (c == null) {
            return;
        }
        try {
            categoryDAO.insert(c);
            load();
            clearForm();
            info("Category added.");
        } catch (SQLException ex) {
            warn(DbMessages.friendly(ex, "That category already exists.", ""));
        }
    }

    private void update() {
        int row = categoryTable.getSelectedRow();
        Category c = readForm();
        if (row < 0 || c == null) {
            return;
        }
        c.setId(shown.get(categoryTable.convertRowIndexToModel(row)).getId());
        try {
            categoryDAO.update(c);
            load();
            clearForm();
            info("Category updated.");
        } catch (SQLException ex) {
            warn(DbMessages.friendly(ex, "That category already exists.", ""));
        }
    }

    private void delete() {
        int row = categoryTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        Category c = shown.get(categoryTable.convertRowIndexToModel(row));
        if (!confirmDelete(c.getName())) {
            return;
        }
        try {
            categoryDAO.delete(c.getId());
            load();
            clearForm();
            info("Category deleted.");
        } catch (SQLException ex) {
            warn(DbMessages.friendly(ex, "",
                    "This category still has products. Move or delete those products first."));
        }
    }

    private void styleScreen() {
        setOpaque(false);
        Theme.styleTable(categoryTable);
        categoryTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        categoryScroll.setBorder(BorderFactory.createEmptyBorder());
        categoryScroll.getViewport().setBackground(Color.WHITE);
        addButton.setOpaque(true);
        addButton.setBorderPainted(false);
        addButton.setFocusPainted(false);
        categoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showSelected();
            }
        });
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { load(); }
            @Override public void removeUpdate(DocumentEvent e) { load(); }
            @Override public void changedUpdate(DocumentEvent e) { load(); }
        });
        addButton.addActionListener(e -> add());
        updateButton.addActionListener(e -> update());
        deleteButton.addActionListener(e -> delete());
        clearButton.addActionListener(e -> clearForm());
        setEditing(false);
    }

    /** While a row is selected you can only Update or Delete it; press Clear / New to add another. */
    private void setEditing(boolean editing) {
        addButton.setEnabled(!editing);
        updateButton.setEnabled(editing);
        deleteButton.setEnabled(editing);
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Please check", JOptionPane.WARNING_MESSAGE);
    }

    private void info(String message) {
        JOptionPane.showMessageDialog(this, message, "Done", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean confirmDelete(String what) {
        return JOptionPane.showConfirmDialog(this, "Delete " + what + "?", "Confirm delete",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        listCard = new inventorysystem.SmoothEdges();
        listHeading = new javax.swing.JLabel();
        searchField = new javax.swing.JTextField();
        categoryScroll = new javax.swing.JScrollPane();
        categoryTable = new javax.swing.JTable();
        statusLabel = new javax.swing.JLabel();
        formCard = new inventorysystem.SmoothEdges();
        formHeading = new javax.swing.JLabel();
        nameFieldLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        descriptionFieldLabel = new javax.swing.JLabel();
        descriptionField = new javax.swing.JTextField();
        addButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listCard.setBackground(new java.awt.Color(255, 255, 255));
        listCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        listHeading.setForeground(new java.awt.Color(23, 43, 77));
        listHeading.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        listHeading.setText("Categories");

        listCard.add(listHeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 12, 400, 24));
        searchField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        listCard.add(searchField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 46, 528, 32));
        categoryTable.setModel(categoryModel);

        categoryScroll.setViewportView(categoryTable);

        listCard.add(categoryScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 88, 528, 478));
        statusLabel.setForeground(new java.awt.Color(220, 53, 69));
        statusLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        statusLabel.setText(" ");

        listCard.add(statusLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 572, 528, 20));

        this.add(listCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 560, 600));

        formCard.setBackground(new java.awt.Color(255, 255, 255));
        formCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        formHeading.setForeground(new java.awt.Color(23, 43, 77));
        formHeading.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        formHeading.setText("Category Details");

        formCard.add(formHeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 12, 290, 24));
        nameFieldLabel.setForeground(new java.awt.Color(120, 130, 145));
        nameFieldLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        nameFieldLabel.setText("Category name");

        formCard.add(nameFieldLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 46, 290, 18));
        nameField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        formCard.add(nameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 66, 292, 30));
        descriptionFieldLabel.setForeground(new java.awt.Color(120, 130, 145));
        descriptionFieldLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        descriptionFieldLabel.setText("Description (optional)");

        formCard.add(descriptionFieldLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 108, 290, 18));
        descriptionField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        formCard.add(descriptionField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 128, 292, 30));
        addButton.setBackground(new java.awt.Color(13, 148, 136));
        addButton.setForeground(new java.awt.Color(255, 255, 255));
        addButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        addButton.setText("Add Category");

        formCard.add(addButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 180, 142, 36));
        updateButton.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        updateButton.setText("Update");

        formCard.add(updateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(166, 180, 142, 36));
        deleteButton.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        deleteButton.setText("Delete");

        formCard.add(deleteButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 224, 142, 36));
        clearButton.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        clearButton.setText("Clear / New");

        formCard.add(clearButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(166, 224, 142, 36));

        this.add(formCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(576, 0, 324, 600));

    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JScrollPane categoryScroll;
    private javax.swing.JTable categoryTable;
    private javax.swing.JButton clearButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextField descriptionField;
    private javax.swing.JLabel descriptionFieldLabel;
    private javax.swing.JPanel formCard;
    private javax.swing.JLabel formHeading;
    private javax.swing.JPanel listCard;
    private javax.swing.JLabel listHeading;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameFieldLabel;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}
