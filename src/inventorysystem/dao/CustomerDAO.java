package inventorysystem.dao;

import inventorysystem.model.Customer;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO extends BaseDAO implements CrudDAO<Customer> {

    @Override
    public List<Customer> search(String keyword) throws SQLException {
        String sql = "SELECT customer_id, name, phone, email, address, registered_date FROM customers "
                + "WHERE name LIKE ? OR phone LIKE ? OR email LIKE ? OR address LIKE ? ORDER BY name";
        List<Customer> list = new ArrayList<>();
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 1; i <= 4; i++) {
                ps.setString(i, like(keyword));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date d = rs.getDate(6);
                    list.add(new Customer(rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getString(5), d == null ? null : d.toLocalDate()));
                }
            }
        }
        return list;
    }

    @Override
    public void insert(Customer cu) throws SQLException {
        String sql = "INSERT INTO customers (name, phone, email, address) VALUES (?, ?, ?, ?)";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            fill(ps, cu);
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Customer cu) throws SQLException {
        String sql = "UPDATE customers SET name = ?, phone = ?, email = ?, address = ? WHERE customer_id = ?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            fill(ps, cu);
            ps.setInt(5, cu.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        // Past sales keep their rows; the FK sets customer_id to NULL (shown as Walk-in).
        deleteById("DELETE FROM customers WHERE customer_id = ?", id);
    }

    private void fill(PreparedStatement ps, Customer cu) throws SQLException {
        ps.setString(1, cu.getName().trim());
        setNullable(ps, 2, cu.getPhone());
        setNullable(ps, 3, cu.getEmail());
        setNullable(ps, 4, cu.getAddress());
    }
}
