package main.control;

import main.data.ApplicationRepository;
import main.entity.Application;
import main.entity.Internship;
import main.entity.Student;
import main.entity.enums.ApplicationStatus;
import main.entity.enums.InternshipLevel;
import main.entity.enums.InternshipStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ApplicationManager - Orchestrates the student application workflow.
 * <p>
 * RESPONSIBILITIES:
 * <ul>
 *   <li>Validate and submit applications (eligibility, dates, capacity, duplicates)</li>
 *   <li>List applications for students and company reps</li>
 *   <li>Approve/Reject applications and handle acceptance (auto-withdraw others)</li>
 *   <li>Manage withdrawal requests and staff approvals</li>
 * </ul>
 * <p>
 * BCE Mapping: Control (C).
 */
public class ApplicationManager {
    private final ApplicationRepository appRepo;
    private final InternshipManager internshipMgr;
    private static final int MAX_APPLICATIONS_PER_STUDENT = 3;


    public ApplicationManager(ApplicationRepository appRepo, InternshipManager internshipMgr) {
        this.appRepo = appRepo;
        this.internshipMgr = internshipMgr;
    }

    // --- STUDENT APPLY ---
    // Returns null on success, error message string on failure
    public String applyForInternship(Student student, String internshipId) {
        Internship internship = internshipMgr.findInternshipById(internshipId);
        if (internship == null) {
            String msg = "❌ Internship not found.";
            System.out.println(msg);
            return msg;
        }

        // --- Rule 0: Student cannot apply if they already accepted an offer ---
        boolean hasAcceptedOffer = appRepo.getApplicationsByStudent(student.getUserId()).stream()
                .anyMatch(a -> a.getStatus() == ApplicationStatus.ACCEPTED);

        if (hasAcceptedOffer) {
            String msg = "❌ You have already accepted an internship offer. You cannot apply for new internships.";
            System.out.println(msg);
            return msg;
        }

        // Rule 1: Internship must be visible, approved, and not filled
        if (!internship.isVisible() || internship.getStatus() != InternshipStatus.APPROVED) {
            if (internship.getStatus() == InternshipStatus.FILLED) {
                String msg = "❌ This internship is full. All slots have been filled.";
                System.out.println(msg);
                return msg;
            }
            String msg = "❌ Internship is not open for applications.";
            System.out.println(msg);
            return msg;
        }

        // --- Rule 2: Check student already has 3 active applications (PENDING, SUCCESSFUL, or WITHDRAWAL_PENDING) ---
        long activeApps = appRepo.getApplicationsByStudent(student.getUserId()).stream()
                .filter(a -> a.getStatus() == ApplicationStatus.PENDING 
                          || a.getStatus() == ApplicationStatus.SUCCESSFUL
                          || a.getStatus() == ApplicationStatus.WITHDRAWAL_PENDING)
                .count();

        if (activeApps >= MAX_APPLICATIONS_PER_STUDENT) {
            String msg = "❌ You cannot apply for more than " + MAX_APPLICATIONS_PER_STUDENT + " internships at once. Please withdraw an existing application before applying for a new one.";
            System.out.println(msg);
            return msg;
        }

        //  Rule 3: No applications before opening and after closing date
        LocalDate closing = LocalDate.parse(internship.getClosingDate());
        LocalDate opening = LocalDate.parse(internship.getOpeningDate());

        if (LocalDate.now().isBefore(opening)) {
            String msg = "❌ The application period has not opened yet.";
            System.out.println(msg);
            return msg;
        }
        if (LocalDate.now().isAfter(closing)) {
            String msg = "❌ The application period has closed.";
            System.out.println(msg);
            return msg;
        }
        //  Rule 4: Applications should respect the Major Rules
        if (!internshipMgr.majorsMatch(student.getMajor(), internship.getPreferredMajor())) {
            String msg = String.format("❌ You cannot apply. Internship is restricted to %s majors.", internship.getPreferredMajor());
            System.out.println(msg);
            return msg;
        }


        //  Rule 5: Applications should respect the Level rules
        boolean levelAllowed = (student.getYearOfStudy() <= 2 && internship.getLevel() == InternshipLevel.BASIC)
                || (student.getYearOfStudy()  >= 3); // Year 3+ can apply to any level
        if (!levelAllowed) {
            String msg = "❌ You are not eligible to apply for this internship level.";
            System.out.println(msg);
            return msg;
        }

        // Rule 6 : Check slots left
        if (!internship.hasAvailableSlots()) {
            String msg = "❌ This internship has no remaining slots.";
            System.out.println(msg);
            return msg;
        }

        //  Rule 7: Prevent duplicate application for same internship (only check active applications)
        //  Students can re-apply after withdrawing from an internship
        for (Application existing : appRepo.getApplicationsByStudent(student.getUserId())) {
            if (existing.getInternshipId().equalsIgnoreCase(internshipId)) {
                // Only block if it's an active application (not withdrawn)
                if (existing.getStatus() != ApplicationStatus.WITHDRAWN && 
                    existing.getStatus() != ApplicationStatus.UNSUCCESSFUL) {
                    String msg = "❌ You already applied for this internship.";
                    System.out.println(msg);
                    return msg;
                }
            }
        }

        //  Create new application
        String appId = generateAppId();
        String date = LocalDate.now().toString();
        Application app = new Application(
                appId,
                student.getUserId(),
                student.getName(),
                student.getMajor(),
                student.getYearOfStudy(),
                internshipId,
                date,
                ApplicationStatus.PENDING
        );
        appRepo.addApplication(app);
        appRepo.saveApplications();
        System.out.println("✅ Application submitted successfully!");
        return null; // Success
    }

    /**
     * Helper to check if a student has an active application to a given internship.
     * Students can re-apply after withdrawing from an internship.
     */
    public boolean hasApplied(String studentId, String internshipId) {
        for (Application existing : appRepo.getApplicationsByStudent(studentId)) {
            if (existing.getInternshipId().equalsIgnoreCase(internshipId)) {
                // Only return true if it's an active application (not withdrawn/unsuccessful)
                if (existing.getStatus() != ApplicationStatus.WITHDRAWN && 
                    existing.getStatus() != ApplicationStatus.UNSUCCESSFUL) {
                    return true;
                }
            }
        }
        return false;
    }

    public void approveApplication(Application a) {
        Internship internship = internshipMgr.findInternshipById(a.getInternshipId());
        if (internship == null) return;

        if (!internship.hasAvailableSlots()) {
            System.out.printf("⚠ Internship '%s' is full.%n", internship.getTitle());
            return;
        }

        // Mark as successful; capacity is only consumed upon student acceptance.
        a.setStatus(ApplicationStatus.SUCCESSFUL);
        saveApplications();
    }

    public void rejectApplication(Application a) {
        a.setStatus(ApplicationStatus.UNSUCCESSFUL);
        saveApplications();
    }

    // COMPANY REP REVIEWS APPLICATIONS

    public List<Application> getPendingApplicationsForRep(String repId) {
        List<Application> result = new ArrayList<>();
        for (Internship i : internshipMgr.getAllInternships()) {
            if (i.getRepresentativeId().equalsIgnoreCase(repId)) {
                for (Application a : appRepo.getApplicationsByInternship(i.getInternshipId())) {
                    if (a.getStatus() == ApplicationStatus.PENDING) result.add(a);
                }
            }
        }
        return result;
    }

    public List<Application> getApplicationsForRep(String repId) {
        List<Application> result = new ArrayList<>();

        // Go through all internships created by this rep
        for (Internship internship : internshipMgr.getAllInternships()) {
            if (internship.getRepresentativeId().equalsIgnoreCase(repId)) {
                // Add all applications for that internship
                List<Application> apps = appRepo.getApplicationsByInternship(internship.getInternshipId());
                result.addAll(apps);
            }
        }

        return result;
    }

    public List<Application> getApplicationsForInternship(String internshipId) {
        return appRepo.getApplicationsByInternship(internshipId);
    }


    public void updateApplicationStatus(String appId, ApplicationStatus newStatus) {
        appRepo.updateApplicationStatus(appId, newStatus);
    }


    //  GET STUDENT APPLICATIONS
    public List<Application> getMyApplications(String studentId) {
        return appRepo.getApplicationsByStudent(studentId);
    }

    public void displayApplicationsForStudent(Student student) {
        List<Application> apps = getMyApplications(student.getUserId());

        if (apps.isEmpty()) {
            System.out.println("You have no applications yet.");
            return;
        }

        System.out.println("\n--- YOUR APPLICATIONS ---");
        for (Application a : apps) {
            Internship i = internshipMgr.findInternshipById(a.getInternshipId());
            String title = (i != null) ? i.getTitle() : a.getInternshipId();
            System.out.printf("[%s] %s | Status: %s%n", a.getApplicationId(), title, a.getStatus());
        }
    }

    public boolean hasSuccessfulOffer(Student student) {
        return getMyApplications(student.getUserId()).stream()
                .anyMatch(a -> a.getStatus() == ApplicationStatus.SUCCESSFUL);
    }



    // Returns null on success, error message string on failure
    public String acceptOffer(Student student, String appId) {
        Application selected = appRepo.getAllApplications().stream()
                .filter(a -> a.getApplicationId().equalsIgnoreCase(appId)
                        && a.getStudentId().equalsIgnoreCase(student.getUserId()))
                .findFirst().orElse(null);

        if (selected == null) {
            String msg = "Application not found.";
            System.out.println(msg);
            return msg;
        }
        if (selected.getStatus() != ApplicationStatus.SUCCESSFUL) {
            String msg = "You can only accept a successful offer.";
            System.out.println(msg);
            return msg;
        }

        // Check if internship still has available slots BEFORE accepting
        Internship acceptedInternship = internshipMgr.findInternshipById(selected.getInternshipId());
        if (acceptedInternship == null) {
            String msg = "Internship not found for this application.";
            System.out.println(msg);
            return msg;
        }

        // Check if internship is already filled or has no slots
        if (acceptedInternship.getStatus() == InternshipStatus.FILLED || !acceptedInternship.hasAvailableSlots()) {
            String msg = "This internship is full. All slots have been filled.";
            System.out.println(msg);
            return msg;
        }

        // Accept the selected one
        selected.setStatus(ApplicationStatus.ACCEPTED);

        // Decrement slot count
        acceptedInternship.decrementSlot();
        // Set status to FILLED when all slots are taken
        if (acceptedInternship.getSlotsLeft() == 0) {
            acceptedInternship.setStatus(InternshipStatus.FILLED);
        }
        internshipMgr.saveAllInternships();

        // Withdraw all other active applications
        for (Application a : appRepo.getAllApplications()) {
            if (a.getStudentId().equalsIgnoreCase(student.getUserId())
                    && !a.getApplicationId().equalsIgnoreCase(appId)
                    && (a.getStatus() == ApplicationStatus.PENDING
                    || a.getStatus() == ApplicationStatus.SUCCESSFUL)) {
                a.setStatus(ApplicationStatus.WITHDRAWN);
            }
        }

        appRepo.saveApplications();
        System.out.println("✅ You have accepted the offer for " + selected.getInternshipId() + ".");
        return null; // Success
    }

    public void displayWithdrawableApplications(Student student) {
        List<Application> apps = getMyApplications(student.getUserId()).stream()
                .filter(a -> a.getStatus() == ApplicationStatus.PENDING)
                .toList();

        if (apps.isEmpty()) {
            System.out.println("You have no pending applications to withdraw.");
            return;
        }

        System.out.println("\n--- WITHDRAWABLE APPLICATIONS ---");
        for (Application a : apps) {
            Internship i = internshipMgr.findInternshipById(a.getInternshipId());
            String title = (i != null) ? i.getTitle() : a.getInternshipId();
            System.out.printf("[%s] %s | Status: %s%n", a.getApplicationId(), title, a.getStatus());
        }
    }


    public void withdrawApplication(Student student, String appId) {
        Application app = appRepo.getAllApplications().stream()
                .filter(a -> a.getApplicationId().equalsIgnoreCase(appId)
                        && a.getStudentId().equalsIgnoreCase(student.getUserId()))
                .findFirst().orElse(null);

        if (app == null) {
            System.out.println("Application not found.");
            return;
        }

        Internship internship = internshipMgr.findInternshipById(app.getInternshipId());
        if (internship == null) {
            System.out.println("Internship not found for this application.");
            return;
        }

        //  Rule: Students can only withdraw from approved internships
        if (internship.getStatus() != InternshipStatus.APPROVED && internship.getStatus() != InternshipStatus.FILLED) {
            System.out.println("You can only withdraw applications for approved internships.");
            return;
        }

        //  Rule: Can withdraw pending applications (before placement confirmation) or accepted applications (after placement confirmation)
        if (app.getStatus() != ApplicationStatus.PENDING && app.getStatus() != ApplicationStatus.ACCEPTED) {
            if (app.getStatus() == ApplicationStatus.WITHDRAWN || app.getStatus() == ApplicationStatus.WITHDRAWAL_PENDING) {
                System.out.println("This application has already been withdrawn or is pending withdrawal approval.");
            } else {
                System.out.println("You can only withdraw pending applications (before confirmation) or accepted applications (after confirmation).");
            }
            return;
        }

        //  Mark as withdrawal requested
        app.setStatus(ApplicationStatus.WITHDRAWAL_PENDING);
        appRepo.saveApplications();
        System.out.println("✅ Withdrawal request submitted. Awaiting staff approval.");
    }

    // --- WITHDRAWAL APPROVAL LOGIC ---
    public List<Application> getPendingWithdrawals() {
        return getAllApplications().stream()
                .filter(a -> a.getStatus() == ApplicationStatus.WITHDRAWAL_PENDING)
                .toList();
    }

    public void approveWithdrawal(Application application) {
        application.setStatus(ApplicationStatus.WITHDRAWN);
        saveApplications(); // ✅ persistence
    }

    public void rejectWithdrawal(Application application) {
        application.setStatus(ApplicationStatus.PENDING);
        saveApplications(); // ✅ persistence
    }


    // --- HELPER: Generate readable ID ---
    private String generateAppId() {
        int max = 0;
        for (Application a : appRepo.getAllApplications()) {
            try {
                if (a.getApplicationId().startsWith("APP")) {
                    int num = Integer.parseInt(a.getApplicationId().substring(3));
                    if (num > max) max = num;
                }
            } catch (Exception ignored) {}
        }
        return String.format("APP%03d", max + 1);
    }

    public List<Application> getAllApplications() {
        return appRepo.getAllApplications();
    }

    public void saveApplications() {
        appRepo.saveApplications();
    }


}
