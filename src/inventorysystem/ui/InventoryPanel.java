package inventorysystem.ui;

import inventorysystem.dao.ProductDAO;
import inventorysystem.model.Product;
import java.awt.Color;
import java.awt.Component;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Inventory screen: see the stock level of every product and restock it.
 * Uses Product.updateStock() to check the rule (stock never below zero) and
 * ProductDAO.adjustStock() to save the new stock in the database.
 * The layout is built with the NetBeans GUI builder (Design tab); this file holds the logic.
 */
public class InventoryPanel extends javax.swing.JPanel implements Refreshable {

    private static final String ALL = "All products";
    private static final String LOW = "Low stock";
    private static final String OUT = "Out of stock";

    private final ProductDAO productDAO = new ProductDAO();
    private final DefaultTableModel inventoryModel =
            Theme.readOnlyModel("Code", "Product", "Category", "Stock", "Min", "Status");
    private List<Product> shown = new ArrayList<>();
    private boolean loading;

    public InventoryPanel() {
        initComponents();
        styleScreen();
        Theme.alignRight(inventoryTable, 3, 4);
        inventoryTable.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());
        Theme.columnWidths(inventoryTable, 65, 150, 100, 45, 40, 100);
    }

    /** Colours the Status column: red = out of stock, orange = low stock, green = fine. */
    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value, boolean sel,
                                                       boolean focus, int row, int col) {
            Component c = super.getTableCellRendererComponent(t, value, sel, focus, row, col);
            String text = String.valueOf(value);
            Color color = Theme.TEAL;
            if (text.startsWith("OUT")) {
                color = Theme.DANGER;
            } else if (text.startsWith("LOW")) {
                color = Theme.WARN;
            }
            c.setForeground(color);
            c.setFont(c.getFont().deriveFont(java.awt.Font.BOLD));
            return c;
        }
    }

    @Override
    public void refresh() {
        statusLabel.setText(" ");
        load();
    }

    private void styleScreen() {
        setOpaque(false);
        Theme.styleTable(inventoryTable);
        inventoryTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        inventoryScroll.setBorder(BorderFactory.createEmptyBorder());
        inventoryScroll.getViewport().setBackground(Color.WHITE);
        restockButton.setOpaque(true);
        restockButton.setBorderPainted(false);
        restockButton.setFocusPainted(false);
        filterCombo.setModel(new DefaultComboBoxModel<>(new String[] {ALL, LOW, OUT}));

        inventoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !loading) {
                showSelected();
            }
        });
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { load(); }
            @Override public void removeUpdate(DocumentEvent e) { load(); }
            @Override public void changedUpdate(DocumentEvent e) { load(); }
        });
        filterCombo.addActionListener(e -> load());
        restockButton.addActionListener(e -> changeStock(1));
        quantityField.addActionListener(e -> changeStock(1));
        removeButton.addActionListener(e -> changeStock(-1));
        showSelected();
    }

    private Product selectedProduct() {
        int row = inventoryTable.getSelectedRow();
        return row < 0 ? null : shown.get(inventoryTable.convertRowIndexToModel(row));
    }

    private void load() {
        Product before = selectedProduct();
        int keepId = before == null ? -1 : before.getId();
        loading = true;
        try {
            String filter = (String) filterCombo.getSelectedItem();
            shown = new ArrayList<>();
            inventoryModel.setRowCount(0);
            for (Product p : productDAO.search(searchField.getText())) {
                if (LOW.equals(filter) && !p.isLowStock()) {
                    continue;
                }
                if (OUT.equals(filter) && p.getStockQuantity() != 0) {
                    continue;
                }
                shown.add(p);
                inventoryModel.addRow(new Object[] {
                    p.getCode(), p.getName(), p.getCategoryName(),
                    p.getStockQuantity(), p.getMinStockLevel(), p.getStockStatus()
                });
            }
            statusLabel.setText(" ");
        } catch (SQLException ex) {
            statusLabel.setText("Database problem: " + ex.getMessage());
        }
        for (int i = 0; i < shown.size(); i++) {
            if (shown.get(i).getId() == keepId) {
                inventoryTable.setRowSelectionInterval(i, i);
            }
        }
        loading = false;
        showSelected();
    }

    private void showSelected() {
        Product p = selectedProduct();
        boolean has = p != null;
        restockButton.setEnabled(has);
        removeButton.setEnabled(has);
        quantityField.setEnabled(has);
        if (!has) {
            selectedLabel.setText("Select a product");
            stockInfoLabel.setText("Current stock: -");
            minInfoLabel.setText("Minimum level: -");
            stateInfoLabel.setText("Status: -");
            stateInfoLabel.setForeground(Theme.TEXT);
            return;
        }
        selectedLabel.setText(p.getName());
        stockInfoLabel.setText("Current stock: " + p.getStockQuantity());
        minInfoLabel.setText("Minimum level: " + p.getMinStockLevel());
        String status = p.getStockStatus();
        stateInfoLabel.setText("Status: " + status);
        stateInfoLabel.setForeground(status.startsWith("OUT") ? Theme.DANGER
                : status.startsWith("LOW") ? Theme.WARN : Theme.TEAL);
    }

    /** direction +1 = restock (add), -1 = remove. */
    private void changeStock(int direction) {
        Product p = selectedProduct();
        if (p == null) {
            return;
        }
        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
        } catch (NumberFormatException ex) {
            quantity = 0;
        }
        if (quantity <= 0 || quantity > 100000) {
            warn("Enter a whole number between 1 and 100000 in the Quantity box.");
            return;
        }
        int change = direction * quantity;
        try {
            p.updateStock(change);                          // checks the rule: never below zero
            productDAO.adjustStock(p.getId(), change);      // saves it in the database
            String action = direction > 0 ? "Added " : "Removed ";
            quantityField.setText("");
            load();
            info(action + quantity + " - " + p.getName() + ". New stock: " + p.getStockQuantity() + ".");
        } catch (IllegalArgumentException ex) {
            warn(ex.getMessage());
        } catch (SQLException ex) {
            warn(DbMessages.friendly(ex, "", ""));
            load();
        }
    }

    private void warn(String message) {
        JOptionPane.showMessageDialog(this, message, "Please check", JOptionPane.WARNING_MESSAGE);
    }

    private void info(String message) {
        JOptionPane.showMessageDialog(this, message, "Done", JOptionPane.INFORMATION_MESSAGE);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        listCard = new inventorysystem.SmoothEdges();
        listHeading = new javax.swing.JLabel();
        searchField = new javax.swing.JTextField();
        filterCombo = new javax.swing.JComboBox<>();
        inventoryScroll = new javax.swing.JScrollPane();
        inventoryTable = new javax.swing.JTable();
        statusLabel = new javax.swing.JLabel();
        formCard = new inventorysystem.SmoothEdges();
        formHeading = new javax.swing.JLabel();
        selectedLabel = new javax.swing.JLabel();
        stockInfoLabel = new javax.swing.JLabel();
        minInfoLabel = new javax.swing.JLabel();
        stateInfoLabel = new javax.swing.JLabel();
        quantityLabel = new javax.swing.JLabel();
        quantityField = new javax.swing.JTextField();
        restockButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        hint1Label = new javax.swing.JLabel();
        hint2Label = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        listCard.setBackground(new java.awt.Color(255, 255, 255));
        listCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        listHeading.setForeground(new java.awt.Color(23, 43, 77));
        listHeading.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        listHeading.setText("Stock Levels");

        listCard.add(listHeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 12, 400, 24));
        searchField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        listCard.add(searchField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 46, 330, 32));
        filterCombo.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        listCard.add(filterCombo, new org.netbeans.lib.awtextra.AbsoluteConstraints(356, 46, 188, 32));
        inventoryTable.setModel(inventoryModel);

        inventoryScroll.setViewportView(inventoryTable);

        listCard.add(inventoryScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 88, 528, 478));
        statusLabel.setForeground(new java.awt.Color(220, 53, 69));
        statusLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        statusLabel.setText(" ");

        listCard.add(statusLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 572, 528, 20));

        this.add(listCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 560, 600));

        formCard.setBackground(new java.awt.Color(255, 255, 255));
        formCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        formHeading.setForeground(new java.awt.Color(23, 43, 77));
        formHeading.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        formHeading.setText("Update Stock");

        formCard.add(formHeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 12, 290, 24));
        selectedLabel.setForeground(new java.awt.Color(23, 43, 77));
        selectedLabel.setFont(new java.awt.Font("Arial", 1, 15)); // NOI18N
        selectedLabel.setText("Select a product");

        formCard.add(selectedLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 50, 292, 24));
        stockInfoLabel.setForeground(new java.awt.Color(35, 45, 60));
        stockInfoLabel.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        stockInfoLabel.setText("Current stock: -");

        formCard.add(stockInfoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 84, 292, 20));
        minInfoLabel.setForeground(new java.awt.Color(35, 45, 60));
        minInfoLabel.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        minInfoLabel.setText("Minimum level: -");

        formCard.add(minInfoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 108, 292, 20));
        stateInfoLabel.setForeground(new java.awt.Color(35, 45, 60));
        stateInfoLabel.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        stateInfoLabel.setText("Status: -");

        formCard.add(stateInfoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 132, 292, 20));
        quantityLabel.setForeground(new java.awt.Color(120, 130, 145));
        quantityLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        quantityLabel.setText("Quantity");

        formCard.add(quantityLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 184, 290, 18));
        quantityField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        formCard.add(quantityField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 204, 292, 30));
        restockButton.setBackground(new java.awt.Color(13, 148, 136));
        restockButton.setForeground(new java.awt.Color(255, 255, 255));
        restockButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        restockButton.setText("Restock (add stock)");

        formCard.add(restockButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 252, 292, 40));
        removeButton.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        removeButton.setText("Remove stock");

        formCard.add(removeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 300, 292, 36));
        hint1Label.setForeground(new java.awt.Color(120, 130, 145));
        hint1Label.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        hint1Label.setText("Restock adds new deliveries to the stock.");

        formCard.add(hint1Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 354, 292, 18));
        hint2Label.setForeground(new java.awt.Color(120, 130, 145));
        hint2Label.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        hint2Label.setText("Remove is for damaged or expired items.");

        formCard.add(hint2Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 374, 292, 18));

        this.add(formCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(576, 0, 324, 600));

    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> filterCombo;
    private javax.swing.JPanel formCard;
    private javax.swing.JLabel formHeading;
    private javax.swing.JLabel hint1Label;
    private javax.swing.JLabel hint2Label;
    private javax.swing.JScrollPane inventoryScroll;
    private javax.swing.JTable inventoryTable;
    private javax.swing.JPanel listCard;
    private javax.swing.JLabel listHeading;
    private javax.swing.JLabel minInfoLabel;
    private javax.swing.JTextField quantityField;
    private javax.swing.JLabel quantityLabel;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton restockButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel selectedLabel;
    private javax.swing.JLabel stateInfoLabel;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel stockInfoLabel;
    // End of variables declaration//GEN-END:variables
}
