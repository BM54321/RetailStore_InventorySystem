package inventorysystem.dao;

/** A time range used to filter sales on the Transactions and Reports screens. */
public enum Period {
    ALL("All time", "1 = 1"),
    TODAY("Today", "DATE(%s) = CURDATE()"),
    LAST_7_DAYS("Last 7 days", "%s >= NOW() - INTERVAL 7 DAY"),
    THIS_MONTH("This month", "YEAR(%1$s) = YEAR(CURDATE()) AND MONTH(%1$s) = MONTH(CURDATE())");

    private final String label;
    private final String template;

    Period(String label, String template) {
        this.label = label;
        this.template = template;
    }

    /** The SQL condition for the given date column, e.g. clause("s.sale_date"). */
    public String clause(String dateColumn) {
        return String.format(template, dateColumn);
    }

    /** The text shown in the drop-down list. */
    @Override
    public String toString() {
        return label;
    }
}
