package inventorysystem;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class MainFrame extends JFrame {
    private static final Color NAVY = new Color(23, 43, 77);
    private static final Color TEAL = new Color(13, 148, 136);
    private static final Color TEXT = new Color(35, 45, 60);
    private static final Color MUTED = new Color(120, 130, 145);

    public MainFrame() {
        initComponents();
        setTitle("RSIS | Login");
        setLocationRelativeTo(null);
    }

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both your username and password.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
                "The login interface is ready. Account authentication still needs to be connected.",
                "Login", JOptionPane.INFORMATION_MESSAGE);
    }

    @SuppressWarnings("unchecked")
    //GEN-BEGIN:initComponents
    private void initComponents() {
        loginPanelbackground = new GradientPanel();
        loginCard = new SmoothEdges();
        brandLabel = new javax.swing.JLabel();
        headingLabel = new javax.swing.JLabel();
        subtitleLabel = new javax.swing.JLabel();
        usernameLabel = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        passwordLabel = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        loginButton = new javax.swing.JButton();
        footerLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        loginPanelbackground.setLayout(new GridBagLayout());

        loginCard.setBackground(Color.WHITE);
        loginCard.setBorder(BorderFactory.createEmptyBorder(28, 38, 28, 38));
        loginCard.setPreferredSize(new java.awt.Dimension(390, 440));

        brandLabel.setFont(new Font("Arial", Font.BOLD, 15));
        brandLabel.setForeground(TEAL);
        brandLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        brandLabel.setText("RSIS");

        headingLabel.setFont(new Font("Arial", Font.BOLD, 26));
        headingLabel.setForeground(NAVY);
        headingLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        headingLabel.setText("Welcome Back");

        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitleLabel.setForeground(MUTED);
        subtitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        subtitleLabel.setText("Sign in to your inventory account");

        usernameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        usernameLabel.setForeground(TEXT);
        usernameLabel.setText("Username");

        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setForeground(TEXT);
        usernameField.setBackground(new Color(248, 250, 252));
        usernameField.setCaretColor(TEAL);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 234)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));

        passwordLabel.setFont(new Font("Arial", Font.BOLD, 12));
        passwordLabel.setForeground(TEXT);
        passwordLabel.setText("Password");

        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setForeground(TEXT);
        passwordField.setBackground(new Color(248, 250, 252));
        passwordField.setCaretColor(TEAL);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 234)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)));

        loginButton.setBackground(TEAL);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setText("LOG IN");
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        loginButton.addActionListener(evt -> loginButtonActionPerformed(evt));

        footerLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        footerLabel.setForeground(MUTED);
        footerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        footerLabel.setText("Retail Store Inventory System");

        javax.swing.GroupLayout cardLayout = new javax.swing.GroupLayout(loginCard);
        loginCard.setLayout(cardLayout);
        cardLayout.setHorizontalGroup(
            cardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cardLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(cardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(brandLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(headingLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(subtitleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(usernameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(usernameField)
                    .addComponent(passwordLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(passwordField)
                    .addComponent(loginButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(footerLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        cardLayout.setVerticalGroup(
            cardLayout.createSequentialGroup()
                .addComponent(brandLabel, 20, 20, 20).addGap(12)
                .addComponent(headingLabel, 32, 32, 32).addGap(5)
                .addComponent(subtitleLabel, 20, 20, 20).addGap(27)
                .addComponent(usernameLabel, 18, 18, 18).addGap(6)
                .addComponent(usernameField, 42, 42, 42).addGap(15)
                .addComponent(passwordLabel, 18, 18, 18).addGap(6)
                .addComponent(passwordField, 42, 42, 42).addGap(23)
                .addComponent(loginButton, 44, 44, 44).addGap(18)
                .addComponent(footerLabel, 16, 16, 16)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.gridx = 0;
        cardGbc.gridy = 0;
        cardGbc.anchor = GridBagConstraints.CENTER;
        loginPanelbackground.add(loginCard, cardGbc);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(loginPanelbackground, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(loginPanelbackground, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE));
        setSize(new java.awt.Dimension(900, 600));
    }
    //GEN-END:initComponents

    //GEN-BEGIN:variables
    private javax.swing.JLabel brandLabel, headingLabel, subtitleLabel, usernameLabel, passwordLabel, footerLabel;
    private javax.swing.JTextField usernameField;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JButton loginButton;
    private javax.swing.JPanel loginCard, loginPanelbackground;
    //GEN-END:variables

    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
