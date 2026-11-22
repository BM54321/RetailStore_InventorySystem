package inventorysystem.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/** Colours, fonts and small helpers shared by all screens (same palette as the login screen). */
public final class Theme {
    public static final Color NAVY = new Color(23, 43, 77);
    public static final Color TEAL = new Color(13, 148, 136);
    public static final Color PAGE_BG = new Color(243, 245, 249);
    public static final Color TEXT = new Color(35, 45, 60);
    public static final Color MUTED = new Color(120, 130, 145);
    public static final Color DANGER = new Color(220, 53, 69);
    public static final Color WARN = new Color(217, 119, 6);

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    private Theme() { }

    public static String money(BigDecimal amount) {
        return String.format("₱%,.2f", amount == null ? BigDecimal.ZERO : amount);
    }

    public static String dateTime(LocalDateTime when) {
        return when == null ? "" : when.format(DATE_TIME);
    }

    public static JLabel label(String text, int style, int size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", style, size));
        l.setForeground(color);
        return l;
    }

    /** Clean, flat look for a table. */
    public static void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setForeground(TEXT);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 233, 240));
        table.setSelectionBackground(new Color(204, 236, 232));
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setBackground(NAVY);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new java.awt.Dimension(0, 32));
    }

    /** Right-aligns the given table columns (for numbers and money). */
    public static void alignRight(JTable table, int... columns) {
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int c : columns) {
            table.getColumnModel().getColumn(c).setCellRenderer(right);
        }
    }

    /** Coloured text for status columns (e.g. LOW STOCK in red). */
    public static void colorColumn(JTable table, int column, String match, Color color, int alignment) {
        table.getColumnModel().getColumn(column).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean sel,
                                                           boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                setHorizontalAlignment(alignment);
                boolean hit = value != null && value.toString().contains(match);
                c.setForeground(hit ? color : TEXT);
                c.setFont(c.getFont().deriveFont(hit ? Font.BOLD : Font.PLAIN));
                return c;
            }
        });
    }

    /** A table model whose cells cannot be edited by typing in the table. */
    public static javax.swing.table.DefaultTableModel readOnlyModel(String... columns) {
        return new javax.swing.table.DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    /** Sets the preferred width of each column (extra space is shared in proportion). */
    public static void columnWidths(JTable table, int... widths) {
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    public static javax.swing.border.Border padding(int top, int left, int bottom, int right) {
        return BorderFactory.createEmptyBorder(top, left, bottom, right);
    }
}
