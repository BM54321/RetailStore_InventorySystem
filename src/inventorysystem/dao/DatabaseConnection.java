package inventorysystem.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Opens connections to the MySQL database inventory_db.
 * Needs the MySQL Connector/J jar on the project's Libraries.
 *
 * Defaults can be changed by creating a file named db.properties in the
 * project folder (see db.properties.example).
 */
public class DatabaseConnection {
    private static String url  = "jdbc:mysql://localhost:3306/inventory_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String user = "root";
    private static String password = "";

    static {
        try (FileInputStream in = new FileInputStream("db.properties")) {
            Properties p = new Properties();
            p.load(in);
            url = p.getProperty("db.url", url);
            user = p.getProperty("db.user", user);
            password = p.getProperty("db.password", password);
        } catch (IOException ignored) {
            // no db.properties file: keep the defaults above
        }
    }

    private DatabaseConnection() { }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Connector/J is missing. In NetBeans: right-click the project > "
                    + "Properties > Libraries > Add JAR/Folder, then pick mysql-connector-j-x.x.x.jar.", e);
        }
        return DriverManager.getConnection(url, user, password);
    }
}
