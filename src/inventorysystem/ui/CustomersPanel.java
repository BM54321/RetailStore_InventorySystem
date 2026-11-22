package inventorysystem.ui;

import inventorysystem.dao.CustomerDAO;
import inventorysystem.model.Customer;
import inventorysystem.model.Person;
import java.awt.Color;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * Customers screen: search the customer list and add, update or delete customers.
 * "View Details" shows the customer through a Person reference (polymorphism).
 * The layout is built with the NetBeans GUI builder (Design tab); this file holds the logic.
 */
public class CustomersPanel extends javax.swing.JPanel implements Refreshable {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MMM d, yyyy");

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final DefaultTableModel customerModel = Theme.readOnlyModel("Name", "Phone", "Email", "Registered");
    private List<Customer> shown = new ArrayList<>();

    public CustomersPanel() {
        initComponents();
        styleScreen();
        Theme.columnWidths(customerTable, 130, 100, 150, 90);
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
            shown = customerDAO.search(searchField.getText());
            customerModel.setRowCount(0);
            for (Customer c : shown) {
                customerModel.addRow(new Object[] {
                    c.getName(), nz(c.getPhone()), nz(c.getEmail()),
                    c.getRegisteredDate() == null ? "" : DATE.format(c.getRegisteredDate())
                });
            }
            statusLabel.setText(" ");
        } catch (SQLException ex) {
            statusLabel.setText("Database problem: " + ex.getMessage());
        }
    }

    private void showSelected() {
        int row = customerTable.getSelectedRow();
        if (row < 0) {
            setEditing(false);
            return;
        }
        Customer c = shown.get(customerTable.convertRowIndexToModel(row));
        nameField.setText(c.getName());
        phoneField.setText(nz(c.getPhone()));
        emailField.setText(nz(c.getEmail()));
        addressField.setText(nz(c.getAddress()));
        setEditing(true);
    }

    private void clearForm() {
        customerTable.clearSelection();
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        setEditing(false);
        nameField.requestFocusInWindow();
    }

    private Customer readForm() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();
        if (name.isEmpty()) {
            warn("Customer name is required.");
            return null;
        }
        if (name.length() > 100 || phone.length() > 30 || email.length() > 100 || address.length() > 200) {
            warn("One of the entries is too long. (Name: 100, phone: 30, email: 100, address: 200)");
            return null;
        }
        if (!email.isEmpty() && !email.contains("@")) {
            warn("The email address should contain an @.");
            return null;
        }
        return new Customer(0, name, phone, email, address, null);
    }

    private void add() {
        Customer c = readForm();
        if (c == null) {
            return;
        }
        try {
            customerDAO.insert(c);
            load();
            clearForm();
            info("Customer added.");
        } catch (SQLException ex) {
            warn(DbMessages.friendly(ex, "", ""));
        }
    }

    private void update() {
        int row = customerTable.getSelectedRow();
        Customer c = readForm();
        if (row < 0 || c == null) {
            return;
        }
        c.setId(shown.get(customerTable.convertRowIndexToModel(row)).getId());
        try {
            customerDAO.update(c);
            load();
            clearForm();
            info("Customer updated.");
        } catch (SQLException ex) {
            warn(DbMessages.friendly(ex, "", ""));
        }
    }

    private void delete() {
        int row = customerTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        Customer c = shown.get(customerTable.convertRowIndexToModel(row));
        if (!confirmDelete(c.getName() + " (their past sales will show as Walk-in)")) {
            return;
        }
        try {
            customerDAO.delete(c.getId());
            load();
            clearForm();
            info("Customer deleted.");
        } catch (SQLException ex) {
            warn(DbMessages.friendly(ex, "", ""));
        }
    }

    /** POLYMORPHISM: the customer is used as a Person, and displayInfo() runs Customer's own version. */
    private void showDetails() {
        int row = customerTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        Person person = shown.get(customerTable.convertRowIndexToModel(row));
        JOptionPane.showMessageDialog(this, person.displayInfo(), "Customer details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void styleScreen() {
        setOpaque(false);
        Theme.styleTable(customerTable);
        customerTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        customerScroll.setBorder(BorderFactory.createEmptyBorder());
        customerScroll.getViewport().setBackground(Color.WHITE);
        addButton.setOpaque(true);
        addButton.setBorderPainted(false);
        addButton.setFocusPainted(false);
        customerTable.getSelectionModel().addListSelectionListener(e -> {
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
        detailsButton.addActionListener(e -> showDetails());
        setEditing(false);
    }

    /** While a row is selected you can only Update or Delete it; press Clear / New to add another. */
    private void setEditing(boolean editing) {
        addButton.setEnabled(!editing);
        detailsButton.setEnabled(editing);
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
        customerScroll = new javax.swing.JScrollPane();
        customerTable = new javax.swing.JTable();
        statusLabel = new javax.swing.JLabel();
        formCard = new inventorysystem.SmoothEdges();
        formHeading = new javax.swing.JLabel();
        nameFieldLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
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
        detailsButton = new javax.swing.JButton();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listCard.setBackground(new java.awt.Color(255, 255, 255));
        listCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        listHeading.setForeground(new java.awt.Color(23, 43, 77));
        listHeading.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        listHeading.setText("Customers");

        listCard.add(listHeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 12, 400, 24));
        searchField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        listCard.add(searchField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 46, 528, 32));
        customerTable.setModel(customerModel);

        customerScroll.setViewportView(customerTable);

        listCard.add(customerScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 88, 528, 478));
        statusLabel.setForeground(new java.awt.Color(220, 53, 69));
        statusLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        statusLabel.setText(" ");

        listCard.add(statusLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 572, 528, 20));

        this.add(listCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 560, 600));

        formCard.setBackground(new java.awt.Color(255, 255, 255));
        formCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        formHeading.setForeground(new java.awt.Color(23, 43, 77));
        formHeading.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        formHeading.setText("Customer Details");

        formCard.add(formHeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 12, 290, 24));
        nameFieldLabel.setForeground(new java.awt.Color(120, 130, 145));
        nameFieldLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        nameFieldLabel.setText("Customer name");

        formCard.add(nameFieldLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 46, 290, 18));
        nameField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        formCard.add(nameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 66, 292, 30));
        phoneFieldLabel.setForeground(new java.awt.Color(120, 130, 145));
        phoneFieldLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        phoneFieldLabel.setText("Phone (optional)");

        formCard.add(phoneFieldLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 108, 290, 18));
        phoneField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        formCard.add(phoneField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 128, 292, 30));
        emailFieldLabel.setForeground(new java.awt.Color(120, 130, 145));
        emailFieldLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        emailFieldLabel.setText("Email (optional)");

        formCard.add(emailFieldLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 170, 290, 18));
        emailField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        formCard.add(emailField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 190, 292, 30));
        addressFieldLabel.setForeground(new java.awt.Color(120, 130, 145));
        addressFieldLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        addressFieldLabel.setText("Address (optional)");

        formCard.add(addressFieldLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 232, 290, 18));
        addressField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        formCard.add(addressField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 252, 292, 30));
        addButton.setBackground(new java.awt.Color(13, 148, 136));
        addButton.setForeground(new java.awt.Color(255, 255, 255));
        addButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        addButton.setText("Add Customer");

        formCard.add(addButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 304, 142, 36));
        updateButton.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        updateButton.setText("Update");

        formCard.add(updateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(166, 304, 142, 36));
        deleteButton.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        deleteButton.setText("Delete");

        formCard.add(deleteButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 348, 142, 36));
        clearButton.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        clearButton.setText("Clear / New");

        formCard.add(clearButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(166, 348, 142, 36));
        detailsButton.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        detailsButton.setText("View Details");

        formCard.add(detailsButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 392, 292, 36));

        this.add(formCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(576, 0, 324, 600));

    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JTextField addressField;
    private javax.swing.JLabel addressFieldLabel;
    private javax.swing.JButton clearButton;
    private javax.swing.JScrollPane customerScroll;
    private javax.swing.JTable customerTable;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton detailsButton;
    private javax.swing.JTextField emailField;
    private javax.swing.JLabel emailFieldLabel;
    private javax.swing.JPanel formCard;
    private javax.swing.JLabel formHeading;
    private javax.swing.JPanel listCard;
    private javax.swing.JLabel listHeading;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameFieldLabel;
    private javax.swing.JTextField phoneField;
    private javax.swing.JLabel phoneFieldLabel;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}
