package main.gui;

import main.control.AppContext;
import main.entity.User;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog (Boundary) for securely changing the current user's password.
 */
public class ChangePasswordDialog extends JDialog {
    private final AppContext app;
    private final User user;
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    public ChangePasswordDialog(JFrame parent, AppContext app, User user) {
        super(parent, "Change Password", true);
        this.app = app;
        this.user = user;
        initializeDialog();
    }

    private void initializeDialog() {
        setLayout(new BorderLayout());
        setSize(400, 250);
        setLocationRelativeTo(getParent());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Old Password
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Old Password:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        oldPasswordField = new JPasswordField(15);
        formPanel.add(oldPasswordField, gbc);

        // New Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        newPasswordField = new JPasswordField(15);
        formPanel.add(newPasswordField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        confirmPasswordField = new JPasswordField(15);
        formPanel.add(confirmPasswordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton changeButton = new JButton("Change Password");
        changeButton.addActionListener(e -> changePassword());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(changeButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void changePassword() {
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "New password and confirmation do not match.",
                    "Password Mismatch",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // The changePassword method in Authenticator uses InputHandler which won't work in GUI
        // We need to handle it directly
        if (user.getPassword().equals(oldPassword)) {
            if (newPassword.length() < 4) {
                JOptionPane.showMessageDialog(this,
                        "Password too short. Minimum 4 characters.",
                        "Invalid Password",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            user.setPassword(newPassword);
            main.data.DataLoader.saveAllUsers(app.userManager);
            JOptionPane.showMessageDialog(this,
                    "Password changed successfully! Please login again.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof MainGUI) {
                ((MainGUI) window).logout();
            } else if (window != null) {
                window.dispose();
                // Find the MainGUI frame
                for (Frame frame : Frame.getFrames()) {
                    if (frame instanceof MainGUI) {
                        ((MainGUI) frame).logout();
                        break;
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Old password is incorrect.",
                    "Invalid Password",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

