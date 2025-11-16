package main.gui;

import main.control.AppContext;
import main.entity.*;
import main.entity.enums.ApplicationStatus;
import main.entity.enums.InternshipStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Company Representative dashboard GUI panel
 */
public class CompanyRepPanel extends JPanel {
    private final MainGUI mainGUI;
    private final AppContext app;
    private final CompanyRepresentative rep;
    private final FilterSettings filters;
    
    private JTable internshipTable;
    private DefaultTableModel internshipTableModel;
    private JTable applicationTable;
    private DefaultTableModel applicationTableModel;

    public CompanyRepPanel(MainGUI mainGUI, AppContext app, CompanyRepresentative rep) {
        this.mainGUI = mainGUI;
        this.app = app;
        this.rep = rep;
        this.filters = new FilterSettings();
        initializePanel();
        refreshInternshipTable();
        refreshApplicationTable();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel welcomeLabel = new JLabel("Welcome, " + rep.getName() + " (Company Representative)");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JLabel infoLabel = new JLabel(rep.getCompanyName() + " | " + rep.getDepartment());
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoLabel.setForeground(Color.WHITE);
        headerPanel.add(infoLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Main content with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        tabbedPane.addTab("My Internships", createInternshipPanel());
        tabbedPane.addTab("Applications", createApplicationPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        toolbar.setBackground(new Color(245, 245, 250));
        toolbar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton         createButton = new JButton("Create Internship");
        createButton.setFont(new Font("Arial", Font.BOLD, 14));
        createButton.setBackground(new Color(34, 139, 34));
        createButton.setForeground(Color.BLACK);
        createButton.addActionListener(e -> createInternship());
        toolbar.add(createButton);

        JButton filterButton = new JButton("Set Filters");
        filterButton.addActionListener(e -> openFilterDialog());
        toolbar.add(filterButton);

        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(e -> changePassword());
        toolbar.add(changePasswordButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> mainGUI.logout());
        toolbar.add(logoutButton);

        add(toolbar, BorderLayout.SOUTH);
    }

    private JPanel createInternshipPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columnNames = {"ID", "Title", "Company", "Level", "Major", "Status", "Visible", "Slots Left"};
        internshipTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        internshipTable = new JTable(internshipTableModel);
        internshipTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        internshipTable.setFont(new Font("Arial", Font.PLAIN, 12));
        internshipTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(internshipTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("My Internships"));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton editButton = new JButton("Edit Selected");
        editButton.addActionListener(e -> editInternship());
        buttonPanel.add(editButton);

        JButton toggleButton = new JButton("Toggle Visibility");
        toggleButton.addActionListener(e -> toggleVisibility());
        buttonPanel.add(toggleButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshInternshipTable());
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createApplicationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columnNames = {"App ID", "Student Name", "Student ID", "Major", "Year", "Internship ID", "Status"};
        applicationTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        applicationTable = new JTable(applicationTableModel);
        applicationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        applicationTable.setFont(new Font("Arial", Font.PLAIN, 12));
        applicationTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(applicationTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Applications"));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton         approveButton = new JButton("Approve");
        approveButton.setBackground(new Color(34, 139, 34));
        approveButton.setForeground(Color.BLACK);
        approveButton.addActionListener(e -> approveApplication());
        buttonPanel.add(approveButton);

        JButton         rejectButton = new JButton("Reject");
        rejectButton.setBackground(new Color(220, 20, 60));
        rejectButton.setForeground(Color.BLACK);
        rejectButton.addActionListener(e -> rejectApplication());
        buttonPanel.add(rejectButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshApplicationTable());
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshInternshipTable() {
        internshipTableModel.setRowCount(0);
        List<Internship> internships = app.internshipManager.getFilteredInternships(filters);
        
        for (Internship i : internships) {
            if (i.getRepresentativeId().equalsIgnoreCase(rep.getUserId())) {
                internshipTableModel.addRow(new Object[]{
                    i.getInternshipId(),
                    i.getTitle(),
                    i.getCompanyName(),
                    i.getLevel(),
                    i.getPreferredMajor(),
                    i.getStatus(),
                    i.isVisible() ? "Yes" : "No",
                    i.getSlotsLeft()
                });
            }
        }
    }

    private void refreshApplicationTable() {
        applicationTableModel.setRowCount(0);
        List<Application> applications = app.applicationManager.getApplicationsForRep(rep.getUserId());
        
        for (Application a : applications) {
            applicationTableModel.addRow(new Object[]{
                a.getApplicationId(),
                a.getStudentName(),
                a.getStudentId(),
                a.getStudentMajor(),
                a.getStudentYear(),
                a.getInternshipId(),
                a.getStatus()
            });
        }
    }

    private void createInternship() {
        CreateInternshipDialog dialog = new CreateInternshipDialog((JFrame) SwingUtilities.getWindowAncestor(this), app, rep);
        dialog.setVisible(true);
        refreshInternshipTable();
    }

    private void editInternship() {
        int selectedRow = internshipTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an internship to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String internshipId = (String) internshipTableModel.getValueAt(selectedRow, 0);
        Internship internship = app.internshipManager.findInternshipById(internshipId);
        
        if (internship == null) {
            JOptionPane.showMessageDialog(this, "Internship not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (internship.getStatus() != InternshipStatus.PENDING) {
            JOptionPane.showMessageDialog(this, "Cannot edit. Internship has already been " + internship.getStatus(), "Cannot Edit", JOptionPane.WARNING_MESSAGE);
            return;
        }

        EditInternshipDialog dialog = new EditInternshipDialog((JFrame) SwingUtilities.getWindowAncestor(this), app, rep, internship);
        dialog.setVisible(true);
        refreshInternshipTable();
    }

    private void toggleVisibility() {
        int selectedRow = internshipTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an internship.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String internshipId = (String) internshipTableModel.getValueAt(selectedRow, 0);
        Internship internship = app.internshipManager.findInternshipById(internshipId);
        
        if (internship == null) {
            JOptionPane.showMessageDialog(this, "Internship not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean newVisibility = !internship.isVisible();
        app.internshipManager.toggleVisibilityForRep(rep.getUserId(), internshipId, newVisibility);
        JOptionPane.showMessageDialog(this, "Visibility set to " + (newVisibility ? "ON" : "OFF"), "Success", JOptionPane.INFORMATION_MESSAGE);
        refreshInternshipTable();
    }

    private void approveApplication() {
        int selectedRow = applicationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an application.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String appId = (String) applicationTableModel.getValueAt(selectedRow, 0);
        Application application = app.applicationManager.getAllApplications().stream()
            .filter(a -> a.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);

        if (application == null || application.getStatus() != ApplicationStatus.PENDING) {
            JOptionPane.showMessageDialog(this, "Only pending applications can be approved.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Approve this application?", "Confirm Approval", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            app.applicationManager.approveApplication(application);
            JOptionPane.showMessageDialog(this, "Application approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshApplicationTable();
        }
    }

    private void rejectApplication() {
        int selectedRow = applicationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an application.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String appId = (String) applicationTableModel.getValueAt(selectedRow, 0);
        Application application = app.applicationManager.getAllApplications().stream()
            .filter(a -> a.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);

        if (application == null || application.getStatus() != ApplicationStatus.PENDING) {
            JOptionPane.showMessageDialog(this, "Only pending applications can be rejected.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Reject this application?", "Confirm Rejection", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            app.applicationManager.rejectApplication(application);
            JOptionPane.showMessageDialog(this, "Application rejected.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshApplicationTable();
        }
    }

    private void openFilterDialog() {
        FilterDialog dialog = new FilterDialog((JFrame) SwingUtilities.getWindowAncestor(this), filters, true, true, true, true, true);
        dialog.setVisible(true);
        refreshInternshipTable();
    }

    private void changePassword() {
        ChangePasswordDialog dialog = new ChangePasswordDialog((JFrame) SwingUtilities.getWindowAncestor(this), app, rep);
        dialog.setVisible(true);
    }
}

