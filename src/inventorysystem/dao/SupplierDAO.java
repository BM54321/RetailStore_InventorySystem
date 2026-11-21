package inventorysystem.dao;

import inventorysystem.model.Supplier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO extends BaseDAO implements CrudDAO<Supplier> {

    @Override
    public List<Supplier> search(String keyword) throws SQLException {
        String sql = "SELECT supplier_id, company_name, name, phone, email, address FROM suppliers "
                + "WHERE company_name LIKE ? OR name LIKE ? OR phone LIKE ? OR email LIKE ? "
                + "ORDER BY company_name";
        List<Supplier> list = new ArrayList<>();
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 1; i <= 4; i++) {
                ps.setString(i, like(keyword));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Supplier(rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getString(5), rs.getString(6)));
                }
            }
        }
        return list;
    }

    @Override
    public void insert(Supplier s) throws SQLException {
        String sql = "INSERT INTO suppliers (company_name, name, phone, email, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            fill(ps, s);
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Supplier s) throws SQLException {
        String sql = "UPDATE suppliers SET company_name = ?, name = ?, phone = ?, email = ?, address = ? "
                + "WHERE supplier_id = ?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            fill(ps, s);
            ps.setInt(6, s.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        deleteById("DELETE FROM suppliers WHERE supplier_id = ?", id);
    }

    private void fill(PreparedStatement ps, Supplier s) throws SQLException {
        ps.setString(1, s.getCompanyName().trim());
        ps.setString(2, s.getName().trim());
        setNullable(ps, 3, s.getPhone());
        setNullable(ps, 4, s.getEmail());
        setNullable(ps, 5, s.getAddress());
    }
}
