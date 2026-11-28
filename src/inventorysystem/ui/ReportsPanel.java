package inventorysystem.ui;

import inventorysystem.dao.Period;
import inventorysystem.dao.ReportDAO;
import inventorysystem.dao.ReportDAO.SalesSummary;
import java.awt.Color;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;

/**
 * Reports screen: sales totals, sales per day and the best-selling products
 * for a chosen period (all time, today, last 7 days or this month).
 * The layout is built with the NetBeans GUI builder (Design tab); this file holds the logic.
 */
public class ReportsPanel extends javax.swing.JPanel implements Refreshable {

    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy");

    private final ReportDAO reportDAO = new ReportDAO();
    private final DefaultTableModel dayModel = Theme.readOnlyModel("Date", "Sales", "Revenue");
    private final DefaultTableModel topModel = Theme.readOnlyModel("Product", "Qty sold", "Revenue");

    public ReportsPanel() {
        initComponents();
        styleScreen();
        Theme.alignRight(dayTable, 1, 2);
        Theme.columnWidths(dayTable, 190, 70, 130);
        Theme.alignRight(topTable, 1, 2);
        Theme.columnWidths(topTable, 200, 80, 120);
    }

    @Override
    public void refresh() {
        statusLabel.setText(" ");
        load();
    }

    private void styleScreen() {
        setOpaque(false);
        Theme.styleTable(dayTable);
        Theme.styleTable(topTable);
        for (javax.swing.JScrollPane sp : new javax.swing.JScrollPane[] {dayScroll, topScroll}) {
            sp.setBorder(BorderFactory.createEmptyBorder());
            sp.getViewport().setBackground(Color.WHITE);
        }
        periodCombo.setModel(new DefaultComboBoxModel<>(Period.values()));
        periodCombo.addActionListener(e -> load());
    }

    private void load() {
        Period period = (Period) periodCombo.getSelectedItem();
        try {
            SalesSummary s = reportDAO.getSalesSummary(period);
            revenueValue.setText(Theme.money(s.revenue));
            salesValue.setText(String.valueOf(s.transactions));
            itemsValue.setText(String.valueOf(s.itemsSold));
            averageValue.setText(Theme.money(s.average()));

            dayModel.setRowCount(0);
            for (Object[] r : reportDAO.getSalesByDay(period)) {
                dayModel.addRow(new Object[] {DAY.format((LocalDate) r[0]), r[1], Theme.money((BigDecimal) r[2])});
            }
            topModel.setRowCount(0);
            for (Object[] r : reportDAO.getTopProducts(period, 10)) {
                topModel.addRow(new Object[] {r[0], r[1], Theme.money((BigDecimal) r[2])});
            }
            statusLabel.setText(" ");
        } catch (SQLException ex) {
            statusLabel.setText("Database problem: " + ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        periodLabel = new javax.swing.JLabel();
        periodCombo = new javax.swing.JComboBox<>();
        revenueCard = new inventorysystem.SmoothEdges();
        revenueTitle = new javax.swing.JLabel();
        revenueValue = new javax.swing.JLabel();
        salesCard = new inventorysystem.SmoothEdges();
        salesTitle = new javax.swing.JLabel();
        salesValue = new javax.swing.JLabel();
        itemsCard = new inventorysystem.SmoothEdges();
        itemsTitle = new javax.swing.JLabel();
        itemsValue = new javax.swing.JLabel();
        averageCard = new inventorysystem.SmoothEdges();
        averageTitle = new javax.swing.JLabel();
        averageValue = new javax.swing.JLabel();
        dayCard = new inventorysystem.SmoothEdges();
        dayHeading = new javax.swing.JLabel();
        dayScroll = new javax.swing.JScrollPane();
        dayTable = new javax.swing.JTable();
        statusLabel = new javax.swing.JLabel();
        topCard = new inventorysystem.SmoothEdges();
        topHeading = new javax.swing.JLabel();
        topScroll = new javax.swing.JScrollPane();
        topTable = new javax.swing.JTable();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        periodLabel.setForeground(new java.awt.Color(120, 130, 145));
        periodLabel.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        periodLabel.setText("Period");

        this.add(periodLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 8, 60, 22));

        periodCombo.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        this.add(periodCombo, new org.netbeans.lib.awtextra.AbsoluteConstraints(62, 2, 200, 32));

        revenueCard.setBackground(new java.awt.Color(255, 255, 255));
        revenueCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        revenueTitle.setForeground(new java.awt.Color(120, 130, 145));
        revenueTitle.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        revenueTitle.setText("Total revenue");

        revenueCard.add(revenueTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 14, 185, 18));
        revenueValue.setForeground(new java.awt.Color(13, 148, 136));
        revenueValue.setFont(new java.awt.Font("Arial", 1, 26)); // NOI18N
        revenueValue.setText("-");

        revenueCard.add(revenueValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 38, 185, 40));

        this.add(revenueCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 48, 213, 96));

        salesCard.setBackground(new java.awt.Color(255, 255, 255));
        salesCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        salesTitle.setForeground(new java.awt.Color(120, 130, 145));
        salesTitle.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        salesTitle.setText("Transactions");

        salesCard.add(salesTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 14, 185, 18));
        salesValue.setForeground(new java.awt.Color(23, 43, 77));
        salesValue.setFont(new java.awt.Font("Arial", 1, 26)); // NOI18N
        salesValue.setText("-");

        salesCard.add(salesValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 38, 185, 40));

        this.add(salesCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(229, 48, 213, 96));

        itemsCard.setBackground(new java.awt.Color(255, 255, 255));
        itemsCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        itemsTitle.setForeground(new java.awt.Color(120, 130, 145));
        itemsTitle.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        itemsTitle.setText("Items sold");

        itemsCard.add(itemsTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 14, 185, 18));
        itemsValue.setForeground(new java.awt.Color(23, 43, 77));
        itemsValue.setFont(new java.awt.Font("Arial", 1, 26)); // NOI18N
        itemsValue.setText("-");

        itemsCard.add(itemsValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 38, 185, 40));

        this.add(itemsCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(458, 48, 213, 96));

        averageCard.setBackground(new java.awt.Color(255, 255, 255));
        averageCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        averageTitle.setForeground(new java.awt.Color(120, 130, 145));
        averageTitle.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        averageTitle.setText("Average sale");

        averageCard.add(averageTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 14, 185, 18));
        averageValue.setForeground(new java.awt.Color(23, 43, 77));
        averageValue.setFont(new java.awt.Font("Arial", 1, 26)); // NOI18N
        averageValue.setText("-");

        averageCard.add(averageValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 38, 185, 40));

        this.add(averageCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(687, 48, 213, 96));

        dayCard.setBackground(new java.awt.Color(255, 255, 255));
        dayCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        dayHeading.setForeground(new java.awt.Color(23, 43, 77));
        dayHeading.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        dayHeading.setText("Sales by Day");

        dayCard.add(dayHeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 12, 400, 24));
        dayTable.setModel(dayModel);

        dayScroll.setViewportView(dayTable);

        dayCard.add(dayScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 46, 408, 360));
        statusLabel.setForeground(new java.awt.Color(220, 53, 69));
        statusLabel.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        statusLabel.setText(" ");

        dayCard.add(statusLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 414, 408, 20));

        this.add(dayCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 160, 440, 440));

        topCard.setBackground(new java.awt.Color(255, 255, 255));
        topCard.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        topHeading.setForeground(new java.awt.Color(23, 43, 77));
        topHeading.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        topHeading.setText("Top Selling Products");

        topCard.add(topHeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 12, 400, 24));
        topTable.setModel(topModel);

        topScroll.setViewportView(topTable);

        topCard.add(topScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(16, 46, 408, 360));

        this.add(topCard, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 160, 440, 440));

    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel averageCard;
    private javax.swing.JLabel averageTitle;
    private javax.swing.JLabel averageValue;
    private javax.swing.JPanel dayCard;
    private javax.swing.JLabel dayHeading;
    private javax.swing.JScrollPane dayScroll;
    private javax.swing.JTable dayTable;
    private javax.swing.JPanel itemsCard;
    private javax.swing.JLabel itemsTitle;
    private javax.swing.JLabel itemsValue;
    private javax.swing.JComboBox<inventorysystem.dao.Period> periodCombo;
    private javax.swing.JLabel periodLabel;
    private javax.swing.JPanel revenueCard;
    private javax.swing.JLabel revenueTitle;
    private javax.swing.JLabel revenueValue;
    private javax.swing.JPanel salesCard;
    private javax.swing.JLabel salesTitle;
    private javax.swing.JLabel salesValue;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JPanel topCard;
    private javax.swing.JLabel topHeading;
    private javax.swing.JScrollPane topScroll;
    private javax.swing.JTable topTable;
    // End of variables declaration//GEN-END:variables
}
