package main.gui;

import main.control.AppContext;
import main.entity.*;
import main.entity.enums.AccountStatus;
import main.entity.enums.ApplicationStatus;
import main.entity.enums.InternshipStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Career Center Staff dashboard GUI panel
 */
public class StaffPanel extends JPanel {
    private final MainGUI mainGUI;
    private final AppContext app;
    private final CareerCenterStaff staff;
    private final FilterSettings filters;
    
    private JTable internshipTable;
    private DefaultTableModel internshipTableModel;
    private JTable companyRepTable;
    private DefaultTableModel companyRepTableModel;
    private JTable withdrawalTable;
    private DefaultTableModel withdrawalTableModel;

    public StaffPanel(MainGUI mainGUI, AppContext app, CareerCenterStaff staff) {
        this.mainGUI = mainGUI;
        this.app = app;
        this.staff = staff;
        this.filters = new FilterSettings();
        initializePanel();
        refreshInternshipTable();
        refreshCompanyRepTable();
        refreshWithdrawalTable();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel welcomeLabel = new JLabel("Welcome, " + staff.getName() + " (Career Center Staff)");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JLabel infoLabel = new JLabel("Department: " + staff.getStaffDepartment());
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoLabel.setForeground(Color.WHITE);
        headerPanel.add(infoLabel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Main content with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        tabbedPane.addTab("All Internships", createInternshipPanel());
        tabbedPane.addTab("Company Rep Approvals", createCompanyRepPanel());
        tabbedPane.addTab("Withdrawal Requests", createWithdrawalPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        toolbar.setBackground(new Color(245, 245, 250));
        toolbar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton         generateReportButton = new JButton("Generate Report");
        generateReportButton.setFont(new Font("Arial", Font.BOLD, 14));
        generateReportButton.setBackground(new Color(70, 130, 180));
        generateReportButton.setForeground(Color.BLACK);
        generateReportButton.addActionListener(e -> generateReport());
        toolbar.add(generateReportButton);

        JButton filterButton = new JButton("Set Filters");
        filterButton.addActionListener(e -> openFilterDialog());
        toolbar.add(filterButton);

        JButton viewUsersButton = new JButton("View All Users");
        viewUsersButton.addActionListener(e -> viewAllUsers());
        toolbar.add(viewUsersButton);

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

        String[] columnNames = {"ID", "Title", "Company", "Rep ID", "Level", "Major", "Status", "Visible", "Slots"};
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
        scrollPane.setBorder(BorderFactory.createTitledBorder("All Internships"));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton approveButton = new JButton("Approve Selected");
        approveButton.setBackground(new Color(34, 139, 34));
        approveButton.setForeground(Color.BLACK);
        approveButton.addActionListener(e -> approveInternship());
        buttonPanel.add(approveButton);

        JButton rejectButton = new JButton("Reject Selected");
        rejectButton.setBackground(new Color(220, 20, 60));
        rejectButton.setForeground(Color.BLACK);
        rejectButton.addActionListener(e -> rejectInternship());
        buttonPanel.add(rejectButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshInternshipTable());
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCompanyRepPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columnNames = {"Email", "Name", "Company", "Department", "Position", "Status"};
        companyRepTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        companyRepTable = new JTable(companyRepTableModel);
        companyRepTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        companyRepTable.setFont(new Font("Arial", Font.PLAIN, 12));
        companyRepTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(companyRepTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Pending Company Representative Accounts"));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton approveButton = new JButton("Approve Selected");
        approveButton.setBackground(new Color(34, 139, 34));
        approveButton.setForeground(Color.BLACK);
        approveButton.addActionListener(e -> approveCompanyRep());
        buttonPanel.add(approveButton);

        JButton rejectButton = new JButton("Reject Selected");
        rejectButton.setBackground(new Color(220, 20, 60));
        rejectButton.setForeground(Color.BLACK);
        rejectButton.addActionListener(e -> rejectCompanyRep());
        buttonPanel.add(rejectButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshCompanyRepTable());
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createWithdrawalPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        String[] columnNames = {"App ID", "Student Name", "Student ID", "Internship ID", "Status"};
        withdrawalTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        withdrawalTable = new JTable(withdrawalTableModel);
        withdrawalTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        withdrawalTable.setFont(new Font("Arial", Font.PLAIN, 12));
        withdrawalTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(withdrawalTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Pending Withdrawal Requests"));
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton approveButton = new JButton("Approve Withdrawal");
        approveButton.setBackground(new Color(34, 139, 34));
        approveButton.setForeground(Color.BLACK);
        approveButton.addActionListener(e -> approveWithdrawal());
        buttonPanel.add(approveButton);

        JButton rejectButton = new JButton("Reject Withdrawal");
        rejectButton.setBackground(new Color(220, 20, 60));
        rejectButton.setForeground(Color.BLACK);
        rejectButton.addActionListener(e -> rejectWithdrawal());
        buttonPanel.add(rejectButton);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshWithdrawalTable());
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshInternshipTable() {
        internshipTableModel.setRowCount(0);
        List<Internship> internships = app.internshipManager.getFilteredInternships(filters);
        
        for (Internship i : internships) {
            internshipTableModel.addRow(new Object[]{
                i.getInternshipId(),
                i.getTitle(),
                i.getCompanyName(),
                i.getRepresentativeId(),
                i.getLevel(),
                i.getPreferredMajor(),
                i.getStatus(),
                i.isVisible() ? "Yes" : "No",
                i.getSlotsLeft() + "/" + i.getNumSlots()
            });
        }
    }

    private void refreshCompanyRepTable() {
        companyRepTableModel.setRowCount(0);
        List<CompanyRepresentative> pendingReps = app.userManager.getPendingCompanyReps();
        
        for (CompanyRepresentative rep : pendingReps) {
            companyRepTableModel.addRow(new Object[]{
                rep.getEmail(),
                rep.getName(),
                rep.getCompanyName(),
                rep.getDepartment(),
                rep.getPosition(),
                rep.getAccountStatus()
            });
        }
    }

    private void refreshWithdrawalTable() {
        withdrawalTableModel.setRowCount(0);
        List<Application> withdrawals = app.applicationManager.getPendingWithdrawals();
        
        for (Application a : withdrawals) {
            withdrawalTableModel.addRow(new Object[]{
                a.getApplicationId(),
                a.getStudentName(),
                a.getStudentId(),
                a.getInternshipId(),
                a.getStatus()
            });
        }
    }

    private void approveInternship() {
        int selectedRow = internshipTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an internship.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String internshipId = (String) internshipTableModel.getValueAt(selectedRow, 0);
        Internship internship = app.internshipManager.findInternshipById(internshipId);
        
        if (internship == null || internship.getStatus() != InternshipStatus.PENDING) {
            JOptionPane.showMessageDialog(this, "Only pending internships can be approved.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Approve this internship?", "Confirm Approval", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            app.internshipManager.approveInternship(internship);
            JOptionPane.showMessageDialog(this, "Internship approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshInternshipTable();
        }
    }

    private void rejectInternship() {
        int selectedRow = internshipTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an internship.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String internshipId = (String) internshipTableModel.getValueAt(selectedRow, 0);
        Internship internship = app.internshipManager.findInternshipById(internshipId);
        
        if (internship == null || internship.getStatus() != InternshipStatus.PENDING) {
            JOptionPane.showMessageDialog(this, "Only pending internships can be rejected.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Reject this internship?", "Confirm Rejection", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            app.internshipManager.rejectInternship(internship);
            JOptionPane.showMessageDialog(this, "Internship rejected.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshInternshipTable();
        }
    }

    private void approveCompanyRep() {
        int selectedRow = companyRepTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a company representative.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String email = (String) companyRepTableModel.getValueAt(selectedRow, 0);
        User user = app.userManager.findUserByEmail(email);
        if (!(user instanceof CompanyRepresentative)) {
            JOptionPane.showMessageDialog(this, "User not found or not a company representative.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        CompanyRepresentative rep = (CompanyRepresentative) user;
        
        if (rep.getAccountStatus() != AccountStatus.PENDING) {
            JOptionPane.showMessageDialog(this, "Only pending accounts can be approved.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Approve this company representative account?", "Confirm Approval", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            app.userManager.approveCompanyRep(rep);
            JOptionPane.showMessageDialog(this, "Account approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshCompanyRepTable();
        }
    }

    private void rejectCompanyRep() {
        int selectedRow = companyRepTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a company representative.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String email = (String) companyRepTableModel.getValueAt(selectedRow, 0);
        User user = app.userManager.findUserByEmail(email);
        if (!(user instanceof CompanyRepresentative)) {
            JOptionPane.showMessageDialog(this, "User not found or not a company representative.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        CompanyRepresentative rep = (CompanyRepresentative) user;
        
        if (rep.getAccountStatus() != AccountStatus.PENDING) {
            JOptionPane.showMessageDialog(this, "Only pending accounts can be rejected.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Reject this company representative account?", "Confirm Rejection", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            app.userManager.rejectCompanyRep(rep);
            JOptionPane.showMessageDialog(this, "Account rejected.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshCompanyRepTable();
        }
    }

    private void approveWithdrawal() {
        int selectedRow = withdrawalTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a withdrawal request.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String appId = (String) withdrawalTableModel.getValueAt(selectedRow, 0);
        Application application = app.applicationManager.getAllApplications().stream()
            .filter(a -> a.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);

        if (application == null || application.getStatus() != ApplicationStatus.WITHDRAWAL_PENDING) {
            JOptionPane.showMessageDialog(this, "Only pending withdrawal requests can be approved.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Approve this withdrawal request?", "Confirm Approval", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            app.applicationManager.approveWithdrawal(application);
            JOptionPane.showMessageDialog(this, "Withdrawal approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshWithdrawalTable();
        }
    }

    private void rejectWithdrawal() {
        int selectedRow = withdrawalTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a withdrawal request.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String appId = (String) withdrawalTableModel.getValueAt(selectedRow, 0);
        Application application = app.applicationManager.getAllApplications().stream()
            .filter(a -> a.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);

        if (application == null || application.getStatus() != ApplicationStatus.WITHDRAWAL_PENDING) {
            JOptionPane.showMessageDialog(this, "Only pending withdrawal requests can be rejected.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Reject this withdrawal request?", "Confirm Rejection", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            app.applicationManager.rejectWithdrawal(application);
            JOptionPane.showMessageDialog(this, "Withdrawal rejected.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshWithdrawalTable();
        }
    }

    private void generateReport() {
        ReportDialog dialog = new ReportDialog((JFrame) SwingUtilities.getWindowAncestor(this), app);
        dialog.setVisible(true);
    }

    private void openFilterDialog() {
        FilterDialog dialog = new FilterDialog((JFrame) SwingUtilities.getWindowAncestor(this), filters, true, false, false, true, true);
        dialog.setVisible(true);
        refreshInternshipTable();
    }

    private void viewAllUsers() {
        ViewUsersDialog dialog = new ViewUsersDialog((JFrame) SwingUtilities.getWindowAncestor(this), app);
        dialog.setVisible(true);
    }

    private void changePassword() {
        ChangePasswordDialog dialog = new ChangePasswordDialog((JFrame) SwingUtilities.getWindowAncestor(this), app, staff);
        dialog.setVisible(true);
    }
}

