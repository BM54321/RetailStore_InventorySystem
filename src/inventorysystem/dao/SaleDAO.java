package inventorysystem.dao;

import inventorysystem.model.SaleDetail;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
}
