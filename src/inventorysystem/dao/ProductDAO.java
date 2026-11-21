package inventorysystem.dao;

import inventorysystem.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO extends BaseDAO implements CrudDAO<Product> {

    private static final String SELECT =
            "SELECT p.product_id, p.product_code, p.product_name, p.category_id, c.category_name, "
          + "       p.supplier_id, s.company_name, p.price, p.stock_quantity, p.min_stock_level "
          + "FROM products p "
          + "JOIN categories c ON c.category_id = p.category_id "
          + "JOIN suppliers  s ON s.supplier_id = p.supplier_id ";

    private static final String SEARCH_WHERE =
            "WHERE p.product_code LIKE ? OR p.product_name LIKE ? "
          + "   OR c.category_name LIKE ? OR s.company_name LIKE ? ";

    @Override
    public List<Product> search(String keyword) throws SQLException {
        return query(SELECT + SEARCH_WHERE + "ORDER BY p.product_name", keyword);
    }

    /** Only products at or below their minimum stock level. */
    public List<Product> searchLowStock(String keyword) throws SQLException {
        return query(SELECT + "WHERE (p.product_code LIKE ? OR p.product_name LIKE ? "
                + "   OR c.category_name LIKE ? OR s.company_name LIKE ?) "
                + "  AND p.stock_quantity <= p.min_stock_level ORDER BY p.stock_quantity", keyword);
    }

    /** Products that can still be sold (stock > 0) - used by the Sales screen. */
    public List<Product> searchAvailable(String keyword) throws SQLException {
        return query(SELECT + "WHERE (p.product_code LIKE ? OR p.product_name LIKE ? "
                + "   OR c.category_name LIKE ? OR s.company_name LIKE ?) "
                + "  AND p.stock_quantity > 0 ORDER BY p.product_name", keyword);
    }

    private List<Product> query(String sql, String keyword) throws SQLException {
        List<Product> list = new ArrayList<>();
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 1; i <= 4; i++) {
                ps.setString(i, like(keyword));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product();
                    p.setId(rs.getInt(1));
                    p.setCode(rs.getString(2));
                    p.setName(rs.getString(3));
                    p.setCategoryId(rs.getInt(4));
                    p.setCategoryName(rs.getString(5));
                    p.setSupplierId(rs.getInt(6));
                    p.setSupplierName(rs.getString(7));
                    p.setPrice(rs.getBigDecimal(8));
                    p.setStockQuantity(rs.getInt(9));
                    p.setMinStockLevel(rs.getInt(10));
                    list.add(p);
                }
            }
        }
        return list;
    }

    @Override
    public void insert(Product p) throws SQLException {
        String sql = "INSERT INTO products (product_code, product_name, category_id, supplier_id, "
                + "price, stock_quantity, min_stock_level) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            fill(ps, p);
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Product p) throws SQLException {
        String sql = "UPDATE products SET product_code = ?, product_name = ?, category_id = ?, supplier_id = ?, "
                + "price = ?, stock_quantity = ?, min_stock_level = ? WHERE product_id = ?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            fill(ps, p);
            ps.setInt(8, p.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        deleteById("DELETE FROM products WHERE product_id = ?", id);
    }

    /** Inventory screen: add (positive) or remove (negative) stock. Never goes below 0. */
    public void adjustStock(int productId, int delta) throws SQLException {
        String sql = "UPDATE products SET stock_quantity = stock_quantity + ? "
                + "WHERE product_id = ? AND stock_quantity + ? >= 0";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, productId);
            ps.setInt(3, delta);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Stock cannot go below zero.");
            }
        }
    }

    private void fill(PreparedStatement ps, Product p) throws SQLException {
        ps.setString(1, p.getCode().trim());
        ps.setString(2, p.getName().trim());
        ps.setInt(3, p.getCategoryId());
        ps.setInt(4, p.getSupplierId());
        ps.setBigDecimal(5, p.getPrice());
        ps.setInt(6, p.getStockQuantity());
        ps.setInt(7, p.getMinStockLevel());
    }
}
