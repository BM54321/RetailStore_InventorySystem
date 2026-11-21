package inventorysystem.ui;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Temporary screen for modules that are still being built. */
public class PlaceholderPanel extends JPanel {
    public PlaceholderPanel(String name) {
        super(new BorderLayout());
        setOpaque(false);
        JLabel l = Theme.label(name + " screen is coming next.", java.awt.Font.PLAIN, 16, Theme.MUTED);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        add(l, BorderLayout.CENTER);
    }
}
