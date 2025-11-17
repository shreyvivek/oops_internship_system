package main.gui;

import main.control.AppContext;
import main.entity.*;
import main.entity.enums.ApplicationStatus;
import main.entity.enums.InternshipLevel;
import main.entity.enums.InternshipStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Student dashboard (Boundary) for browsing internships, applying,
 * reviewing applications, accepting offers, and requesting withdrawals.
 */
public class StudentPanel extends JPanel {
    private final MainGUI mainGUI;
    private final AppContext app;
    private final Student student;
    private final FilterSettings filters;
    
    private JTable internshipTable;
    private DefaultTableModel internshipTableModel;
    private JTable applicationTable;
    private DefaultTableModel applicationTableModel;
    private JButton applyButton;
    private JButton withdrawButton;
    private JButton acceptOfferButton;
    private JButton changePasswordButton;
    private JButton logoutButton;
    private JButton filterButton;

    public StudentPanel(MainGUI mainGUI, AppContext app, Student student) {
        this.mainGUI = mainGUI;
        this.app = app;
        this.student = student;
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

        JLabel welcomeLabel = new JLabel("Welcome, " + student.getName() + " (Student)");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JLabel infoLabel = new JLabel("Year " + student.getYearOfStudy() + " | " + student.getMajor());
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoLabel.setForeground(Color.WHITE);
        headerPanel.add(infoLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Main content with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        // Available Internships Tab
        tabbedPane.addTab("Available Internships", createInternshipPanel());
        
        // My Applications Tab
        tabbedPane.addTab("My Applications", createApplicationPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        toolbar.setBackground(new Color(245, 245, 250));
        toolbar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        filterButton = new JButton("Set Filters");
        filterButton.addActionListener(e -> openFilterDialog());
        toolbar.add(filterButton);

        changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(e -> changePassword());
        toolbar.add(changePasswordButton);

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> mainGUI.logout());
        toolbar.add(logoutButton);

        add(toolbar, BorderLayout.SOUTH);
    }

    private JPanel createInternshipPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Table
        String[] columnNames = {"ID", "Title", "Company", "Level", "Major", "Opening Date", "Closing Date", "Slots Left"};
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
        internshipTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(internshipTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available Internships"));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        applyButton = new JButton("Apply for Selected");
        applyButton.setFont(new Font("Arial", Font.BOLD, 14));
        applyButton.setBackground(new Color(70, 130, 180));
        applyButton.setForeground(Color.BLACK);
        applyButton.addActionListener(e -> applyForInternship());
        buttonPanel.add(applyButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshInternshipTable());
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createApplicationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Table
        String[] columnNames = {"App ID", "Internship ID", "Title", "Status", "Date Applied"};
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
        applicationTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(applicationTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("My Applications"));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        acceptOfferButton = new JButton("Accept Offer");
        acceptOfferButton.setFont(new Font("Arial", Font.BOLD, 14));
        acceptOfferButton.setBackground(new Color(34, 139, 34));
        acceptOfferButton.setForeground(Color.BLACK);
        acceptOfferButton.addActionListener(e -> acceptOffer());
        buttonPanel.add(acceptOfferButton);

        withdrawButton = new JButton("Withdraw Application");
        withdrawButton.setFont(new Font("Arial", Font.PLAIN, 14));
        withdrawButton.addActionListener(e -> withdrawApplication());
        buttonPanel.add(withdrawButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            refreshApplicationTable();
            refreshInternshipTable();
        });
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshInternshipTable() {
        internshipTableModel.setRowCount(0);
        
        List<Internship> internships = app.internshipManager.getFilteredInternships(filters);
        
        for (Internship i : internships) {
            // Students only see approved, visible, major-compatible, and level-eligible internships
            // Exclude FILLED internships
            boolean visibleOk = i.isVisible() && i.getStatus() == InternshipStatus.APPROVED;
            boolean majorOk = app.internshipManager.majorsMatch(student.getMajor(), i.getPreferredMajor());
            boolean levelOk = (student.getYearOfStudy() <= 2 && i.getLevel() == InternshipLevel.BASIC)
                    || (student.getYearOfStudy() >= 3);
            
            // Check dates
            LocalDate closing = LocalDate.parse(i.getClosingDate());
            LocalDate opening = LocalDate.parse(i.getOpeningDate());
            boolean dateOk = !LocalDate.now().isBefore(opening) && !LocalDate.now().isAfter(closing);
            
            if (visibleOk && majorOk && levelOk && dateOk && i.hasAvailableSlots() && i.getStatus() != InternshipStatus.FILLED) {
                internshipTableModel.addRow(new Object[]{
                    i.getInternshipId(),
                    i.getTitle(),
                    i.getCompanyName(),
                    i.getLevel(),
                    i.getPreferredMajor(),
                    i.getOpeningDate(),
                    i.getClosingDate(),
                    i.getSlotsLeft()
                });
            }
        }
    }

    private void refreshApplicationTable() {
        applicationTableModel.setRowCount(0);
        
        List<Application> applications = app.applicationManager.getMyApplications(student.getUserId());
        
        for (Application application : applications) {
            Internship internship = app.internshipManager.findInternshipById(application.getInternshipId());
            String title = internship != null ? internship.getTitle() : application.getInternshipId();
            
            applicationTableModel.addRow(new Object[]{
                application.getApplicationId(),
                application.getInternshipId(),
                title,
                application.getStatus(),
                application.getAppliedDate()
            });
        }
        
        // Check if there are successful offers
        boolean hasSuccessfulOffer = app.applicationManager.hasSuccessfulOffer(student);
        acceptOfferButton.setEnabled(hasSuccessfulOffer);
        
        // Enable withdraw for pending (before confirmation) or accepted (after confirmation) applications
        withdrawButton.setEnabled(applications.stream()
            .anyMatch(a -> a.getStatus() == ApplicationStatus.PENDING || a.getStatus() == ApplicationStatus.ACCEPTED));
    }

    private void applyForInternship() {
        int selectedRow = internshipTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an internship to apply for.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String internshipId = (String) internshipTableModel.getValueAt(selectedRow, 0);
        // Check duplicate before attempting apply
        if (app.applicationManager.hasApplied(student.getUserId(), internshipId)) {
            JOptionPane.showMessageDialog(this,
                    "You have already applied for this internship.",
                    "Already Applied",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Show confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to apply for this internship?",
                "Confirm Application",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Use the existing application manager logic
            String errorMsg = app.applicationManager.applyForInternship(student, internshipId);
            
            if (errorMsg == null) {
                // Success - no error message means application was created
                JOptionPane.showMessageDialog(this,
                        "Application submitted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Show the specific error message
                JOptionPane.showMessageDialog(this,
                        errorMsg.replace("❌ ", ""), // Remove emoji for cleaner GUI
                        "Application Failed",
                        JOptionPane.WARNING_MESSAGE);
            }
            
            refreshApplicationTable();
            refreshInternshipTable();
        }
    }

    private void acceptOffer() {
        int selectedRow = applicationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an application to accept.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String appId = (String) applicationTableModel.getValueAt(selectedRow, 0);
        Application application = this.app.applicationManager.getMyApplications(student.getUserId()).stream()
            .filter(a -> a.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);

        if (application == null || application.getStatus() != ApplicationStatus.SUCCESSFUL) {
            JOptionPane.showMessageDialog(this,
                    "You can only accept successful offers.",
                    "Invalid Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Accepting this offer will withdraw all other applications.\nProceed?",
                "Confirm Acceptance",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String errorMsg = this.app.applicationManager.acceptOffer(student, appId);
            if (errorMsg == null) {
                JOptionPane.showMessageDialog(this,
                        "Offer accepted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        errorMsg,
                        "Cannot Accept Offer",
                        JOptionPane.WARNING_MESSAGE);
            }
            refreshApplicationTable();
            refreshInternshipTable();
        }
    }

    private void withdrawApplication() {
        int selectedRow = applicationTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an application to withdraw.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String appId = (String) applicationTableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to request withdrawal for this application?",
                "Confirm Withdrawal",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Get the application to check status for better error message
            Application application = app.applicationManager.getMyApplications(student.getUserId()).stream()
                .filter(a -> a.getApplicationId().equals(appId))
                .findFirst()
                .orElse(null);
            
            if (application != null && (application.getStatus() == ApplicationStatus.PENDING || application.getStatus() == ApplicationStatus.ACCEPTED)) {
                app.applicationManager.withdrawApplication(student, appId);
                JOptionPane.showMessageDialog(this,
                        "Withdrawal request submitted. Awaiting staff approval.",
                        "Withdrawal Requested",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshApplicationTable();
            } else {
                // Show specific error message
                String errorMsg = "You can only withdraw pending applications (before confirmation) or accepted applications (after confirmation).";
                if (application != null && (application.getStatus() == ApplicationStatus.WITHDRAWN || application.getStatus() == ApplicationStatus.WITHDRAWAL_PENDING)) {
                    errorMsg = "This application has already been withdrawn or is pending withdrawal approval.";
                }
                JOptionPane.showMessageDialog(this,
                        errorMsg,
                        "Cannot Withdraw",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void openFilterDialog() {
        FilterDialog dialog = new FilterDialog((JFrame) SwingUtilities.getWindowAncestor(this), filters, false, false, true, true, false);
        dialog.setVisible(true);
        refreshInternshipTable();
    }

    private void changePassword() {
        ChangePasswordDialog dialog = new ChangePasswordDialog((JFrame) SwingUtilities.getWindowAncestor(this), app, student);
        dialog.setVisible(true);
    }
}

