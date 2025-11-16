package main.control;

import main.data.DataLoader;
import main.entity.CompanyRepresentative;
import main.entity.User;
import main.entity.enums.AccountStatus;
import main.util.InputHandler;


/**
/**
 * Authenticator - Handles login/logout and password updates.
 * <p>
 * Accepts ID or email depending on role and enforces account approval for
 * company representatives.
 * <p>
 * BCE Mapping: Control (C).
 */
public class Authenticator {
    private final UserManager userManager;
    private User currentUser;
    private InputHandler inputHandler;

    public Authenticator(UserManager userManager) {
        this.userManager = userManager;
        this.inputHandler = new InputHandler();
    }

    // --- Login outcomes for GUI ---
    public enum LoginResult {
        SUCCESS,
        USER_NOT_FOUND,
        WRONG_PASSWORD,
        REP_EMAIL_REQUIRED,
        REP_NOT_APPROVED,
        REP_REJECTED
    }

    // Attempt login (ID or Email depending on role)
    public boolean login(String idOrEmail, String password) {
        User user = null;

        // --- Try finding by ID first ---
        user = userManager.findUserById(idOrEmail);

        // --- If not found by ID, try finding by email (for company reps) ---
        if (user == null) {
            user = userManager.findUserByEmail(idOrEmail);
        }

        // --- If still not found, invalid credentials ---
        if (user == null) {
            System.out.println("Invalid credentials: No such user found.");
            return false;
        }

        // --- Verify password ---
        if (!user.getPassword().equals(password)) {
            System.out.println("Incorrect password.");
            return false;
        }

        // --- Business rule: Company Rep must use email ---
        if (user instanceof CompanyRepresentative rep) {
            // If login input was not email but ID, block it
            if (!idOrEmail.equalsIgnoreCase(rep.getEmail())) {
                System.out.println("Company Representatives must log in using their company email.");
                return false;
            }
            if (rep.getAccountStatus() != AccountStatus.APPROVED) {
                System.out.println("\nCurrent status: " + rep.getAccountStatus());
                if(rep.getAccountStatus() == AccountStatus.REJECTED){
                    System.out.println("Your account has been rejected");
                    return false;
                }
                else{
                    System.out.println("Your account has not been approved yet.");
                    return false;
                }
            }
        }

        // --- Set current user ---
        currentUser = user;
        System.out.println("Login successful. Welcome, " + user.getName() + "!");
        return true;
    }

    /**
     * Attempt login without printing; intended for GUI to show precise messages.
     * On SUCCESS, sets currentUser similarly to {@link #login(String, String)}.
     */
    public LoginResult attemptLogin(String idOrEmail, String password) {
        User user = userManager.findUserById(idOrEmail);
        if (user == null) {
            user = userManager.findUserByEmail(idOrEmail);
        }
        if (user == null) {
            return LoginResult.USER_NOT_FOUND;
        }
        if (!user.getPassword().equals(password)) {
            return LoginResult.WRONG_PASSWORD;
        }
        if (user instanceof CompanyRepresentative rep) {
            if (!idOrEmail.equalsIgnoreCase(rep.getEmail())) {
                return LoginResult.REP_EMAIL_REQUIRED;
            }
            if (rep.getAccountStatus() == AccountStatus.REJECTED) {
                return LoginResult.REP_REJECTED;
            }
            if (rep.getAccountStatus() != AccountStatus.APPROVED) {
                return LoginResult.REP_NOT_APPROVED;
            }
        }
        currentUser = user;
        return LoginResult.SUCCESS;
    }


    // Log out
    public void logout() {
        if (currentUser != null) {
            System.out.println("Logged out: " + currentUser.getName());
            currentUser = null;
        } else {
            System.out.println("No user currently logged in.");
        }
    }

    // Change password
    public void changePassword(User currentUser) {
        if (currentUser == null) {
            System.out.println("No user is currently logged in.");
            return;
        }

        String oldPw = inputHandler.readPassword("Enter current password: ");
        String newPw = inputHandler.readPassword("Enter new password: ");
        if (newPw.length() < 4) {
            System.out.println("⚠ Password too short. Minimum 4 characters.");
            return;
        }
        String confirmPw = inputHandler.readPassword("Confirm new password: ");

        if (!newPw.equals(confirmPw)) {
            System.out.println("❌ New passwords do not match.");
            return;
        }

        if (currentUser.changePassword(oldPw, newPw)) {
            DataLoader.updateUser(currentUser, userManager);
            System.out.println("✅ Password updated successfully and saved.");
        } else {
            System.out.println("❌ Incorrect current password.");
        }
    }


    // Get current logged-in user
    public User getCurrentUser() {
        return currentUser;
    }
}
