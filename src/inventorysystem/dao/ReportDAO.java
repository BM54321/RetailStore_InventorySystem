package inventorysystem.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Read-only queries for the dashboard and reports. */
public class ReportDAO extends BaseDAO {

    /** The numbers shown on the dashboard cards. */
    public static class DashboardStats {
        public int totalProducts;
        public int lowStockProducts;
        public int outOfStockProducts;
        public int totalCustomers;
        public int salesToday;
        public BigDecimal revenueToday = BigDecimal.ZERO;
        public BigDecimal revenueTotal = BigDecimal.ZERO;
    }

    public DashboardStats getDashboardStats() throws SQLException {
        DashboardStats s = new DashboardStats();
        try (Connection c = connect(); Statement st = c.createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "SELECT COUNT(*), "
                  + "       COALESCE(SUM(stock_quantity <= min_stock_level), 0), "
                  + "       COALESCE(SUM(stock_quantity = 0), 0) FROM products")) {
                rs.next();
                s.totalProducts = rs.getInt(1);
                s.lowStockProducts = rs.getInt(2);
                s.outOfStockProducts = rs.getInt(3);
            }
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM customers")) {
                rs.next();
                s.totalCustomers = rs.getInt(1);
            }
            try (ResultSet rs = st.executeQuery(
                    "SELECT COUNT(*), COALESCE(SUM(total_amount), 0) FROM v_sale_totals "
                  + "WHERE DATE(sale_date) = CURDATE()")) {
                rs.next();
                s.salesToday = rs.getInt(1);
                s.revenueToday = rs.getBigDecimal(2);
            }
            try (ResultSet rs = st.executeQuery("SELECT COALESCE(SUM(total_amount), 0) FROM v_sale_totals")) {
                rs.next();
                s.revenueTotal = rs.getBigDecimal(1);
            }
        }
        return s;
    }

    /**
     * Newest sales first. Each row: sale id, date/time, customer name, items, total.
     */
    public List<Object[]> getRecentSales(int limit) throws SQLException {
        String sql = "SELECT v.sale_id, v.sale_date, COALESCE(c.name, 'Walk-in'), v.total_items, v.total_amount "
                + "FROM v_sale_totals v LEFT JOIN customers c ON c.customer_id = v.customer_id "
                + "ORDER BY v.sale_date DESC, v.sale_id DESC LIMIT ?";
        List<Object[]> rows = new ArrayList<>();
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp when = rs.getTimestamp(2);
                    rows.add(new Object[] {
                        rs.getInt(1),
                        when == null ? "" : when.toLocalDateTime(),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getBigDecimal(5)
                    });
                }
            }
        }
        return rows;
    }

    /** Totals for the Reports screen. */
    public static class SalesSummary {
        public int transactions;
        public int itemsSold;
        public BigDecimal revenue = BigDecimal.ZERO;

        public BigDecimal average() {
            return transactions == 0 ? BigDecimal.ZERO
                    : revenue.divide(BigDecimal.valueOf(transactions), 2, java.math.RoundingMode.HALF_UP);
        }
    }

    public SalesSummary getSalesSummary(Period period) throws SQLException {
        String sql = "SELECT COUNT(*), COALESCE(SUM(total_items), 0), COALESCE(SUM(total_amount), 0) "
                + "FROM v_sale_totals WHERE " + period.clause("sale_date");
        SalesSummary s = new SalesSummary();
        try (Connection c = connect(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            s.transactions = rs.getInt(1);
            s.itemsSold = rs.getInt(2);
            s.revenue = rs.getBigDecimal(3);
        }
        return s;
    }

    /** One row per day, newest first: date, number of sales, revenue. */
    public List<Object[]> getSalesByDay(Period period) throws SQLException {
        String sql = "SELECT DATE(sale_date), COUNT(*), SUM(total_amount) FROM v_sale_totals "
                + "WHERE " + period.clause("sale_date") + " "
                + "GROUP BY DATE(sale_date) ORDER BY DATE(sale_date) DESC";
        List<Object[]> rows = new ArrayList<>();
        try (Connection c = connect(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                LocalDate day = rs.getDate(1).toLocalDate();
                rows.add(new Object[] { day, rs.getInt(2), rs.getBigDecimal(3) });
            }
        }
        return rows;
    }

    /** Best sellers: product name, quantity sold, revenue. */
    public List<Object[]> getTopProducts(Period period, int limit) throws SQLException {
        String sql = "SELECT p.product_name, SUM(d.quantity), SUM(d.quantity * d.unit_price) "
                + "FROM sale_details d JOIN sales s ON s.sale_id = d.sale_id "
                + "JOIN products p ON p.product_id = d.product_id "
                + "WHERE " + period.clause("s.sale_date") + " "
                + "GROUP BY p.product_id, p.product_name "
                + "ORDER BY SUM(d.quantity) DESC, p.product_name LIMIT ?";
        List<Object[]> rows = new ArrayList<>();
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[] { rs.getString(1), rs.getInt(2), rs.getBigDecimal(3) });
                }
            }
        }
        return rows;
    }
}
