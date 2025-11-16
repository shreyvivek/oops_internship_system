package main.gui;

import main.control.AppContext;
import main.entity.Internship;
import main.entity.enums.InternshipLevel;
import main.entity.enums.InternshipStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Dialog (Boundary) for generating and displaying internship reports with filters.
 */
public class ReportDialog extends JDialog {
    private final AppContext app;
    private JTable reportTable;
    private DefaultTableModel reportTableModel;
    private JComboBox<String> statusCombo;
    private JTextField majorField;
    private JComboBox<String> levelCombo;

    public ReportDialog(JFrame parent, AppContext app) {
        super(parent, "Generate Internship Report", true);
        this.app = app;
        initializeDialog();
    }

    private void initializeDialog() {
        setLayout(new BorderLayout());
        setSize(900, 600);
        setLocationRelativeTo(getParent());

        // Filter panel
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder("Report Filters"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Status
        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        statusCombo = new JComboBox<>(new String[]{"ALL", "PENDING", "APPROVED", "REJECTED", "FILLED"});
        filterPanel.add(statusCombo, gbc);

        // Major
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        filterPanel.add(new JLabel("Preferred Major:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        majorField = new JTextField(15);
        filterPanel.add(majorField, gbc);

        // Level
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        filterPanel.add(new JLabel("Level:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        levelCombo = new JComboBox<>(new String[]{"ALL", "BASIC", "INTERMEDIATE", "ADVANCED"});
        filterPanel.add(levelCombo, gbc);

        // Generate button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton generateButton = new JButton("Generate Report");
        generateButton.addActionListener(e -> generateReport());
        filterPanel.add(generateButton, gbc);

        add(filterPanel, BorderLayout.NORTH);

        // Report table
        String[] columnNames = {"ID", "Title", "Company", "Created By", "Created On", "Level", "Status", "Major", "Slots", "Visible"};
        reportTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        reportTable = new JTable(reportTableModel);
        reportTable.setFont(new Font("Arial", Font.PLAIN, 11));
        reportTable.setRowHeight(20);
        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Report Results"));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with summary and close button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        summaryPanel.add(new JLabel("Total: 0 | Approved: 0 | Pending: 0 | Rejected: 0"));
        summaryPanel.setName("summaryPanel");
        bottomPanel.add(summaryPanel, BorderLayout.CENTER);

        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void generateReport() {
        reportTableModel.setRowCount(0);

        String statusFilter = (String) statusCombo.getSelectedItem();
        if (statusFilter.equals("ALL")) statusFilter = null;
        
        final String majorFilter = majorField.getText().trim().isEmpty() ? null : majorField.getText().trim();

        String levelFilter = (String) levelCombo.getSelectedItem();
        if (levelFilter.equals("ALL")) levelFilter = null;

        // Get filtered internships
        List<Internship> internships = app.internshipManager.getAllInternships();
        
        if (statusFilter != null) {
            InternshipStatus status = InternshipStatus.valueOf(statusFilter);
            internships = internships.stream()
                .filter(i -> i.getStatus() == status)
                .toList();
        }

        if (majorFilter != null) {
            internships = internships.stream()
                .filter(i -> app.internshipManager.majorsMatch(i.getPreferredMajor(), majorFilter))
                .toList();
        }

        if (levelFilter != null) {
            InternshipLevel level = InternshipLevel.valueOf(levelFilter);
            internships = internships.stream()
                .filter(i -> i.getLevel() == level)
                .toList();
        }

        // Populate table
        for (Internship i : internships) {
            reportTableModel.addRow(new Object[]{
                i.getInternshipId(),
                i.getTitle(),
                i.getCompanyName(),
                i.getRepresentativeId(),
                i.getCreatedDate(),
                i.getLevel(),
                i.getStatus(),
                i.getPreferredMajor(),
                i.getSlotsLeft() + "/" + i.getNumSlots(),
                i.isVisible() ? "Yes" : "No"
            });
        }

        // Update summary
        long approved = internships.stream().filter(i -> i.getStatus() == InternshipStatus.APPROVED).count();
        long pending = internships.stream().filter(i -> i.getStatus() == InternshipStatus.PENDING).count();
        long rejected = internships.stream().filter(i -> i.getStatus() == InternshipStatus.REJECTED).count();

        // Find and update summary label
        JPanel summaryPanel = (JPanel) ((JPanel) getContentPane().getComponent(2)).getComponent(0);
        for (Component c : summaryPanel.getComponents()) {
            if (c instanceof JLabel) {
                ((JLabel) c).setText(
                    String.format("Total: %d | Approved: %d | Pending: %d | Rejected: %d",
                        internships.size(), approved, pending, rejected)
                );
                break;
            }
        }
    }
}

