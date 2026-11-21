package inventorysystem.ui;

import inventorysystem.SmoothEdges;
import inventorysystem.dao.ProductDAO;
import inventorysystem.dao.ReportDAO;
import inventorysystem.dao.ReportDAO.DashboardStats;
import inventorysystem.model.Product;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/** Home screen: summary cards, low-stock alerts and the latest sales. */
public class DashboardPanel extends JPanel implements Refreshable {

    private final ReportDAO reportDAO = new ReportDAO();
    private final ProductDAO productDAO = new ProductDAO();

    private final JLabel productsValue = new JLabel("-");
    private final JLabel lowStockValue = new JLabel("-");
    private final JLabel customersValue = new JLabel("-");
    private final JLabel todayValue = new JLabel("-");
    private final JLabel todaySub = new JLabel(" ");
    private final JLabel revenueValue = new JLabel("-");
    private final JLabel lowStockSub = new JLabel(" ");
    private final JLabel statusLabel = new JLabel(" ");

    private final DefaultTableModel lowStockModel = readOnlyModel("Code", "Product", "Stock", "Min");
    private final DefaultTableModel recentModel = readOnlyModel("Sale #", "Date", "Customer", "Items", "Total");

    public DashboardPanel() {
        super(new BorderLayout(0, 16));
        setOpaque(false);

        statusLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        statusLabel.setForeground(Theme.DANGER);

        JPanel top = new JPanel(new BorderLayout(0, 8));
        top.setOpaque(false);
        top.add(statusLabel, BorderLayout.NORTH);
        top.add(buildCards(), BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        JPanel tables = new JPanel(new GridLayout(1, 2, 16, 0));
        tables.setOpaque(false);
        tables.add(tableCard("Low Stock Alerts", lowStockModel, true));
        tables.add(tableCard("Recent Sales", recentModel, false));
        add(tables, BorderLayout.CENTER);
    }

    private JPanel buildCards() {
        JPanel row = new JPanel(new GridLayout(1, 5, 14, 0));
        row.setOpaque(false);
        row.setPreferredSize(new java.awt.Dimension(0, 104));
        row.add(statCard("Total Products", productsValue, new JLabel(" "), Theme.NAVY));
        row.add(statCard("Low Stock Items", lowStockValue, lowStockSub, Theme.DANGER));
        row.add(statCard("Customers", customersValue, new JLabel(" "), Theme.NAVY));
        row.add(statCard("Today's Sales", todayValue, todaySub, Theme.TEAL));
        row.add(statCard("Total Revenue", revenueValue, new JLabel(" "), Theme.TEAL));
        return row;
    }

    private JPanel statCard(String title, JLabel value, JLabel sub, Color accent) {
        SmoothEdges card = new SmoothEdges();
        card.setBackground(Color.WHITE);
        card.setLayout(new GridLayout(3, 1, 0, 0));
        card.setBorder(Theme.padding(12, 16, 10, 12));
        card.add(Theme.label(title, Font.PLAIN, 13, Theme.MUTED));
        value.setFont(new Font("Arial", Font.BOLD, 26));
        value.setForeground(accent);
        card.add(value);
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(Theme.MUTED);
        card.add(sub);
        return card;
    }

    private JPanel tableCard(String title, DefaultTableModel model, boolean lowStock) {
        SmoothEdges card = new SmoothEdges();
        card.setBackground(Color.WHITE);
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(Theme.padding(14, 14, 14, 14));
        card.add(Theme.label(title, Font.BOLD, 16, Theme.NAVY), BorderLayout.NORTH);

        JTable table = new JTable(model);
        Theme.styleTable(table);
        if (lowStock) {
            Theme.alignRight(table, 3);
            Theme.colorColumn(table, 2, "", Theme.DANGER, javax.swing.SwingConstants.RIGHT);
            Theme.columnWidths(table, 80, 220, 60, 60);
        } else {
            Theme.alignRight(table, 3, 4);
            Theme.columnWidths(table, 55, 185, 130, 50, 95);
        }
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);
        return card;
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
}
