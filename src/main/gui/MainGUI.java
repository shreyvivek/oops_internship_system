package main.gui;

import main.control.AppContext;
import main.entity.*;
import main.entity.enums.AccountStatus;

import javax.swing.*;
import java.awt.*;

/**
 * Main GUI entry (Boundary) for the Internship Placement Management System.
 * Hosts role-specific panels and routes actions to the Control layer via AppContext.
 */
public class MainGUI extends JFrame {
    private final AppContext app;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private RegistrationPanel registrationPanel;
    private StudentPanel studentPanel;
    private CompanyRepPanel companyRepPanel;
    private StaffPanel staffPanel;
    private User currentUser;

    public MainGUI(AppContext app) {
        this.app = app;
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("NTU Internship Placement Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Use CardLayout for switching between panels
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create panels
        loginPanel = new LoginPanel(this);
        registrationPanel = new RegistrationPanel(this);

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registrationPanel, "REGISTER");

        add(mainPanel);
        showLogin();
    }

    public void showLogin() {
        cardLayout.show(mainPanel, "LOGIN");
        loginPanel.clearFields();
    }

    public void showRegistration() {
        cardLayout.show(mainPanel, "REGISTER");
        registrationPanel.clearFields();
    }

    public void handleLogin(String userIdOrEmail, String password) {
        String input = userIdOrEmail.trim().toLowerCase();
        boolean loggedIn = app.authenticator.login(input, password);

        if (!loggedIn) {
            JOptionPane.showMessageDialog(this,
                    "Login Failed. Please check your credentials.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        currentUser = app.authenticator.getCurrentUser();

        // Check if Company Rep is approved
        if (currentUser instanceof CompanyRepresentative rep &&
                rep.getAccountStatus() != AccountStatus.APPROVED) {
            JOptionPane.showMessageDialog(this,
                    "Your account is not approved yet. Current status: " + rep.getAccountStatus(),
                    "Account Not Approved",
                    JOptionPane.WARNING_MESSAGE);
            app.authenticator.logout();
            currentUser = null;
            return;
        }

        // Redirect to role-specific panel
        redirectToRolePanel(currentUser);
    }

    private void redirectToRolePanel(User user) {
        // Remove old role panels if they exist
        if (studentPanel != null) mainPanel.remove(studentPanel);
        if (companyRepPanel != null) mainPanel.remove(companyRepPanel);
        if (staffPanel != null) mainPanel.remove(staffPanel);

        if (user instanceof Student student) {
            studentPanel = new StudentPanel(this, app, student);
            mainPanel.add(studentPanel, "STUDENT");
            cardLayout.show(mainPanel, "STUDENT");
        } else if (user instanceof CompanyRepresentative rep) {
            companyRepPanel = new CompanyRepPanel(this, app, rep);
            mainPanel.add(companyRepPanel, "COMPANY_REP");
            cardLayout.show(mainPanel, "COMPANY_REP");
        } else if (user instanceof CareerCenterStaff staff) {
            staffPanel = new StaffPanel(this, app, staff);
            mainPanel.add(staffPanel, "STAFF");
            cardLayout.show(mainPanel, "STAFF");
        }
    }

    public void logout() {
        app.authenticator.logout();
        currentUser = null;
        showLogin();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public AppContext getAppContext() {
        return app;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppContext app = new AppContext();
            MainGUI gui = new MainGUI(app);
            gui.setVisible(true);
        });
    }
}

