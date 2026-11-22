package inventorysystem.ui;

import java.sql.SQLException;

/** Turns raw MySQL errors into messages a cashier can understand. */
public final class DbMessages {

    private DbMessages() { }

    /**
     * @param duplicateText shown when the value already exists (MySQL error 1062)
     * @param inUseText     shown when a delete is blocked because other records use it (error 1451)
     */
    public static String friendly(SQLException ex, String duplicateText, String inUseText) {
        int code = ex.getErrorCode();
        if (code == 1062) {
            return duplicateText;
        }
        if (code == 1451) {
            return inUseText;
        }
        return "Database error: " + ex.getMessage();
    }
}
