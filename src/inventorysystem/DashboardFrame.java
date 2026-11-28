package inventorysystem;

import inventorysystem.ui.CategoriesPanel;
import inventorysystem.ui.CustomersPanel;
import inventorysystem.ui.DashboardPanel;
import inventorysystem.ui.InventoryPanel;
import inventorysystem.ui.PlaceholderPanel;
import inventorysystem.ui.ProductsPanel;
import inventorysystem.ui.Refreshable;
import inventorysystem.ui.ReportsPanel;
import inventorysystem.ui.SalesPanel;
import inventorysystem.ui.SuppliersPanel;
import inventorysystem.ui.TransactionsPanel;
import inventorysystem.ui.Theme;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Main window: sidebar menu on the left, the selected screen on the right.
 * The layout is built with the NetBeans GUI builder (Design tab).
 * To add a screen: add a menu button in the designer, add it to buildMenu(),
 * and add a case in createScreen().
 */
public class DashboardFrame extends javax.swing.JFrame {

    private static final Color NAVY = new Color(23, 43, 77);
    private static final Color ACTIVE = Theme.TEAL;

    private CardLayout cards;
    private final Map<String, JButton> menu = new LinkedHashMap<>();
    private final Map<String, Component> screens = new LinkedHashMap<>();

    public DashboardFrame() {
        initComponents();
        setTitle("RMS | Retail Store Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        makeMaximizable();
        cards = (CardLayout) cardHost.getLayout();
        buildMenu();
        showScreen("Dashboard");
    }

    /**
     * Lets the window be maximized (full screen). The blue sidebar is pinned to
     * the left edge at full height; the designed content area keeps its size and
     * stays centered in the remaining space, so nothing gets stretched.
     */
    private void makeMaximizable() {
        Container designed = getContentPane();
        Dimension size = designed.getPreferredSize();
        Dimension sidebarSize = new Dimension(220, 720);
        Dimension contentSize = new Dimension(960, 720);

        designed.remove(sidebarPanel);
        designed.remove(contentPanel);
        sidebarPanel.setPreferredSize(sidebarSize);
        contentPanel.setPreferredSize(contentSize);

        JPanel middle = new JPanel(new GridBagLayout());
        middle.setBackground(Theme.PAGE_BG);
        middle.add(contentPanel);

        JPanel stage = new JPanel(new BorderLayout());
        stage.setBackground(Theme.PAGE_BG);
        stage.add(sidebarPanel, BorderLayout.WEST);
        stage.add(middle, BorderLayout.CENTER);

        setContentPane(stage);
        setResizable(true);
        setMinimumSize(new Dimension(size.width + 16, size.height + 39));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /** Connects each sidebar button to its screen name. */
    private void buildMenu() {
        menu.put("Dashboard", dashboardButton);
        menu.put("Products", productsButton);
        menu.put("Categories", categoriesButton);
        menu.put("Suppliers", suppliersButton);
        menu.put("Customers", customersButton);
        menu.put("Inventory", inventoryButton);
        menu.put("Sales", salesButton);
        menu.put("Transactions", transactionsButton);
        menu.put("Reports", reportsButton);

        for (Map.Entry<String, JButton> e : menu.entrySet()) {
            final String name = e.getKey();
            JButton b = e.getValue();
            b.setOpaque(true);
            b.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
            b.addActionListener(evt -> showScreen(name));
        }
        logoutButton.setOpaque(true);
        logoutButton.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        logoutButton.addActionListener(evt -> logout());
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
            case "Products":
                return new ProductsPanel();
            case "Categories":
                return new CategoriesPanel();
            case "Suppliers":
                return new SuppliersPanel();
            case "Customers":
                return new CustomersPanel();
            case "Inventory":
                return new InventoryPanel();
            case "Transactions":
                return new TransactionsPanel();
            case "Reports":
                return new ReportsPanel();
            case "Sales":
                return new SalesPanel();
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sidebarPanel = new javax.swing.JPanel();
        logoLabel = new javax.swing.JLabel();
        dashboardButton = new javax.swing.JButton();
        productsButton = new javax.swing.JButton();
        categoriesButton = new javax.swing.JButton();
        suppliersButton = new javax.swing.JButton();
        customersButton = new javax.swing.JButton();
        inventoryButton = new javax.swing.JButton();
        salesButton = new javax.swing.JButton();
        transactionsButton = new javax.swing.JButton();
        reportsButton = new javax.swing.JButton();
        logoutButton = new javax.swing.JButton();
        contentPanel = new javax.swing.JPanel();
        dashboardTitleLabel = new javax.swing.JLabel();
        cardHost = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        sidebarPanel.setBackground(new java.awt.Color(23, 43, 77));
        sidebarPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        logoLabel.setForeground(new java.awt.Color(255, 255, 255));
        logoLabel.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        logoLabel.setText("<html>Retail Store<br>Management</html>");
        sidebarPanel.add(logoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 25, 170, 60));

        dashboardButton.setBackground(new java.awt.Color(23, 43, 77));
        dashboardButton.setForeground(new java.awt.Color(255, 255, 255));
        dashboardButton.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        dashboardButton.setText("   Dashboard");
        dashboardButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        dashboardButton.setBorderPainted(false);
        dashboardButton.setFocusPainted(false);
        sidebarPanel.add(dashboardButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 105, 180, 42));

        productsButton.setBackground(new java.awt.Color(23, 43, 77));
        productsButton.setForeground(new java.awt.Color(255, 255, 255));
        productsButton.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        productsButton.setText("   Products");
        productsButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        productsButton.setBorderPainted(false);
        productsButton.setFocusPainted(false);
        sidebarPanel.add(productsButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 261, 180, 42));

        categoriesButton.setBackground(new java.awt.Color(23, 43, 77));
        categoriesButton.setForeground(new java.awt.Color(255, 255, 255));
        categoriesButton.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        categoriesButton.setText("   Categories");
        categoriesButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        categoriesButton.setBorderPainted(false);
        categoriesButton.setFocusPainted(false);
        sidebarPanel.add(categoriesButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 313, 180, 42));

        suppliersButton.setBackground(new java.awt.Color(23, 43, 77));
        suppliersButton.setForeground(new java.awt.Color(255, 255, 255));
        suppliersButton.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        suppliersButton.setText("   Suppliers");
        suppliersButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        suppliersButton.setBorderPainted(false);
        suppliersButton.setFocusPainted(false);
        sidebarPanel.add(suppliersButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 469, 180, 42));

        customersButton.setBackground(new java.awt.Color(23, 43, 77));
        customersButton.setForeground(new java.awt.Color(255, 255, 255));
        customersButton.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        customersButton.setText("   Customers");
        customersButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        customersButton.setBorderPainted(false);
        customersButton.setFocusPainted(false);
        sidebarPanel.add(customersButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 417, 180, 42));

        inventoryButton.setBackground(new java.awt.Color(23, 43, 77));
        inventoryButton.setForeground(new java.awt.Color(255, 255, 255));
        inventoryButton.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        inventoryButton.setText("   Inventory");
        inventoryButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        inventoryButton.setBorderPainted(false);
        inventoryButton.setFocusPainted(false);
        sidebarPanel.add(inventoryButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 365, 180, 42));

        salesButton.setBackground(new java.awt.Color(23, 43, 77));
        salesButton.setForeground(new java.awt.Color(255, 255, 255));
        salesButton.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        salesButton.setText("   Sales");
        salesButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        salesButton.setBorderPainted(false);
        salesButton.setFocusPainted(false);
        sidebarPanel.add(salesButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 157, 180, 42));

        transactionsButton.setBackground(new java.awt.Color(23, 43, 77));
        transactionsButton.setForeground(new java.awt.Color(255, 255, 255));
        transactionsButton.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        transactionsButton.setText("   Transactions");
        transactionsButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        transactionsButton.setBorderPainted(false);
        transactionsButton.setFocusPainted(false);
        sidebarPanel.add(transactionsButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 209, 180, 42));

        reportsButton.setBackground(new java.awt.Color(23, 43, 77));
        reportsButton.setForeground(new java.awt.Color(255, 255, 255));
        reportsButton.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        reportsButton.setText("   Reports");
        reportsButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        reportsButton.setBorderPainted(false);
        reportsButton.setFocusPainted(false);
        sidebarPanel.add(reportsButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 521, 180, 42));

        logoutButton.setBackground(new java.awt.Color(23, 43, 77));
        logoutButton.setForeground(new java.awt.Color(255, 255, 255));
        logoutButton.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        logoutButton.setText("   Logout");
        logoutButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        sidebarPanel.add(logoutButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 650, 180, 42));

        getContentPane().add(sidebarPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 220, 720));

        contentPanel.setBackground(new java.awt.Color(243, 245, 249));
        contentPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dashboardTitleLabel.setForeground(new java.awt.Color(23, 43, 77));
        dashboardTitleLabel.setFont(new java.awt.Font("Arial", 1, 28)); // NOI18N
        dashboardTitleLabel.setText("Dashboard");
        contentPanel.add(dashboardTitleLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 22, 600, 44));

        cardHost.setOpaque(false);
        cardHost.setLayout(new java.awt.CardLayout());
        contentPanel.add(cardHost, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 900, 600));

        getContentPane().add(contentPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 0, 960, 720));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel cardHost;
    private javax.swing.JButton categoriesButton;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JButton customersButton;
    private javax.swing.JButton dashboardButton;
    private javax.swing.JLabel dashboardTitleLabel;
    private javax.swing.JButton inventoryButton;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JButton logoutButton;
    private javax.swing.JButton productsButton;
    private javax.swing.JButton reportsButton;
    private javax.swing.JButton salesButton;
    private javax.swing.JPanel sidebarPanel;
    private javax.swing.JButton suppliersButton;
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
