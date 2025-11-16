package main;

import main.boundary.MainMenu;
import main.control.AppContext;
import main.data.DataLoader;
import main.gui.MainGUI;

import javax.swing.*;

/**
 * Main entry point for the Internship Placement Management System.
 * <p>
 * This bootstrap class starts either the CLI (Boundary) menus or the Swing GUI
 * (Boundary) and wires them to the Control layer via {@link main.control.AppContext}.
 * <p>
 * BCE Mapping: Boundary (bootstrap) → Control (AppContext) → Entity/Data.
 * 
 * Usage:
 *   java InternshipPlacementSystem           - Shows mode selection dialog
 *   java InternshipPlacementSystem --cli     - Runs in CLI mode
 *   java InternshipPlacementSystem --gui     - Runs in GUI mode
 */
public class InternshipPlacementSystem {
    public static void main(String[] args) {
        AppContext app = new AppContext(); // Shared managers and data
        
        // Determine mode from command line arguments
        boolean useGUI = false;
        boolean useCLI = false;
        
        for (String arg : args) {
            if (arg.equals("--gui") || arg.equals("-g")) {
                useGUI = true;
                break;
            } else if (arg.equals("--cli") || arg.equals("-c")) {
                useCLI = true;
                break;
            }
        }
        
        // If no argument provided, show selection dialog (GUI by default)
        if (!useGUI && !useCLI) {
            // Show mode selection dialog
            SwingUtilities.invokeLater(() -> {
                int choice = JOptionPane.showOptionDialog(
                    null,
                    "Select Interface Mode:",
                    "Internship Placement System",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"GUI Mode", "CLI Mode"},
                    "GUI Mode"
                );
                
                if (choice == 0) {
                    // GUI Mode
                    MainGUI gui = new MainGUI(app);
                    gui.setVisible(true);
                } else if (choice == 1) {
                    // CLI Mode
                    runCLI(app);
                }
                // If user closed dialog, exit
            });
            // Wait for GUI to start (for GUI mode)
            try {
                Thread.sleep(100); // Brief delay to allow dialog to appear
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else if (useGUI) {
            // GUI Mode - run on EDT
            SwingUtilities.invokeLater(() -> {
                MainGUI gui = new MainGUI(app);
                gui.setVisible(true);
            });
        } else {
            // CLI Mode
            runCLI(app);
        }
        
        // Add shutdown hook to save data
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DataLoader.saveAllUsers(app.userManager);
            DataLoader.saveInternships(app.internshipRepository);
            System.out.println("💾 All data saved. Goodbye!");
        }));
    }
    
    private static void runCLI(AppContext app) {
        MainMenu mainMenu = new MainMenu(app);
        mainMenu.start();
        
        // Save on exit
        DataLoader.saveAllUsers(app.userManager);
        DataLoader.saveInternships(app.internshipRepository);
        System.out.println("💾 All data saved. Goodbye!");
    }
}
