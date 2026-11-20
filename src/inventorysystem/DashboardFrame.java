package inventorysystem;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class DashboardFrame extends javax.swing.JFrame {

    private static final Color NAVY = new Color(23, 43, 77);
    private static final Color CONTENT_BG = new Color(243, 245, 249);

    public DashboardFrame() {
        initComponents();
        setTitle("RSIS | Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sidebarPanel = new JPanel();
        contentPanel = new JPanel();
        logoLabel = new JLabel();
        dashboardButton = new JButton();
        productsButton = new JButton();
        inventoryButton = new JButton();
        salesButton = new JButton();
        transactionsButton = new JButton();
        logoutButton = new JButton();
        dashboardTitleLabel = new JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        sidebarPanel.setBackground(NAVY);
        sidebarPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        logoLabel.setFont(new java.awt.Font("Arial", 1, 26));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setText("RSIS");
        sidebarPanel.add(logoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(25, 25, 170, 45));

        styleMenuButton(dashboardButton, "Dashboard");
        sidebarPanel.add(dashboardButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 105, 180, 45));

        styleMenuButton(productsButton, "Products");
        sidebarPanel.add(productsButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 165, 180, 45));

        styleMenuButton(inventoryButton, "Inventory");
        sidebarPanel.add(inventoryButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 225, 180, 45));

        styleMenuButton(salesButton, "Sales");
        sidebarPanel.add(salesButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 285, 180, 45));

        styleMenuButton(transactionsButton, "Transactions");
        sidebarPanel.add(transactionsButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 345, 180, 45));

        styleMenuButton(logoutButton, "Logout");
        sidebarPanel.add(logoutButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 555, 180, 45));

        getContentPane().add(sidebarPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 220, 650));

        contentPanel.setBackground(CONTENT_BG);
        contentPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        dashboardTitleLabel.setFont(new java.awt.Font("Arial", 1, 28));
        dashboardTitleLabel.setForeground(NAVY);
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private JLabel logoLabel;
    private JButton dashboardButton;
    private JButton productsButton;
    private JButton inventoryButton;
    private JButton salesButton;
    private JButton transactionsButton;
    private JButton logoutButton;
    private JLabel dashboardTitleLabel;
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
