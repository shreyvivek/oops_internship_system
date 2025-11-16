package main.gui;

import main.control.AppContext;
import main.entity.CompanyRepresentative;
import main.entity.enums.InternshipLevel;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

/**
 * Dialog (Boundary) for creating a new internship listing.
 */
public class CreateInternshipDialog extends JDialog {
    private final AppContext app;
    private final CompanyRepresentative rep;
    
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> levelCombo;
    private JTextField majorField;
    private JTextField openDateField;
    private JTextField closeDateField;
    private JSpinner slotsSpinner;

    public CreateInternshipDialog(JFrame parent, AppContext app, CompanyRepresentative rep) {
        super(parent, "Create New Internship", true);
        this.app = app;
        this.rep = rep;
        initializeDialog();
    }

    private void initializeDialog() {
        setLayout(new BorderLayout());
        setSize(500, 500);
        setLocationRelativeTo(getParent());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Title
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        titleField = new JTextField(20);
        formPanel.add(titleField, gbc);
        row++;

        // Description
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(descriptionArea), gbc);
        row++;

        // Level
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        formPanel.add(new JLabel("Level:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        levelCombo = new JComboBox<>(new String[]{"BASIC", "INTERMEDIATE", "ADVANCED"});
        formPanel.add(levelCombo, gbc);
        row++;

        // Major
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Preferred Major:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        majorField = new JTextField(20);
        formPanel.add(majorField, gbc);
        row++;

        // Opening Date
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Opening Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        openDateField = new JTextField(LocalDate.now().toString());
        formPanel.add(openDateField, gbc);
        row++;

        // Closing Date
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Closing Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        closeDateField = new JTextField(LocalDate.now().plusMonths(1).toString());
        formPanel.add(closeDateField, gbc);
        row++;

        // Slots
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Number of Slots:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        slotsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        formPanel.add(slotsSpinner, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton createButton = new JButton("Create");
        createButton.addActionListener(e -> createInternship());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void createInternship() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        InternshipLevel level = InternshipLevel.valueOf((String) levelCombo.getSelectedItem());
        String major = majorField.getText().trim();
        String openDate = openDateField.getText().trim();
        String closeDate = closeDateField.getText().trim();
        int slots = (Integer) slotsSpinner.getValue();

        if (title.isEmpty() || description.isEmpty() || major.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate date format
        try {
            LocalDate.parse(openDate);
            LocalDate.parse(closeDate);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if rep has reached limit
        long repCount = app.internshipManager.getAllInternships().stream()
            .filter(i -> i.getRepresentativeId().equalsIgnoreCase(rep.getUserId()))
            .count();

        if (repCount >= 5) {
            JOptionPane.showMessageDialog(this, "You can only post up to 5 internships.", "Limit Reached", JOptionPane.WARNING_MESSAGE);
            return;
        }

        app.internshipManager.createInternship(
            rep.getUserId(),
            rep.getCompanyName(),
            title, description, level, major, openDate, closeDate, slots
        );

        JOptionPane.showMessageDialog(this, "Internship created successfully! Awaiting staff approval.", "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}

