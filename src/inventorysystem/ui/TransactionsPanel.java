package inventorysystem.ui;

import inventorysystem.dao.Period;
import inventorysystem.dao.SaleDAO;
import java.awt.Color;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * Transactions screen: the history of all sales. Pick a sale in the top table
 * to see the items that were sold in it in the bottom table.
 * The layout is built with the NetBeans GUI builder (Design tab); this file holds the logic.
 */
public class TransactionsPanel extends javax.swing.JPanel implements Refreshable {

    private final SaleDAO saleDAO = new SaleDAO();
    private final DefaultTableModel salesModel = Theme.readOnlyModel("Sale #", "Date", "Customer", "Items", "Total");
    private final DefaultTableModel itemsModel = Theme.readOnlyModel("Code", "Product", "Qty", "Unit price", "Subtotal");
    private List<Object[]> shown = new ArrayList<>();
    private boolean loading;

    public TransactionsPanel() {
        initComponents();
        styleScreen();
        Theme.alignRight(salesTable, 3, 4);
        Theme.columnWidths(salesTable, 60, 200, 260, 70, 120);
        Theme.alignRight(itemsTable, 2, 3, 4);
        Theme.columnWidths(itemsTable, 90, 330, 70, 120, 120);
    }

    @Override
    public void refresh() {
        statusLabel.setText(" ");
        load();
    }

    private void styleScreen() {
        setOpaque(false);
        for (javax.swing.JTable t : new javax.swing.JTable[] {salesTable, itemsTable}) {
            Theme.styleTable(t);
        }
        salesTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        for (javax.swing.JScrollPane sp : new javax.swing.JScrollPane[] {salesScroll, itemsScroll}) {
            sp.setBorder(BorderFactory.createEmptyBorder());
            sp.getViewport().setBackground(Color.WHITE);
        }
        periodCombo.setModel(new DefaultComboBoxModel<>(Period.values()));
        salesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !loading) {
                showItems();
            }
        });
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { load(); }
            @Override public void removeUpdate(DocumentEvent e) { load(); }
            @Override public void changedUpdate(DocumentEvent e) { load(); }
        });
        periodCombo.addActionListener(e -> load());
    }

    private void load() {
        int row = salesTable.getSelectedRow();
        int keepId = row < 0 || row >= shown.size() ? -1 : (Integer) shown.get(salesTable.convertRowIndexToModel(row))[0];
        loading = true;
        BigDecimal sum = BigDecimal.ZERO;
        try {
            shown = saleDAO.findSales(searchField.getText(), (Period) periodCombo.getSelectedItem());
            salesModel.setRowCount(0);
            for (Object[] r : shown) {
                LocalDateTime when = (LocalDateTime) r[1];
                BigDecimal total = (BigDecimal) r[4];
                sum = sum.add(total);
                salesModel.addRow(new Object[] {
                    r[0], when == null ? "" : Theme.dateTime(when), r[2], r[3], Theme.money(total)
                });
            }
            countLabel.setText(shown.size() + (shown.size() == 1 ? " sale" : " sales") + "  -  " + Theme.money(sum));
            statusLabel.setText(" ");
        } catch (SQLException ex) {
            shown = new ArrayList<>();
            salesModel.setRowCount(0);
            countLabel.setText(" ");
            statusLabel.setText("Database problem: " + ex.getMessage());
        }
        int select = shown.isEmpty() ? -1 : 0;
        for (int i = 0; i < shown.size(); i++) {
            if ((Integer) shown.get(i)[0] == keepId) {
                select = i;
            }
        }
        if (select >= 0) {
            salesTable.setRowSelectionInterval(select, select);
        }
        loading = false;
        showItems();
    }

    private void showItems() {
        itemsModel.setRowCount(0);
        int row = salesTable.getSelectedRow();
        if (row < 0 || row >= shown.size()) {
            itemsHeading.setText("Items in the selected sale");
            totalLabel.setText("Total: -");
            return;
        }
        Object[] sale = shown.get(salesTable.convertRowIndexToModel(row));
        try {
            for (Object[] r : saleDAO.getItems((Integer) sale[0])) {
                itemsModel.addRow(new Object[] {
                    r[0], r[1], r[2], Theme.money((BigDecimal) r[3]), Theme.money((BigDecimal) r[4])
                });
            }
            statusLabel.setText(" ");
        } catch (SQLException ex) {
            statusLabel.setText("Database problem: " + ex.getMessage());
        }
        itemsHeading.setText("Items in sale #" + sale[0] + " - " + sale[2]);
        totalLabel.setText("Total: " + Theme.money((BigDecimal) sale[4]));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        salesCard = new inventorysystem.SmoothEdges();
        salesHeading = new javax.swing.JLabel();
        searchField = new javax.swing.JTextField();
        periodCombo = new javax.swing.JComboBox<>();
        countLabel = new javax.swing.JLabel();
        salesScroll = new javax.swing.JScrollPane();
        salesTable = new javax.swing.JTable();
        statusLabel = new javax.swing.JLabel();
        itemsCard = new inventorysystem.SmoothEdges();
        itemsHeading = new javax.swing.JLabel();
        itemsScroll = new javax.swing.JScrollPane();
        itemsTable = new javax.swing.JTable();
        totalLabel = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        salesCard.setBackground(new java.awt.Color(255, 255, 255));
        salesCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        salesHeading.setForeground(new java.awt.Color(23, 43, 77));
        salesHeading.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        salesHeading.setText("Sales History");

        salesCard.add(salesHeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 12, 300, 24));
        searchField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        salesCard.add(searchField, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 46, 380, 32));
        periodCombo.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        salesCard.add(periodCombo, new org.netbeans.lib.awtextra.AbsoluteConstraints(406, 46, 200, 32));
        countLabel.setForeground(new java.awt.Color(120, 130, 145));
        countLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        countLabel.setText(" ");
        countLabel.setHorizontalAlignment(4);

        salesCard.add(countLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(616, 52, 268, 20));
        salesTable.setModel(salesModel);

        salesScroll.setViewportView(salesTable);

        salesCard.add(salesScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 88, 868, 200));
        statusLabel.setForeground(new java.awt.Color(220, 53, 69));
        statusLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        statusLabel.setText(" ");

        salesCard.add(statusLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 294, 868, 20));

        this.add(salesCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 900, 320));

        itemsCard.setBackground(new java.awt.Color(255, 255, 255));
        itemsCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        itemsHeading.setForeground(new java.awt.Color(23, 43, 77));
        itemsHeading.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        itemsHeading.setText("Items in the selected sale");

        itemsCard.add(itemsHeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 12, 600, 24));
        itemsTable.setModel(itemsModel);

        itemsScroll.setViewportView(itemsTable);

        itemsCard.add(itemsScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 44, 868, 170));
        totalLabel.setForeground(new java.awt.Color(23, 43, 77));
        totalLabel.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        totalLabel.setText("Total: -");
        totalLabel.setHorizontalAlignment(4);

        itemsCard.add(totalLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 224, 868, 28));

        this.add(itemsCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 332, 900, 268));

    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel countLabel;
    private javax.swing.JPanel itemsCard;
    private javax.swing.JLabel itemsHeading;
    private javax.swing.JScrollPane itemsScroll;
    private javax.swing.JTable itemsTable;
    private javax.swing.JComboBox<inventorysystem.dao.Period> periodCombo;
    private javax.swing.JPanel salesCard;
    private javax.swing.JLabel salesHeading;
    private javax.swing.JScrollPane salesScroll;
    private javax.swing.JTable salesTable;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel totalLabel;
    // End of variables declaration//GEN-END:variables
}
