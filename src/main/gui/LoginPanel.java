package main.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Login panel (Boundary) for user authentication and navigation to registration.
 */
public class LoginPanel extends JPanel {
    private final MainGUI mainGUI;
    private JTextField userIdField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton exitButton;

    public LoginPanel(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(0, 100));
        JLabel titleLabel = new JLabel("NTU Internship Placement Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Center panel with login form
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(245, 245, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Login form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel userIdLabel = new JLabel("User ID or Email:");
        userIdLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(userIdLabel, gbc);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        userIdField = new JTextField(20);
        userIdField.setFont(new Font("Arial", Font.PLAIN, 14));
        userIdField.setPreferredSize(new Dimension(300, 35));
        formPanel.add(userIdField, gbc);

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordLabel, gbc);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(300, 35));
        formPanel.add(passwordField, gbc);

        // Buttons panel
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> {
            String userId = userIdField.getText();
            String password = new String(passwordField.getPassword());
            if (userId.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter both User ID and Password.",
                        "Input Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            mainGUI.handleLogin(userId, password);
        });

        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.PLAIN, 14));
        registerButton.setPreferredSize(new Dimension(120, 40));
        registerButton.addActionListener(e -> mainGUI.showRegistration());

        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exitButton.setPreferredSize(new Dimension(120, 40));
        exitButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit?",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(exitButton);
        formPanel.add(buttonPanel, gbc);

        // Add form to center
        centerPanel.add(formPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(245, 245, 250));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JTextArea infoArea = new JTextArea(
                "Default Password: password\n\n" +
                "Student ID Format: U followed by 7 digits and a letter (e.g., U2345123F)\n" +
                "Company Rep ID: Company email address\n" +
                "Staff ID: NTU account"
        );
        infoArea.setFont(new Font("Arial", Font.PLAIN, 12));
        infoArea.setEditable(false);
        infoArea.setBackground(new Color(245, 245, 250));
        infoPanel.add(infoArea);
        add(infoPanel, BorderLayout.SOUTH);

        // Enter key support
        passwordField.addActionListener(e -> loginButton.doClick());
    }

    public void clearFields() {
        userIdField.setText("");
        passwordField.setText("");
        userIdField.requestFocus();
    }
}

