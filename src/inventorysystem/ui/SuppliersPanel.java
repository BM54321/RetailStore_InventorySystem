package inventorysystem.ui;

import inventorysystem.dao.SupplierDAO;
import inventorysystem.model.Supplier;
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
 * Suppliers screen: search the supplier list and add, update or delete suppliers.
 * The layout is built with the NetBeans GUI builder (Design tab); this file holds the logic.
 */
public class SuppliersPanel extends javax.swing.JPanel implements Refreshable {

    private final SupplierDAO supplierDAO = new SupplierDAO();
    private final DefaultTableModel supplierModel = Theme.readOnlyModel("Company", "Contact", "Phone", "Email");
    private List<Supplier> shown = new ArrayList<>();

    public SuppliersPanel() {
        initComponents();
        styleScreen();
        Theme.columnWidths(supplierTable, 150, 110, 100, 150);
    }

    @Override
    public void refresh() {
        statusLabel.setText(" ");
        load();
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }

    private void load() {
        try {
            shown = supplierDAO.search(searchField.getText());
            supplierModel.setRowCount(0);
            for (Supplier s : shown) {
                supplierModel.addRow(new Object[] { s.getCompanyName(), s.getName(), nz(s.getPhone()), nz(s.getEmail()) });
            }
            statusLabel.setText(" ");
        } catch (SQLException ex) {
            statusLabel.setText("Database problem: " + ex.getMessage());
        }
    }

    private void showSelected() {
        int row = supplierTable.getSelectedRow();
        if (row < 0) {
            setEditing(false);
            return;
        }
        Supplier s = shown.get(supplierTable.convertRowIndexToModel(row));
        companyField.setText(s.getCompanyName());
        contactField.setText(s.getName());
        phoneField.setText(nz(s.getPhone()));
        emailField.setText(nz(s.getEmail()));
        addressField.setText(nz(s.getAddress()));
        setEditing(true);
    }

    private void clearForm() {
        supplierTable.clearSelection();
        companyField.setText("");
        contactField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        setEditing(false);
        companyField.requestFocusInWindow();
    }

    private Supplier readForm() {
        String company = companyField.getText().trim();
        String contact = contactField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();
        if (company.isEmpty() || contact.isEmpty()) {
            warn("Company name and contact person are required.");
            return null;
        }
        if (company.length() > 100 || contact.length() > 100 || phone.length() > 30
                || email.length() > 100 || address.length() > 200) {
            warn("One of the entries is too long. (Company and contact: 100, phone: 30, email: 100, address: 200)");
            return null;
        }
        if (!email.isEmpty() && !email.contains("@")) {
            warn("The email address should contain an @.");
            return null;
        }
        return new Supplier(0, company, contact, phone, email, address);
    }

    private void add() {
        Supplier s = readForm();
        if (s == null) {
            return;
        }
        try {
            supplierDAO.insert(s);
            load();
            clearForm();
            info("Supplier added.");
        } catch (SQLException ex) {
            warn(DbMessages.friendly(ex, "A supplier with that company name already exists.", ""));
        }
    }

    private void update() {
        int row = supplierTable.getSelectedRow();
        Supplier s = readForm();
        if (row < 0 || s == null) {
            return;
        }
        s.setId(shown.get(supplierTable.convertRowIndexToModel(row)).getId());
        try {
            supplierDAO.update(s);
            load();
            clearForm();
            info("Supplier updated.");
        } catch (SQLException ex) {
            warn(DbMessages.friendly(ex, "A supplier with that company name already exists.", ""));
        }
    }

    private void delete() {
        int row = supplierTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        Supplier s = shown.get(supplierTable.convertRowIndexToModel(row));
        if (!confirmDelete(s.getCompanyName())) {
            return;
        }
        try {
            supplierDAO.delete(s.getId());
            load();
            clearForm();
            info("Supplier deleted.");
        } catch (SQLException ex) {
            warn(DbMessages.friendly(ex, "",
                    "This supplier still has products. Move or delete those products first."));
        }
    }

    private void styleScreen() {
        setOpaque(false);
        Theme.styleTable(supplierTable);
        supplierTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        supplierScroll.setBorder(BorderFactory.createEmptyBorder());
        supplierScroll.getViewport().setBackground(Color.WHITE);
        addButton.setOpaque(true);
        addButton.setBorderPainted(false);
        addButton.setFocusPainted(false);
        supplierTable.getSelectionModel().addListSelectionListener(e -> {
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
        supplierScroll = new javax.swing.JScrollPane();
        supplierTable = new javax.swing.JTable();
        statusLabel = new javax.swing.JLabel();
        formCard = new inventorysystem.SmoothEdges();
        formHeading = new javax.swing.JLabel();
        companyFieldLabel = new javax.swing.JLabel();
        companyField = new javax.swing.JTextField();
        contactFieldLabel = new javax.swing.JLabel();
        contactField = new javax.swing.JTextField();
        phoneFieldLabel = new javax.swing.JLabel();
        phoneField = new javax.swing.JTextField();
        emailFieldLabel = new javax.swing.JLabel();
        emailField = new javax.swing.JTextField();
        addressFieldLabel = new javax.swing.JLabel();
        addressField = new javax.swing.JTextField();
        addButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listCard.setBackground(new java.awt.Color(255, 255, 255));
        listCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        listHeading.setForeground(new java.awt.Color(23, 43, 77));
        listHeading.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        listHeading.setText("Suppliers");

        listCard.add(listHeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 12, 400, 24));
        searchField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        listCard.add(searchField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 46, 528, 32));
        supplierTable.setModel(supplierModel);

        supplierScroll.setViewportView(supplierTable);

        listCard.add(supplierScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 88, 528, 478));
        statusLabel.setForeground(new java.awt.Color(220, 53, 69));
        statusLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        statusLabel.setText(" ");

        listCard.add(statusLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 572, 528, 20));

        this.add(listCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 560, 600));

        formCard.setBackground(new java.awt.Color(255, 255, 255));
        formCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        formHeading.setForeground(new java.awt.Color(23, 43, 77));
        formHeading.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        formHeading.setText("Supplier Details");

        formCard.add(formHeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 12, 290, 24));
        companyFieldLabel.setForeground(new java.awt.Color(120, 130, 145));
        companyFieldLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        companyFieldLabel.setText("Company name");

        formCard.add(companyFieldLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 46, 290, 18));
        companyField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        formCard.add(companyField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 66, 292, 30));
        contactFieldLabel.setForeground(new java.awt.Color(120, 130, 145));
        contactFieldLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        contactFieldLabel.setText("Contact person");

        formCard.add(contactFieldLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 108, 290, 18));
        contactField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        formCard.add(contactField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 128, 292, 30));
        phoneFieldLabel.setForeground(new java.awt.Color(120, 130, 145));
        phoneFieldLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        phoneFieldLabel.setText("Phone (optional)");

        formCard.add(phoneFieldLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 170, 290, 18));
        phoneField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        formCard.add(phoneField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 190, 292, 30));
        emailFieldLabel.setForeground(new java.awt.Color(120, 130, 145));
        emailFieldLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        emailFieldLabel.setText("Email (optional)");

        formCard.add(emailFieldLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 232, 290, 18));
        emailField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        formCard.add(emailField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 252, 292, 30));
        addressFieldLabel.setForeground(new java.awt.Color(120, 130, 145));
        addressFieldLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        addressFieldLabel.setText("Address (optional)");

        formCard.add(addressFieldLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 294, 290, 18));
        addressField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        formCard.add(addressField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 314, 292, 30));
        addButton.setBackground(new java.awt.Color(13, 148, 136));
        addButton.setForeground(new java.awt.Color(255, 255, 255));
        addButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        addButton.setText("Add Supplier");

        formCard.add(addButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 366, 142, 36));
        updateButton.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        updateButton.setText("Update");

        formCard.add(updateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(166, 366, 142, 36));
        deleteButton.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        deleteButton.setText("Delete");

        formCard.add(deleteButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 410, 142, 36));
        clearButton.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        clearButton.setText("Clear / New");

        formCard.add(clearButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(166, 410, 142, 36));

        this.add(formCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(576, 0, 324, 600));

    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JTextField addressField;
    private javax.swing.JLabel addressFieldLabel;
    private javax.swing.JButton clearButton;
    private javax.swing.JTextField companyField;
    private javax.swing.JLabel companyFieldLabel;
    private javax.swing.JTextField contactField;
    private javax.swing.JLabel contactFieldLabel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextField emailField;
    private javax.swing.JLabel emailFieldLabel;
    private javax.swing.JPanel formCard;
    private javax.swing.JLabel formHeading;
    private javax.swing.JPanel listCard;
    private javax.swing.JLabel listHeading;
    private javax.swing.JTextField phoneField;
    private javax.swing.JLabel phoneFieldLabel;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JScrollPane supplierScroll;
    private javax.swing.JTable supplierTable;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}
