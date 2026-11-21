package inventorysystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** Shared helpers for the DAO classes. */
public abstract class BaseDAO {

    protected Connection connect() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    /** Turns "milk" into "%milk%" for LIKE searches. */
    protected String like(String keyword) {
        return "%" + (keyword == null ? "" : keyword.trim()) + "%";
    }

    /** Runs "DELETE FROM table WHERE id = ?" style statements. */
    protected void deleteById(String sql, int id) throws SQLException {
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** Empty text becomes SQL NULL. */
    protected void setNullable(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null || value.trim().isEmpty()) {
            ps.setNull(index, java.sql.Types.VARCHAR);
        } else {
            ps.setString(index, value.trim());
        }
    }
}
