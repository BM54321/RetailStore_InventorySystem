package inventorysystem.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
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
}
