package inventorysystem.dao;

import java.sql.SQLException;
import java.util.List;

/** The operations every table-managing DAO supports (ABSTRACTION). */
public interface CrudDAO<T> {
    List<T> search(String keyword) throws SQLException;   // empty keyword = all rows
    void insert(T item) throws SQLException;
    void update(T item) throws SQLException;
    void delete(int id) throws SQLException;
}
