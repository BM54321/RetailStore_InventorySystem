package inventorysystem;

import inventorysystem.ui.DashboardPanel;
import inventorysystem.ui.PlaceholderPanel;
import inventorysystem.ui.Refreshable;
import inventorysystem.ui.Theme;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class DashboardFrame extends javax.swing.JFrame {

    private static final Color NAVY = new Color(23, 43, 77);
    private static final Color CONTENT_BG = new Color(243, 245, 249);

    private static final Color ACTIVE = Theme.TEAL;

    private final CardLayout cards = new CardLayout();
    private final JPanel cardHost = new JPanel(cards);
    private final Map<String, JButton> menu = new LinkedHashMap<>();
    private final Map<String, Component> screens = new LinkedHashMap<>();
    private JButton categoriesButton;
    private JButton suppliersButton;
    private JButton customersButton;
    private JButton reportsButton;

    public DashboardFrame() {
        initComponents();
        setTitle("RMS | Retail Store Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        buildLayout();
        setMinimumSize(new Dimension(1000, 620));
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setResizable(true);
        showScreen("Dashboard");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sidebarPanel = new javax.swing.JPanel();
        logoLabel = new javax.swing.JLabel();
        dashboardButton = new javax.swing.JButton();
        productsButton = new javax.swing.JButton();
        inventoryButton = new javax.swing.JButton();
        salesButton = new javax.swing.JButton();
        transactionsButton = new javax.swing.JButton();
        logoutButton = new javax.swing.JButton();
        contentPanel = new javax.swing.JPanel();
        dashboardTitleLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        sidebarPanel.setBackground(new java.awt.Color(23, 43, 77));
        sidebarPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        logoLabel.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        logoLabel.setForeground(new java.awt.Color(255, 255, 255));
        logoLabel.setText("<html>Inventory<br>Management</html>");
        sidebarPanel.add(logoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 25, 170, 60));

        dashboardButton.setBackground(new java.awt.Color(23, 43, 77));
        dashboardButton.setForeground(new java.awt.Color(255, 255, 255));
        dashboardButton.setText("   Dashboard");
        dashboardButton.setBorderPainted(false);
        dashboardButton.setFocusPainted(false);
        sidebarPanel.add(dashboardButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 105, 180, 45));

        productsButton.setBackground(new java.awt.Color(23, 43, 77));
        productsButton.setForeground(new java.awt.Color(255, 255, 255));
        productsButton.setText("   Products");
        productsButton.setFocusPainted(false);
        productsButton.setBorderPainted(false);
        sidebarPanel.add(productsButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 165, 180, 45));

        inventoryButton.setBackground(new java.awt.Color(23, 43, 77));
        inventoryButton.setForeground(new java.awt.Color(255, 255, 255));
        inventoryButton.setText("   Inventory");
        inventoryButton.setFocusPainted(false);
        inventoryButton.setBorderPainted(false);
        sidebarPanel.add(inventoryButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 225, 180, 45));

        salesButton.setBackground(new java.awt.Color(23, 43, 77));
        salesButton.setForeground(new java.awt.Color(255, 255, 255));
        salesButton.setText("   Sales");
        salesButton.setFocusPainted(false);
        salesButton.setBorderPainted(false);
        sidebarPanel.add(salesButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 285, 180, 45));

        transactionsButton.setBackground(new java.awt.Color(23, 43, 77));
        transactionsButton.setForeground(new java.awt.Color(255, 255, 255));
        transactionsButton.setText("   Transactions");
        transactionsButton.setFocusPainted(false);
        transactionsButton.setBorderPainted(false);
        sidebarPanel.add(transactionsButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 345, 180, 45));

        logoutButton.setBackground(new java.awt.Color(23, 43, 77));
        logoutButton.setForeground(new java.awt.Color(255, 255, 255));
        logoutButton.setText("   Logout");
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        sidebarPanel.add(logoutButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 555, 180, 45));

        getContentPane().add(sidebarPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 220, 650));

        contentPanel.setBackground(new java.awt.Color(243, 245, 249));
        contentPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dashboardTitleLabel.setFont(new java.awt.Font("Arial", 1, 28)); // NOI18N
        dashboardTitleLabel.setForeground(new java.awt.Color(23, 43, 77));
        dashboardTitleLabel.setText("Dashboard");
        contentPanel.add(dashboardTitleLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 25, 300, 50));

        getContentPane().add(contentPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 0, 780, 650));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void styleMenuButton(JButton button, String text) {
        button.setBackground(NAVY);
        button.setForeground(Color.WHITE);
        button.setFont(new java.awt.Font("Arial", 0, 15));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setText("   " + text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
    }

    /**
     * Rebuilds the window with a resizable layout (sidebar + screens).
     * Done in code after initComponents() so the NetBeans designer code is untouched.
     */
    private void buildLayout() {
        categoriesButton = new JButton();
        suppliersButton = new JButton();
        customersButton = new JButton();
        reportsButton = new JButton();

        menu.put("Dashboard", dashboardButton);
        menu.put("Products", productsButton);
        menu.put("Categories", categoriesButton);
        menu.put("Suppliers", suppliersButton);
        menu.put("Customers", customersButton);
        menu.put("Inventory", inventoryButton);
        menu.put("Sales", salesButton);
        menu.put("Transactions", transactionsButton);
        menu.put("Reports", reportsButton);

        sidebarPanel.removeAll();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(22, 16, 18, 16));
        sidebarPanel.setPreferredSize(new Dimension(220, 0));

        logoLabel.setText("<html>Retail Store<br>Management</html>");
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(logoLabel);
        sidebarPanel.add(javax.swing.Box.createVerticalStrut(24));

        for (Map.Entry<String, JButton> e : menu.entrySet()) {
            final String name = e.getKey();
            JButton b = e.getValue();
            styleMenuButton(b, name);
            sizeMenuButton(b);
            b.addActionListener(evt -> showScreen(name));
            sidebarPanel.add(b);
            sidebarPanel.add(javax.swing.Box.createVerticalStrut(6));
        }

        sidebarPanel.add(javax.swing.Box.createVerticalGlue());
        styleMenuButton(logoutButton, "Logout");
        sizeMenuButton(logoutButton);
        logoutButton.addActionListener(evt -> logout());
        sidebarPanel.add(logoutButton);

        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout(0, 14));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));
        contentPanel.add(dashboardTitleLabel, BorderLayout.NORTH);
        cardHost.setOpaque(false);
        contentPanel.add(cardHost, BorderLayout.CENTER);

        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sidebarPanel, BorderLayout.WEST);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
    }

    private void sizeMenuButton(JButton b) {
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setPreferredSize(new Dimension(188, 42));
        b.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
    }

    /** Creates a screen the first time it is opened, then refreshes its data every time. */
    private void showScreen(String name) {
        Component screen = screens.get(name);
        if (screen == null) {
            screen = createScreen(name);
            screens.put(name, screen);
            cardHost.add(screen, name);
        }
        dashboardTitleLabel.setText(name);
        cards.show(cardHost, name);
        for (Map.Entry<String, JButton> e : menu.entrySet()) {
            e.getValue().setBackground(e.getKey().equals(name) ? ACTIVE : NAVY);
        }
        if (screen instanceof Refreshable) {
            ((Refreshable) screen).refresh();
        }
    }

    private Component createScreen(String name) {
        switch (name) {
            case "Dashboard":
                return new DashboardPanel();
            default:
                return new PlaceholderPanel(name);   // replaced as each module is built
        }
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Log out of the system?", "Logout",
                JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new MainFrame().setVisible(true);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentPanel;
    private javax.swing.JButton dashboardButton;
    private javax.swing.JLabel dashboardTitleLabel;
    private javax.swing.JButton inventoryButton;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JButton logoutButton;
    private javax.swing.JButton productsButton;
    private javax.swing.JButton salesButton;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JButton transactionsButton;
    // End of variables declaration//GEN-END:variables

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(DashboardFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new DashboardFrame().setVisible(true));
    }
}
