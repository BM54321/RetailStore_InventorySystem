package inventorysystem.dao;

import inventorysystem.model.Category;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO extends BaseDAO implements CrudDAO<Category> {

    @Override
    public List<Category> search(String keyword) throws SQLException {
        String sql = "SELECT category_id, category_name, description FROM categories "
                + "WHERE category_name LIKE ? OR description LIKE ? ORDER BY category_name";
        List<Category> list = new ArrayList<>();
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, like(keyword));
            ps.setString(2, like(keyword));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Category(rs.getInt(1), rs.getString(2), rs.getString(3)));
                }
            }
        }
        return list;
    }

    @Override
    public void insert(Category cat) throws SQLException {
        String sql = "INSERT INTO categories (category_name, description) VALUES (?, ?)";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cat.getName().trim());
            setNullable(ps, 2, cat.getDescription());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Category cat) throws SQLException {
        String sql = "UPDATE categories SET category_name = ?, description = ? WHERE category_id = ?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cat.getName().trim());
            setNullable(ps, 2, cat.getDescription());
            ps.setInt(3, cat.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        deleteById("DELETE FROM categories WHERE category_id = ?", id);
    }
}
