package inventorysystem.dao;

import inventorysystem.model.SaleDetail;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Saves sales. A sale is all-or-nothing: either everything is saved and the stock is deducted, or nothing is. */
public class SaleDAO extends BaseDAO {

    /**
     * Records a sale and deducts the stock of every item sold, in ONE database transaction.
     *
     * @param customerId the customer, or null for a walk-in customer
     * @param items      the cart (one line per product)
     * @return the new sale number
     * @throws InsufficientStockException if a product does not have enough stock (nothing is saved)
     */
    public int createSale(Integer customerId, List<SaleDetail> items)
            throws SQLException, InsufficientStockException {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("The cart is empty.");
        }
        // Always lock products in the same order so two cashiers cannot block each other.
        List<SaleDetail> sorted = new ArrayList<>(items);
        sorted.sort(Comparator.comparingInt(SaleDetail::getProductId));

        try (Connection c = connect()) {
            c.setAutoCommit(false);
            try {
                int saleId = insertSale(c, customerId);
                for (SaleDetail item : sorted) {
                    BigDecimal price = takeStock(c, item);          // checks and deducts stock
                    insertDetail(c, saleId, item, price);
                }
                c.commit();
                return saleId;
            } catch (SQLException | InsufficientStockException | RuntimeException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    private int insertSale(Connection c, Integer customerId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO sales (customer_id) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
            if (customerId == null) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, customerId);
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Could not create the sale record.");
                }
                return keys.getInt(1);
            }
        }
    }

    /** Locks the product row, checks the stock, deducts it, and returns the CURRENT selling price. */
    private BigDecimal takeStock(Connection c, SaleDetail item) throws SQLException, InsufficientStockException {
        BigDecimal price;
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT product_name, price, stock_quantity FROM products WHERE product_id = ? FOR UPDATE")) {
            ps.setInt(1, item.getProductId());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new InsufficientStockException(item.getProductName() + " no longer exists.");
                }
                int stock = rs.getInt(3);
                if (stock < item.getQuantity()) {
                    throw new InsufficientStockException("Not enough stock for " + rs.getString(1)
                            + ". Only " + stock + " left.");
                }
                price = rs.getBigDecimal(2);
            }
        }
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ?")) {
            ps.setInt(1, item.getQuantity());
            ps.setInt(2, item.getProductId());
            ps.executeUpdate();
        }
        return price;
    }

    private void insertDetail(Connection c, int saleId, SaleDetail item, BigDecimal price) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO sale_details (sale_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, saleId);
            ps.setInt(2, item.getProductId());
            ps.setInt(3, item.getQuantity());
            ps.setBigDecimal(4, price);
            ps.executeUpdate();
        }
    }

    /**
     * Sales history for the Transactions screen, newest first.
     * Each row: sale number, date/time, customer name, items, total.
     * The keyword matches the sale number or the customer name.
     */
    public List<Object[]> findSales(String keyword, Period period) throws SQLException {
        String sql = "SELECT v.sale_id, v.sale_date, COALESCE(c.name, 'Walk-in'), v.total_items, v.total_amount "
                + "FROM v_sale_totals v LEFT JOIN customers c ON c.customer_id = v.customer_id "
                + "WHERE (CAST(v.sale_id AS CHAR) LIKE ? OR COALESCE(c.name, 'Walk-in') LIKE ?) "
                + "AND " + period.clause("v.sale_date") + " "
                + "ORDER BY v.sale_date DESC, v.sale_id DESC";
        List<Object[]> rows = new ArrayList<>();
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, like(keyword));
            ps.setString(2, like(keyword));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Timestamp when = rs.getTimestamp(2);
                    rows.add(new Object[] {
                        rs.getInt(1),
                        when == null ? null : when.toLocalDateTime(),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getBigDecimal(5)
                    });
                }
            }
        }
        return rows;
    }

    /** The items of one sale. Each row: product code, product name, quantity, unit price, subtotal. */
    public List<Object[]> getItems(int saleId) throws SQLException {
        String sql = "SELECT p.product_code, p.product_name, d.quantity, d.unit_price, d.quantity * d.unit_price "
                + "FROM sale_details d JOIN products p ON p.product_id = d.product_id "
                + "WHERE d.sale_id = ? ORDER BY p.product_name";
        List<Object[]> rows = new ArrayList<>();
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new Object[] {
                        rs.getString(1), rs.getString(2), rs.getInt(3),
                        rs.getBigDecimal(4), rs.getBigDecimal(5)
                    });
                }
            }
        }
        return rows;
    }
}
