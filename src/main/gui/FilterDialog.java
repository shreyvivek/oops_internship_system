package main.gui;

import main.entity.FilterSettings;
import main.entity.enums.InternshipLevel;
import main.entity.enums.InternshipStatus;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog (Boundary) for configuring internship filters and sorting.
 */
public class FilterDialog extends JDialog {
    private final FilterSettings filters;
    private final boolean allowStatus;
    private final boolean allowMajor;
    private final boolean allowLevel;
    private final boolean allowSort;
    private final boolean allowVisibility;
    
    private JComboBox<String> statusCombo;
    private JTextField majorField;
    private JComboBox<String> levelCombo;
    private JComboBox<String> visibilityCombo;
    private JComboBox<String> sortCombo;

    public FilterDialog(JFrame parent, FilterSettings filters, 
                       boolean allowStatus, boolean allowMajor, boolean allowLevel,
                       boolean allowSort, boolean allowVisibility) {
        super(parent, "Filter Settings", true);
        this.filters = filters;
        this.allowStatus = allowStatus;
        this.allowMajor = allowMajor;
        this.allowLevel = allowLevel;
        this.allowSort = allowSort;
        this.allowVisibility = allowVisibility;
        
        initializeDialog();
        loadCurrentFilters();
    }

    private void initializeDialog() {
        setLayout(new BorderLayout());
        setSize(400, 400);
        setLocationRelativeTo(getParent());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        if (allowStatus) {
            gbc.gridx = 0;
            gbc.gridy = row;
            formPanel.add(new JLabel("Status:"), gbc);
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            statusCombo = new JComboBox<>(new String[]{"ANY", "PENDING", "APPROVED", "REJECTED", "FILLED"});
            formPanel.add(statusCombo, gbc);
            row++;
        }

        if (allowMajor) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            formPanel.add(new JLabel("Preferred Major:"), gbc);
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            majorField = new JTextField(15);
            formPanel.add(majorField, gbc);
            row++;
        }

        if (allowLevel) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            formPanel.add(new JLabel("Level:"), gbc);
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            levelCombo = new JComboBox<>(new String[]{"ANY", "BASIC", "INTERMEDIATE", "ADVANCED"});
            formPanel.add(levelCombo, gbc);
            row++;
        }

        if (allowVisibility) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            formPanel.add(new JLabel("Visibility:"), gbc);
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            visibilityCombo = new JComboBox<>(new String[]{"ALL", "Visible Only", "Hidden Only"});
            formPanel.add(visibilityCombo, gbc);
            row++;
        }

        if (allowSort) {
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            formPanel.add(new JLabel("Sort By:"), gbc);
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            sortCombo = new JComboBox<>(new String[]{"Alphabetical", "Closing Date", "Opening Date"});
            formPanel.add(sortCombo, gbc);
            row++;
        }

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(e -> applyFilters());
        JButton clearButton = new JButton("Clear All");
        clearButton.addActionListener(e -> clearFilters());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(applyButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadCurrentFilters() {
        if (allowStatus && statusCombo != null) {
            InternshipStatus status = filters.getStatus();
            if (status == null) {
                statusCombo.setSelectedIndex(0); // ANY
            } else {
                statusCombo.setSelectedItem(status.toString());
            }
        }

        if (allowMajor && majorField != null) {
            String major = filters.getPreferredMajor();
            majorField.setText(major != null ? major : "");
        }

        if (allowLevel && levelCombo != null) {
            InternshipLevel level = filters.getLevel();
            if (level == null) {
                levelCombo.setSelectedIndex(0); // ANY
            } else {
                levelCombo.setSelectedItem(level.toString());
            }
        }

        if (allowVisibility && visibilityCombo != null) {
            Boolean visible = filters.getVisible();
            if (visible == null) {
                visibilityCombo.setSelectedIndex(0); // ALL
            } else if (visible) {
                visibilityCombo.setSelectedIndex(1); // Visible Only
            } else {
                visibilityCombo.setSelectedIndex(2); // Hidden Only
            }
        }

        if (allowSort && sortCombo != null) {
            String sortBy = filters.getSortBy();
            if (sortBy == null || sortBy.equals("title")) {
                sortCombo.setSelectedIndex(0); // Alphabetical
            } else if (sortBy.equals("closingDate")) {
                sortCombo.setSelectedIndex(1); // Closing Date
            } else {
                sortCombo.setSelectedIndex(2); // Opening Date
            }
        }
    }

    private void applyFilters() {
        if (allowStatus && statusCombo != null) {
            String selected = (String) statusCombo.getSelectedItem();
            if (selected.equals("ANY")) {
                filters.setStatus(null);
            } else {
                filters.setStatus(InternshipStatus.valueOf(selected));
            }
        }

        if (allowMajor && majorField != null) {
            String major = majorField.getText().trim();
            filters.setPreferredMajor(major.isEmpty() ? null : major);
        }

        if (allowLevel && levelCombo != null) {
            String selected = (String) levelCombo.getSelectedItem();
            if (selected.equals("ANY")) {
                filters.setLevel(null);
            } else {
                filters.setLevel(InternshipLevel.valueOf(selected));
            }
        }

        if (allowVisibility && visibilityCombo != null) {
            int index = visibilityCombo.getSelectedIndex();
            if (index == 0) {
                filters.setVisible(null); // ALL
            } else if (index == 1) {
                filters.setVisible(true); // Visible Only
            } else {
                filters.setVisible(false); // Hidden Only
            }
        }

        if (allowSort && sortCombo != null) {
            int index = sortCombo.getSelectedIndex();
            if (index == 0) {
                filters.setSortBy("title");
            } else if (index == 1) {
                filters.setSortBy("closingDate");
            } else {
                filters.setSortBy("openingDate");
            }
        }

        dispose();
    }

    private void clearFilters() {
        filters.clear();
        loadCurrentFilters();
    }
}

