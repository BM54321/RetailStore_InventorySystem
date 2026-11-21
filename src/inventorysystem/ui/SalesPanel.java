package inventorysystem.ui;

import inventorysystem.dao.CustomerDAO;
import inventorysystem.dao.InsufficientStockException;
import inventorysystem.dao.ProductDAO;
import inventorysystem.dao.SaleDAO;
import inventorysystem.model.Customer;
import inventorysystem.model.Product;
import inventorysystem.model.SaleDetail;
import java.awt.Color;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * Sales screen (cashier / point of sale).
 * Pick products, build a cart, choose a customer (or Walk-in) and complete the sale.
 * Completing a sale saves it and deducts the stock in one transaction (see SaleDAO).
 * The layout is built with the NetBeans GUI builder (Design tab); this file holds the logic.
 */
public class SalesPanel extends javax.swing.JPanel implements Refreshable {

    private final ProductDAO productDAO = new ProductDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final SaleDAO saleDAO = new SaleDAO();

    // Models are created here and attached to the components in the designer
    private final DefaultTableModel productModel = readOnlyModel("Code", "Product", "Price", "Stock");
    private final DefaultTableModel cartModel = readOnlyModel("Product", "Qty", "Subtotal");
    private final SpinnerNumberModel qtyModel = new SpinnerNumberModel(1, 1, 9999, 1);

    private List<Product> shownProducts = new ArrayList<>();
    private final List<SaleDetail> cart = new ArrayList<>();
    private final Map<Integer, Integer> stockById = new HashMap<>();   // stock when the item was added

    public SalesPanel() {
        initComponents();
        setOpaque(false);
        styleTable(productTable, productScroll);
        Theme.alignRight(productTable, 2, 3);
        Theme.columnWidths(productTable, 80, 250, 80, 60);
        styleTable(cartTable, cartScroll);
        Theme.alignRight(cartTable, 1, 2);
        Theme.columnWidths(cartTable, 150, 40, 90);

        for (javax.swing.JButton b : new javax.swing.JButton[] {addButton, completeButton}) {
            b.setOpaque(true);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
        }
        productTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { loadProducts(); }
            @Override public void removeUpdate(DocumentEvent e) { loadProducts(); }
            @Override public void changedUpdate(DocumentEvent e) { loadProducts(); }
        });
        addButton.addActionListener(e -> addToCart());
        removeButton.addActionListener(e -> removeSelected());
        clearButton.addActionListener(e -> clearCart());
        completeButton.addActionListener(e -> completeSale());
        productTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addToCart();      // double-click a product to add it
                }
            }
        });
    }

    private void styleTable(JTable table, JScrollPane scroll) {
        Theme.styleTable(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
    }

    private static DefaultTableModel readOnlyModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    /** Reloads products and customers each time the screen is opened (the cart is kept). */
    @Override
    public void refresh() {
        statusLabel.setText(" ");
        loadCustomers();
        loadProducts();
    }

    private void loadCustomers() {
        Object selected = customerCombo.getSelectedItem();
        try {
            customerCombo.removeAllItems();
            Customer walkIn = new Customer();                 // id 0 = walk-in (no customer saved)
            walkIn.setName("Walk-in customer");
            customerCombo.addItem(walkIn);
            for (Customer c : customerDAO.search("")) {
                customerCombo.addItem(c);
                if (selected instanceof Customer && ((Customer) selected).getId() == c.getId()) {
                    customerCombo.setSelectedItem(c);
                }
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    /** Shows only products that are in stock and match the search box. */
    private void loadProducts() {
        try {
            shownProducts = productDAO.searchAvailable(searchField.getText());
            productModel.setRowCount(0);
            for (Product p : shownProducts) {
                productModel.addRow(new Object[] {
                    p.getCode(), p.getName(), Theme.money(p.getPrice()), p.getStockQuantity()
                });
            }
            statusLabel.setText(" ");
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        statusLabel.setText("Database problem: " + ex.getMessage());
    }

    private int quantityInCart(int productId) {
        for (SaleDetail d : cart) {
            if (d.getProductId() == productId) {
                return d.getQuantity();
            }
        }
        return 0;
    }

    private void addToCart() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a product first.");
            return;
        }
        Product p = shownProducts.get(productTable.convertRowIndexToModel(row));
        int qty = (Integer) qtySpinner.getValue();
        int alreadyInCart = quantityInCart(p.getId());
        if (alreadyInCart + qty > p.getStockQuantity()) {
            JOptionPane.showMessageDialog(this, "Only " + p.getStockQuantity() + " in stock for " + p.getName()
                    + (alreadyInCart > 0 ? " (" + alreadyInCart + " already in the cart)." : "."));
            return;
        }
        if (alreadyInCart > 0) {
            for (SaleDetail d : cart) {
                if (d.getProductId() == p.getId()) {
                    d.setQuantity(d.getQuantity() + qty);
                }
            }
        } else {
            cart.add(new SaleDetail(p.getId(), p.getCode(), p.getName(), qty, p.getPrice()));
        }
        stockById.put(p.getId(), p.getStockQuantity());
        qtySpinner.setValue(1);
        refreshCart();
    }

    private void removeSelected() {
        int row = cartTable.getSelectedRow();
        if (row >= 0) {
            cart.remove(row);
            refreshCart();
        }
    }

    private void clearCart() {
        cart.clear();
        stockById.clear();
        refreshCart();
    }

    private void refreshCart() {
        cartModel.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;
        for (SaleDetail d : cart) {
            cartModel.addRow(new Object[] { d.getProductName(), d.getQuantity(), Theme.money(d.getSubtotal()) });
            total = total.add(d.getSubtotal());
        }
        totalValue.setText(Theme.money(total));
    }

    private void completeSale() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "The cart is empty.");
            return;
        }
        Customer chosen = (Customer) customerCombo.getSelectedItem();
        Integer customerId = (chosen == null || chosen.getId() == 0) ? null : chosen.getId();
        BigDecimal total = BigDecimal.ZERO;
        for (SaleDetail d : cart) {
            total = total.add(d.getSubtotal());
        }
        try {
            int saleId = saleDAO.createSale(customerId, new ArrayList<>(cart));
            JOptionPane.showMessageDialog(this, "Sale #" + saleId + " completed.\nTotal: " + Theme.money(total),
                    "Sale completed", JOptionPane.INFORMATION_MESSAGE);
            clearCart();
            customerCombo.setSelectedIndex(0);
            loadProducts();                      // stock has changed
        } catch (InsufficientStockException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage() + "\nNothing was saved. Adjust the cart and try again.",
                    "Not enough stock", JOptionPane.WARNING_MESSAGE);
            loadProducts();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not save the sale: " + ex.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        productsCard = new inventorysystem.SmoothEdges();
        productsHeading = new javax.swing.JLabel();
        searchField = new javax.swing.JTextField();
        productScroll = new javax.swing.JScrollPane();
        productTable = new javax.swing.JTable();
        qtyLabel = new javax.swing.JLabel();
        qtySpinner = new javax.swing.JSpinner();
        addButton = new javax.swing.JButton();
        statusLabel = new javax.swing.JLabel();
        cartCard = new inventorysystem.SmoothEdges();
        cartHeading = new javax.swing.JLabel();
        cartScroll = new javax.swing.JScrollPane();
        cartTable = new javax.swing.JTable();
        removeButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        customerLabel = new javax.swing.JLabel();
        customerCombo = new javax.swing.JComboBox<>();
        totalCaption = new javax.swing.JLabel();
        totalValue = new javax.swing.JLabel();
        completeButton = new javax.swing.JButton();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        productsCard.setBackground(new java.awt.Color(255, 255, 255));
        productsCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        productsHeading.setForeground(new java.awt.Color(23, 43, 77));
        productsHeading.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        productsHeading.setText("Products");

        productsCard.add(productsHeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 12, 300, 24));
        searchField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        productsCard.add(searchField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 46, 508, 32));
        productTable.setModel(productModel);

        productScroll.setViewportView(productTable);

        productsCard.add(productScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 88, 508, 408));
        qtyLabel.setForeground(new java.awt.Color(35, 45, 60));
        qtyLabel.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        qtyLabel.setText("Quantity");

        productsCard.add(qtyLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 514, 70, 32));
        qtySpinner.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        qtySpinner.setModel(qtyModel);

        productsCard.add(qtySpinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 512, 80, 32));
        addButton.setBackground(new java.awt.Color(13, 148, 136));
        addButton.setForeground(new java.awt.Color(255, 255, 255));
        addButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        addButton.setText("Add to Cart");

        productsCard.add(addButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 510, 150, 36));
        statusLabel.setForeground(new java.awt.Color(220, 53, 69));
        statusLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        statusLabel.setText(" ");

        productsCard.add(statusLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 560, 508, 20));

        this.add(productsCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 540, 600));

        cartCard.setBackground(new java.awt.Color(255, 255, 255));
        cartCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        cartHeading.setForeground(new java.awt.Color(23, 43, 77));
        cartHeading.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        cartHeading.setText("Cart");

        cartCard.add(cartHeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 12, 300, 24));
        cartTable.setModel(cartModel);

        cartScroll.setViewportView(cartTable);

        cartCard.add(cartScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 46, 312, 300));
        removeButton.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        removeButton.setText("Remove Selected");

        cartCard.add(removeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 354, 150, 32));
        clearButton.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        clearButton.setText("Clear Cart");

        cartCard.add(clearButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(178, 354, 150, 32));
        customerLabel.setForeground(new java.awt.Color(120, 130, 145));
        customerLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        customerLabel.setText("Customer");

        cartCard.add(customerLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 404, 300, 20));
        customerCombo.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        cartCard.add(customerCombo, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 426, 312, 32));
        totalCaption.setForeground(new java.awt.Color(120, 130, 145));
        totalCaption.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        totalCaption.setText("Total");

        cartCard.add(totalCaption, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 476, 100, 20));
        totalValue.setForeground(new java.awt.Color(13, 148, 136));
        totalValue.setFont(new java.awt.Font("Arial", 1, 28)); // NOI18N
        totalValue.setText("₱0.00");

        cartCard.add(totalValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 496, 312, 40));
        completeButton.setBackground(new java.awt.Color(13, 148, 136));
        completeButton.setForeground(new java.awt.Color(255, 255, 255));
        completeButton.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        completeButton.setText("Complete Sale");

        cartCard.add(completeButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 546, 312, 42));

        this.add(cartCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(556, 0, 344, 600));

    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JPanel cartCard;
    private javax.swing.JLabel cartHeading;
    private javax.swing.JScrollPane cartScroll;
    private javax.swing.JTable cartTable;
    private javax.swing.JButton clearButton;
    private javax.swing.JButton completeButton;
    private javax.swing.JComboBox<inventorysystem.model.Customer> customerCombo;
    private javax.swing.JLabel customerLabel;
    private javax.swing.JScrollPane productScroll;
    private javax.swing.JTable productTable;
    private javax.swing.JPanel productsCard;
    private javax.swing.JLabel productsHeading;
    private javax.swing.JLabel qtyLabel;
    private javax.swing.JSpinner qtySpinner;
    private javax.swing.JButton removeButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel totalCaption;
    private javax.swing.JLabel totalValue;
    // End of variables declaration//GEN-END:variables
}
