package main.gui;

import main.control.AppContext;
import main.entity.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Dialog for viewing all users in the system
 */
public class ViewUsersDialog extends JDialog {
    private final AppContext app;
    private JTable userTable;
    private DefaultTableModel userTableModel;

    public ViewUsersDialog(JFrame parent, AppContext app) {
        super(parent, "All Users", true);
        this.app = app;
        initializeDialog();
        loadUsers();
    }

    private void initializeDialog() {
        setLayout(new BorderLayout());
        setSize(800, 500);
        setLocationRelativeTo(getParent());

        String[] columnNames = {"User ID", "Name", "Email", "Role", "Additional Info"};
        userTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userTableModel);
        userTable.setFont(new Font("Arial", Font.PLAIN, 12));
        userTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("All Users in System"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadUsers() {
        userTableModel.setRowCount(0);
        
        List<User> users = app.userManager.getAllUsers();
        
        for (User user : users) {
            String additionalInfo = "";
            
            if (user instanceof Student student) {
                additionalInfo = "Year " + student.getYearOfStudy() + " | " + student.getMajor();
            } else if (user instanceof CompanyRepresentative rep) {
                additionalInfo = rep.getCompanyName() + " | " + rep.getDepartment() + " | Status: " + rep.getAccountStatus();
            } else if (user instanceof CareerCenterStaff staff) {
                additionalInfo = "Department: " + staff.getStaffDepartment();
            }
            
            userTableModel.addRow(new Object[]{
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                additionalInfo
            });
        }
    }
}

