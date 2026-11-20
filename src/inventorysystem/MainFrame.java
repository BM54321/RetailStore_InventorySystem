/*
 * Retail Store Inventory System
 */
package inventorysystem;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class MainFrame extends JFrame {

    private static final Color NAVY = new Color(23, 43, 77);
    private static final Color TEAL = new Color(13, 148, 136);
    private static final Color TEXT = new Color(35, 45, 60);
    private static final Color MUTED = new Color(120, 130, 145);

    private JTextField usernameField;
    private JPasswordField passwordField;

    public MainFrame() {
        initComponents();
        buildLoginPage();
    }

    /*
     * Builds the login interface inside the existing gradient background.
     * The NetBeans-generated form setup remains untouched.
     */
    private void buildLoginPage() {
        loginPanelbackground.removeAll();
        loginPanelbackground.setLayout(new GridBagLayout());

        SmoothEdges loginCard = new SmoothEdges();
        loginCard.setBackground(Color.WHITE);
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));
        loginCard.setPreferredSize(new Dimension(390, 440));

        JLabel brand = new JLabel("RSIS");
        brand.setFont(new Font("Arial", Font.BOLD, 15));
        brand.setForeground(TEAL);
        brand.setAlignmentX(CENTER_ALIGNMENT);

        JLabel heading = new JLabel("Welcome Back");
        heading.setFont(new Font("Arial", Font.BOLD, 26));
        heading.setForeground(NAVY);
        heading.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to your inventory account");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        JLabel usernameLabel = createFieldLabel("Username");
        usernameField = new JTextField();
        styleTextField(usernameField);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel passwordLabel = createFieldLabel("Password");
        passwordField = new JPasswordField();
        styleTextField(passwordField);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JButton loginButton = new JButton("Sign In");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(TEAL);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        loginButton.setAlignmentX(CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginButton.addActionListener(e -> handleLogin());

        JLabel footer = new JLabel("Retail Store Inventory System");
        footer.setFont(new Font("Arial", Font.PLAIN, 11));
        footer.setForeground(MUTED);
        footer.setAlignmentX(CENTER_ALIGNMENT);

        loginCard.add(brand);
        loginCard.add(Box.createVerticalStrut(15));
        loginCard.add(heading);
        loginCard.add(Box.createVerticalStrut(7));
        loginCard.add(subtitle);
        loginCard.add(Box.createVerticalStrut(30));
        loginCard.add(usernameLabel);
        loginCard.add(Box.createVerticalStrut(7));
        loginCard.add(usernameField);
        loginCard.add(Box.createVerticalStrut(18));
        loginCard.add(passwordLabel);
        loginCard.add(Box.createVerticalStrut(7));
        loginCard.add(passwordField);
        loginCard.add(Box.createVerticalStrut(27));
        loginCard.add(loginButton);
        loginCard.add(Box.createVerticalStrut(25));
        loginCard.add(footer);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanelbackground.add(loginCard, gbc);

        setTitle("RSIS | Login");
        setSize(900, 600);
        setMinimumSize(new Dimension(760, 520));
        setLocationRelativeTo(null);

        loginPanelbackground.revalidate();
        loginPanelbackground.repaint();
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(TEXT);
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setBackground(new Color(248, 250, 252));
        field.setCaretColor(TEAL);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 234)),
                BorderFactory.createEmptyBorder(0, 12, 0, 12)
        ));
        field.setAlignmentX(LEFT_ALIGNMENT);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both your username and password.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "The login interface is ready. Account authentication still needs to be connected.",
                "Login",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        loginPanelbackground = new GradientPanel();
        jPanel1 = new SmoothEdges();
        jLabel1 = new JLabel();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.WHITE);

        loginPanelbackground.setBackground(NAVY);
        loginPanelbackground.setLayout(new GridBagLayout());

        jPanel1.setBackground(new Color(0, 102, 204));
        jLabel1.setFont(new Font("Arial", Font.BOLD, 24));
        jLabel1.setText("Retail Store Inventory System");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(210, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(loginPanelbackground, javax.swing.GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(loginPanelbackground, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
        );

        pack();
    }
    // </editor-fold>

    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel loginPanelbackground;

    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName())
                    .log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
