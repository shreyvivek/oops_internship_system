package main.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Registration panel for Company Representative registration
 */
public class RegistrationPanel extends JPanel {
    private final MainGUI mainGUI;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField companyField;
    private JTextField deptField;
    private JTextField positionField;
    private JButton registerButton;
    private JButton backButton;

    public RegistrationPanel(MainGUI mainGUI) {
        this.mainGUI = mainGUI;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        JLabel titleLabel = new JLabel("Company Representative Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Center panel with registration form
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(245, 245, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        int row = 0;
        
        // Name
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nameField = new JTextField(25);
        nameField.setPreferredSize(new Dimension(300, 30));
        formPanel.add(nameField, gbc);

        row++;
        // Email
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Company Email:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        emailField = new JTextField(25);
        emailField.setPreferredSize(new Dimension(300, 30));
        formPanel.add(emailField, gbc);

        row++;
        // Company
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Company Name:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        companyField = new JTextField(25);
        companyField.setPreferredSize(new Dimension(300, 30));
        formPanel.add(companyField, gbc);

        row++;
        // Department
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        deptField = new JTextField(25);
        deptField.setPreferredSize(new Dimension(300, 30));
        formPanel.add(deptField, gbc);

        row++;
        // Position
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Position:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        positionField = new JTextField(25);
        positionField.setPreferredSize(new Dimension(300, 30));
        formPanel.add(positionField, gbc);

        row++;
        // Buttons
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setPreferredSize(new Dimension(120, 40));
        registerButton.setBackground(new Color(70, 130, 180));
        registerButton.setForeground(Color.BLACK);
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(e -> handleRegistration());

        backButton = new JButton("Back to Login");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setPreferredSize(new Dimension(120, 40));
        backButton.addActionListener(e -> mainGUI.showLogin());

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        formPanel.add(buttonPanel, gbc);

        centerPanel.add(formPanel);
        add(centerPanel, BorderLayout.CENTER);

        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(245, 245, 250));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel infoLabel = new JLabel(
                "<html><center>Note: Your account will need to be approved by Career Center Staff before you can login.</center></html>"
        );
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoPanel.add(infoLabel);
        add(infoPanel, BorderLayout.SOUTH);
    }

    private void handleRegistration() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String company = companyField.getText().trim();
        String dept = deptField.getText().trim();
        String position = positionField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || company.isEmpty() || dept.isEmpty() || position.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate email format
        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address.",
                    "Invalid Email",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        mainGUI.getAppContext().companyRepManager.registerNewRep(name, email, company, dept, position);
        JOptionPane.showMessageDialog(this,
                "Registration successful! Please wait for Career Center Staff approval.",
                "Registration Success",
                JOptionPane.INFORMATION_MESSAGE);
        mainGUI.showLogin();
    }

    public void clearFields() {
        nameField.setText("");
        emailField.setText("");
        companyField.setText("");
        deptField.setText("");
        positionField.setText("");
        nameField.requestFocus();
    }
}

