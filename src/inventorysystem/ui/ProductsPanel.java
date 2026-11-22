package inventorysystem.ui;

import inventorysystem.dao.CategoryDAO;
import inventorysystem.dao.ProductDAO;
import inventorysystem.dao.SupplierDAO;
import inventorysystem.model.Category;
import inventorysystem.model.Product;
import inventorysystem.model.Supplier;
import java.awt.Color;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * Products screen: search the product list and add, update or delete products.
 * The layout is built with the NetBeans GUI builder (Design tab); this file holds the logic.
 */
public class ProductsPanel extends javax.swing.JPanel implements Refreshable {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();

    // Created here and attached to the table in the designer
    private final DefaultTableModel productModel =
            Theme.readOnlyModel("Code", "Product", "Category", "Price", "Stock");
    private List<Product> shown = new ArrayList<>();

    public ProductsPanel() {
        initComponents();
        styleScreen();
        Theme.alignRight(productTable, 3, 4);
        Theme.columnWidths(productTable, 70, 170, 110, 80, 50);
    }

    @Override
    public void refresh() {
        statusLabel.setText(" ");
        loadCombos();
        load();
    }

    private void loadCombos() {
        try {
            Category selectedCat = (Category) categoryCombo.getSelectedItem();
            Supplier selectedSup = (Supplier) supplierCombo.getSelectedItem();
            categoryCombo.removeAllItems();
            for (Category c : categoryDAO.search("")) {
                categoryCombo.addItem(c);
                if (selectedCat != null && selectedCat.getId() == c.getId()) {
                    categoryCombo.setSelectedItem(c);
                }
            }
            supplierCombo.removeAllItems();
            for (Supplier s : supplierDAO.search("")) {
                supplierCombo.addItem(s);
                if (selectedSup != null && selectedSup.getId() == s.getId()) {
                    supplierCombo.setSelectedItem(s);
                }
            }
        } catch (SQLException ex) {
            statusLabel.setText("Database problem: " + ex.getMessage());
        }
    }

    private void load() {
        try {
            shown = productDAO.search(searchField.getText());
            productModel.setRowCount(0);
            for (Product p : shown) {
                productModel.addRow(new Object[] {
                    p.getCode(), p.getName(), p.getCategoryName(), Theme.money(p.getPrice()), p.getStockQuantity()
                });
            }
            statusLabel.setText(" ");
        } catch (SQLException ex) {
            statusLabel.setText("Database problem: " + ex.getMessage());
        }
    }

    private void showSelected() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            setEditing(false);
            return;
        }
        Product p = shown.get(productTable.convertRowIndexToModel(row));
        codeField.setText(p.getCode());
        nameField.setText(p.getName());
        priceField.setText(p.getPrice().toPlainString());
        stockField.setText(String.valueOf(p.getStockQuantity()));
        minStockField.setText(String.valueOf(p.getMinStockLevel()));
        for (int i = 0; i < categoryCombo.getItemCount(); i++) {
            if (categoryCombo.getItemAt(i).getId() == p.getCategoryId()) {
                categoryCombo.setSelectedIndex(i);
            }
        }
        for (int i = 0; i < supplierCombo.getItemCount(); i++) {
            if (supplierCombo.getItemAt(i).getId() == p.getSupplierId()) {
                supplierCombo.setSelectedIndex(i);
            }
        }
        setEditing(true);
    }

    private void clearForm() {
        productTable.clearSelection();
        codeField.setText("");
        nameField.setText("");
        priceField.setText("");
        stockField.setText("");
        minStockField.setText("");
        if (categoryCombo.getItemCount() > 0) {
            categoryCombo.setSelectedIndex(0);
        }
        if (supplierCombo.getItemCount() > 0) {
            supplierCombo.setSelectedIndex(0);
        }
        setEditing(false);
        codeField.requestFocusInWindow();
    }

    /** Reads and checks the form. Returns null (after showing a message) if something is wrong. */
    private Product readForm() {
        String code = codeField.getText().trim();
        String name = nameField.getText().trim();
        if (code.isEmpty() || name.isEmpty()) {
            warn("Product code and product name are required.");
            return null;
        }
        if (code.length() > 30 || name.length() > 100) {
            warn("Product code can be 30 characters at most, and the name 100.");
            return null;
        }
        Category cat = (Category) categoryCombo.getSelectedItem();
        Supplier sup = (Supplier) supplierCombo.getSelectedItem();
        if (cat == null || sup == null) {
            warn("Add a category and a supplier first (Categories and Suppliers screens).");
            return null;
        }
        BigDecimal price;
        try {
            price = new BigDecimal(priceField.getText().trim());
        } catch (NumberFormatException ex) {
            warn("Price must be a number, for example 25.50.");
            return null;
        }
        if (price.signum() < 0 || price.scale() > 2 || price.compareTo(new BigDecimal("99999999.99")) > 0) {
            warn("Price must be 0 or more, with at most 2 decimal places.");
            return null;
        }
        int stock = parseWhole(stockField, "Stock quantity");
        int min = parseWhole(minStockField, "Minimum stock level");
        if (stock < 0 || min < 0) {
            return null;
        }
        Product p = new Product();
        p.setCode(code);
        p.setName(name);
        p.setCategoryId(cat.getId());
        p.setSupplierId(sup.getId());
        p.setPrice(price);
        p.setStockQuantity(stock);
        p.setMinStockLevel(min);
        return p;
    }

    /** Returns the number, or -1 after showing a message. */
    private int parseWhole(JTextField field, String label) {
        try {
            int value = Integer.parseInt(field.getText().trim());
            if (value >= 0) {
                return value;
            }
        } catch (NumberFormatException ignored) {
            // falls through to the message below
        }
        warn(label + " must be a whole number, 0 or more.");
        return -1;
    }

    private void add() {
        Product p = readForm();
        if (p == null) {
            return;
        }
        try {
            productDAO.insert(p);
            load();
            clearForm();
            info("Product added.");
        } catch (SQLException ex) {
            warn(DbMessages.friendly(ex, "That product code already exists.", ""));
        }
    }

    private void update() {
        int row = productTable.getSelectedRow();
        Product p = readForm();
        if (row < 0 || p == null) {
            return;
        }
        p.setId(shown.get(productTable.convertRowIndexToModel(row)).getId());
        try {
            productDAO.update(p);
            load();
            clearForm();
            info("Product updated.");
        } catch (SQLException ex) {
            warn(DbMessages.friendly(ex, "That product code already exists.", ""));
        }
    }

    private void delete() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        Product p = shown.get(productTable.convertRowIndexToModel(row));
        if (!confirmDelete(p.getName())) {
            return;
        }
        try {
            productDAO.delete(p.getId());
            load();
            clearForm();
            info("Product deleted.");
        } catch (SQLException ex) {
            warn(DbMessages.friendly(ex, "",
                    "This product already appears in sales records, so it cannot be deleted."));
        }
    }

    private void styleScreen() {
        setOpaque(false);
        Theme.styleTable(productTable);
        productTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        productScroll.setBorder(BorderFactory.createEmptyBorder());
        productScroll.getViewport().setBackground(Color.WHITE);
        addButton.setOpaque(true);
        addButton.setBorderPainted(false);
        addButton.setFocusPainted(false);
        productTable.getSelectionModel().addListSelectionListener(e -> {
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
        productScroll = new javax.swing.JScrollPane();
        productTable = new javax.swing.JTable();
        statusLabel = new javax.swing.JLabel();
        formCard = new inventorysystem.SmoothEdges();
        formHeading = new javax.swing.JLabel();
        codeFieldLabel = new javax.swing.JLabel();
        codeField = new javax.swing.JTextField();
        nameFieldLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        categoryComboLabel = new javax.swing.JLabel();
        categoryCombo = new javax.swing.JComboBox<>();
        supplierComboLabel = new javax.swing.JLabel();
        supplierCombo = new javax.swing.JComboBox<>();
        priceFieldLabel = new javax.swing.JLabel();
        priceField = new javax.swing.JTextField();
        stockFieldLabel = new javax.swing.JLabel();
        stockField = new javax.swing.JTextField();
        minStockFieldLabel = new javax.swing.JLabel();
        minStockField = new javax.swing.JTextField();
        addButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listCard.setBackground(new java.awt.Color(255, 255, 255));
        listCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        listHeading.setForeground(new java.awt.Color(23, 43, 77));
        listHeading.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        listHeading.setText("Products");

        listCard.add(listHeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 12, 400, 24));
        searchField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        listCard.add(searchField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 46, 528, 32));
        productTable.setModel(productModel);

        productScroll.setViewportView(productTable);

        listCard.add(productScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 88, 528, 478));
        statusLabel.setForeground(new java.awt.Color(220, 53, 69));
        statusLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        statusLabel.setText(" ");

        listCard.add(statusLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 572, 528, 20));

        this.add(listCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 560, 600));

        formCard.setBackground(new java.awt.Color(255, 255, 255));
        formCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        formHeading.setForeground(new java.awt.Color(23, 43, 77));
        formHeading.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        formHeading.setText("Product Details");

        formCard.add(formHeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 12, 290, 24));
        codeFieldLabel.setForeground(new java.awt.Color(120, 130, 145));
        codeFieldLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        codeFieldLabel.setText("Product code");

        formCard.add(codeFieldLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 46, 290, 18));
        codeField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        formCard.add(codeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 66, 292, 30));
        nameFieldLabel.setForeground(new java.awt.Color(120, 130, 145));
        nameFieldLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        nameFieldLabel.setText("Product name");

        formCard.add(nameFieldLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 108, 290, 18));
        nameField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        formCard.add(nameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 128, 292, 30));
        categoryComboLabel.setForeground(new java.awt.Color(120, 130, 145));
        categoryComboLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        categoryComboLabel.setText("Category");

        formCard.add(categoryComboLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 170, 290, 18));
        categoryCombo.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        formCard.add(categoryCombo, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 190, 292, 30));
        supplierComboLabel.setForeground(new java.awt.Color(120, 130, 145));
        supplierComboLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        supplierComboLabel.setText("Supplier");

        formCard.add(supplierComboLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 232, 290, 18));
        supplierCombo.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        formCard.add(supplierCombo, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 252, 292, 30));
        priceFieldLabel.setForeground(new java.awt.Color(120, 130, 145));
        priceFieldLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        priceFieldLabel.setText("Price (₱)");

        formCard.add(priceFieldLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 294, 290, 18));
        priceField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        formCard.add(priceField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 314, 292, 30));
        stockFieldLabel.setForeground(new java.awt.Color(120, 130, 145));
        stockFieldLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        stockFieldLabel.setText("Stock quantity");

        formCard.add(stockFieldLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 356, 290, 18));
        stockField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        formCard.add(stockField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 376, 292, 30));
        minStockFieldLabel.setForeground(new java.awt.Color(120, 130, 145));
        minStockFieldLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        minStockFieldLabel.setText("Minimum stock level");

        formCard.add(minStockFieldLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 418, 290, 18));
        minStockField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        formCard.add(minStockField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 438, 292, 30));
        addButton.setBackground(new java.awt.Color(13, 148, 136));
        addButton.setForeground(new java.awt.Color(255, 255, 255));
        addButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        addButton.setText("Add Product");

        formCard.add(addButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 490, 142, 36));
        updateButton.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        updateButton.setText("Update");

        formCard.add(updateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(166, 490, 142, 36));
        deleteButton.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        deleteButton.setText("Delete");

        formCard.add(deleteButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 534, 142, 36));
        clearButton.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        clearButton.setText("Clear / New");

        formCard.add(clearButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(166, 534, 142, 36));

        this.add(formCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(576, 0, 324, 600));

    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JComboBox<inventorysystem.model.Category> categoryCombo;
    private javax.swing.JLabel categoryComboLabel;
    private javax.swing.JButton clearButton;
    private javax.swing.JTextField codeField;
    private javax.swing.JLabel codeFieldLabel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JPanel formCard;
    private javax.swing.JLabel formHeading;
    private javax.swing.JPanel listCard;
    private javax.swing.JLabel listHeading;
    private javax.swing.JTextField minStockField;
    private javax.swing.JLabel minStockFieldLabel;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameFieldLabel;
    private javax.swing.JTextField priceField;
    private javax.swing.JLabel priceFieldLabel;
    private javax.swing.JScrollPane productScroll;
    private javax.swing.JTable productTable;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JTextField stockField;
    private javax.swing.JLabel stockFieldLabel;
    private javax.swing.JComboBox<inventorysystem.model.Supplier> supplierCombo;
    private javax.swing.JLabel supplierComboLabel;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}
