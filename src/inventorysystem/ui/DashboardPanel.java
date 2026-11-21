package inventorysystem.ui;

import inventorysystem.dao.ProductDAO;
import inventorysystem.dao.ReportDAO;
import inventorysystem.dao.ReportDAO.DashboardStats;
import inventorysystem.model.Product;
import java.awt.Color;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 * Home screen: summary cards, low-stock alerts and the latest sales.
 * The layout is built with the NetBeans GUI builder (Design tab);
 * this file only fills it with data.
 */
public class DashboardPanel extends javax.swing.JPanel implements Refreshable {

    private final ReportDAO reportDAO = new ReportDAO();
    private final ProductDAO productDAO = new ProductDAO();

    // The table models are created here and attached to the tables in the designer
    private final DefaultTableModel lowStockModel = readOnlyModel("Code", "Product", "Stock", "Min");
    private final DefaultTableModel recentModel = readOnlyModel("Sale #", "Date", "Customer", "Items", "Total");

    public DashboardPanel() {
        initComponents();
        setOpaque(false);
        styleTable(lowStockListTable, lowStockListScroll);
        Theme.alignRight(lowStockListTable, 3);
        Theme.colorColumn(lowStockListTable, 2, "", Theme.DANGER, SwingConstants.RIGHT);
        Theme.columnWidths(lowStockListTable, 80, 220, 60, 60);
        styleTable(recentTable, recentScroll);
        Theme.alignRight(recentTable, 3, 4);
        Theme.columnWidths(recentTable, 55, 185, 130, 50, 95);
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

    /** Loads everything in the background so the window never freezes. */
    @Override
    public void refresh() {
        statusLabel.setText(" ");
        new SwingWorker<Object[], Void>() {
            @Override
            protected Object[] doInBackground() throws Exception {
                return new Object[] {
                    reportDAO.getDashboardStats(),
                    productDAO.searchLowStock(""),
                    reportDAO.getRecentSales(10)
                };
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void done() {
                try {
                    Object[] r = get();
                    show((DashboardStats) r[0], (List<Product>) r[1], (List<Object[]>) r[2]);
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    statusLabel.setText("Cannot load data from the database: " + cause.getMessage());
                }
            }
        }.execute();
    }

    private void show(DashboardStats s, List<Product> low, List<Object[]> recent) {
        productsValue.setText(String.valueOf(s.totalProducts));
        lowStockValue.setText(String.valueOf(s.lowStockProducts));
        lowStockSub.setText(s.outOfStockProducts + " out of stock");
        customersValue.setText(String.valueOf(s.totalCustomers));
        todayValue.setText(Theme.money(s.revenueToday));
        todaySub.setText(s.salesToday + (s.salesToday == 1 ? " transaction" : " transactions"));
        revenueValue.setText(Theme.money(s.revenueTotal));

        lowStockModel.setRowCount(0);
        for (Product p : low) {
            lowStockModel.addRow(new Object[] {
                p.getCode(), p.getName(), p.getStockQuantity(), p.getMinStockLevel()
            });
        }

        recentModel.setRowCount(0);
        for (Object[] row : recent) {
            recentModel.addRow(new Object[] {
                "#" + row[0], Theme.dateTime((LocalDateTime) row[1]), row[2], row[3],
                Theme.money((BigDecimal) row[4])
            });
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statusLabel = new javax.swing.JLabel();
        productsCard = new inventorysystem.SmoothEdges();
        productsTitle = new javax.swing.JLabel();
        productsValue = new javax.swing.JLabel();
        lowStockCard = new inventorysystem.SmoothEdges();
        lowStockTitle = new javax.swing.JLabel();
        lowStockValue = new javax.swing.JLabel();
        lowStockSub = new javax.swing.JLabel();
        customersCard = new inventorysystem.SmoothEdges();
        customersTitle = new javax.swing.JLabel();
        customersValue = new javax.swing.JLabel();
        todayCard = new inventorysystem.SmoothEdges();
        todayTitle = new javax.swing.JLabel();
        todayValue = new javax.swing.JLabel();
        todaySub = new javax.swing.JLabel();
        revenueCard = new inventorysystem.SmoothEdges();
        revenueTitle = new javax.swing.JLabel();
        revenueValue = new javax.swing.JLabel();
        lowStockListCard = new inventorysystem.SmoothEdges();
        lowStockListHeading = new javax.swing.JLabel();
        lowStockListScroll = new javax.swing.JScrollPane();
        lowStockListTable = new javax.swing.JTable();
        recentCard = new inventorysystem.SmoothEdges();
        recentHeading = new javax.swing.JLabel();
        recentScroll = new javax.swing.JScrollPane();
        recentTable = new javax.swing.JTable();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        statusLabel.setForeground(new java.awt.Color(220, 53, 69));
        statusLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        statusLabel.setText(" ");

        this.add(statusLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 900, 20));

        productsCard.setBackground(new java.awt.Color(255, 255, 255));
        productsCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        productsTitle.setForeground(new java.awt.Color(120, 130, 145));
        productsTitle.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        productsTitle.setText("Total Products");

        productsCard.add(productsTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 10, 140, 20));
        productsValue.setForeground(new java.awt.Color(23, 43, 77));
        productsValue.setFont(new java.awt.Font("Arial", 1, 26)); // NOI18N
        productsValue.setText("-");

        productsCard.add(productsValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 34, 145, 36));

        this.add(productsCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 26, 168, 100));

        lowStockCard.setBackground(new java.awt.Color(255, 255, 255));
        lowStockCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        lowStockTitle.setForeground(new java.awt.Color(120, 130, 145));
        lowStockTitle.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        lowStockTitle.setText("Low Stock Items");

        lowStockCard.add(lowStockTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 10, 140, 20));
        lowStockValue.setForeground(new java.awt.Color(220, 53, 69));
        lowStockValue.setFont(new java.awt.Font("Arial", 1, 26)); // NOI18N
        lowStockValue.setText("-");

        lowStockCard.add(lowStockValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 34, 145, 36));
        lowStockSub.setForeground(new java.awt.Color(120, 130, 145));
        lowStockSub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        lowStockSub.setText(" ");

        lowStockCard.add(lowStockSub, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 72, 145, 18));

        this.add(lowStockCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(183, 26, 168, 100));

        customersCard.setBackground(new java.awt.Color(255, 255, 255));
        customersCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        customersTitle.setForeground(new java.awt.Color(120, 130, 145));
        customersTitle.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        customersTitle.setText("Customers");

        customersCard.add(customersTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 10, 140, 20));
        customersValue.setForeground(new java.awt.Color(23, 43, 77));
        customersValue.setFont(new java.awt.Font("Arial", 1, 26)); // NOI18N
        customersValue.setText("-");

        customersCard.add(customersValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 34, 145, 36));

        this.add(customersCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(366, 26, 168, 100));

        todayCard.setBackground(new java.awt.Color(255, 255, 255));
        todayCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        todayTitle.setForeground(new java.awt.Color(120, 130, 145));
        todayTitle.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        todayTitle.setText("Today's Sales");

        todayCard.add(todayTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 10, 140, 20));
        todayValue.setForeground(new java.awt.Color(13, 148, 136));
        todayValue.setFont(new java.awt.Font("Arial", 1, 26)); // NOI18N
        todayValue.setText("-");

        todayCard.add(todayValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 34, 145, 36));
        todaySub.setForeground(new java.awt.Color(120, 130, 145));
        todaySub.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        todaySub.setText(" ");

        todayCard.add(todaySub, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 72, 145, 18));

        this.add(todayCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(549, 26, 168, 100));

        revenueCard.setBackground(new java.awt.Color(255, 255, 255));
        revenueCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        revenueTitle.setForeground(new java.awt.Color(120, 130, 145));
        revenueTitle.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        revenueTitle.setText("Total Revenue");

        revenueCard.add(revenueTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 10, 140, 20));
        revenueValue.setForeground(new java.awt.Color(13, 148, 136));
        revenueValue.setFont(new java.awt.Font("Arial", 1, 26)); // NOI18N
        revenueValue.setText("-");

        revenueCard.add(revenueValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 34, 145, 36));

        this.add(revenueCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(732, 26, 168, 100));

        lowStockListCard.setBackground(new java.awt.Color(255, 255, 255));
        lowStockListCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        lowStockListHeading.setForeground(new java.awt.Color(23, 43, 77));
        lowStockListHeading.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        lowStockListHeading.setText("Low Stock Alerts");

        lowStockListCard.add(lowStockListHeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 12, 300, 24));
        lowStockListTable.setModel(lowStockModel);

        lowStockListScroll.setViewportView(lowStockListTable);

        lowStockListCard.add(lowStockListScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 46, 414, 392));

        this.add(lowStockListCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 142, 442, 452));

        recentCard.setBackground(new java.awt.Color(255, 255, 255));
        recentCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        recentHeading.setForeground(new java.awt.Color(23, 43, 77));
        recentHeading.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        recentHeading.setText("Recent Sales");

        recentCard.add(recentHeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 12, 300, 24));
        recentTable.setModel(recentModel);

        recentScroll.setViewportView(recentTable);

        recentCard.add(recentScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(14, 46, 414, 392));

        this.add(recentCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(458, 142, 442, 452));

    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel customersCard;
    private javax.swing.JLabel customersTitle;
    private javax.swing.JLabel customersValue;
    private javax.swing.JPanel lowStockCard;
    private javax.swing.JPanel lowStockListCard;
    private javax.swing.JLabel lowStockListHeading;
    private javax.swing.JScrollPane lowStockListScroll;
    private javax.swing.JTable lowStockListTable;
    private javax.swing.JLabel lowStockSub;
    private javax.swing.JLabel lowStockTitle;
    private javax.swing.JLabel lowStockValue;
    private javax.swing.JPanel productsCard;
    private javax.swing.JLabel productsTitle;
    private javax.swing.JLabel productsValue;
    private javax.swing.JPanel recentCard;
    private javax.swing.JLabel recentHeading;
    private javax.swing.JScrollPane recentScroll;
    private javax.swing.JTable recentTable;
    private javax.swing.JPanel revenueCard;
    private javax.swing.JLabel revenueTitle;
    private javax.swing.JLabel revenueValue;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JPanel todayCard;
    private javax.swing.JLabel todaySub;
    private javax.swing.JLabel todayTitle;
    private javax.swing.JLabel todayValue;
    // End of variables declaration//GEN-END:variables
}
