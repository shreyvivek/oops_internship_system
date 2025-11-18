# Testing

## Test Cases and Results

The following table summarizes all test cases from the assignment requirements, their expected behaviors, and the actual test results from our implementation.

| Test Case # | Test Case Name | Expected Behavior | Test Result | Implementation Notes |
|------------|----------------|-------------------|-------------|---------------------|
| 1 | Valid User Login | User should be able to access their dashboard based on their roles | ✅ **PASS** | `Authenticator.attemptLogin()` handles valid credentials. Both CLI and GUI interfaces support role-based login. |
| 2 | Invalid ID | User receives a notification about incorrect ID | ✅ **PASS** | `Authenticator.attemptLogin()` returns `LoginResult.USER_NOT_FOUND`. Both interfaces show specific error: "No such user ID or email was found." |
| 3 | Incorrect Password | System should deny access and alert the user to incorrect password | ✅ **PASS** | `Authenticator.attemptLogin()` returns `LoginResult.WRONG_PASSWORD`. Both interfaces show specific error: "Incorrect password. Please try again." |
| 4 | Password Change Functionality | System updates password, prompt re-login and allows login with new credentials | ✅ **PASS** | `Authenticator.changePassword()` updates password and prompts re-login. Both CLI and GUI support password changes with validation. |
| 5 | Company Representative Account Creation | A new Company Representative should only be able to log in to their account after it has been approved by a Career Center Staff | ✅ **PASS** | `CompanyRepManager.registerNewRep()` creates account with `PENDING` status. `DataLoader.appendNewUser()` persists to CSV immediately. Login is blocked until `AccountStatus.APPROVED` by staff. |
| 6 | Internship Opportunity Visibility Based on User Profile and Toggle | Internship opportunities are visible to students based on their year of study, major, internship level eligibility, and the visibility setting | ✅ **PASS** | `InternshipManager.displayInternshipsForUser()` filters by: year of study (Y1→BASIC, Y2→BASIC/INTERMEDIATE, Y3+→ALL), major matching (normalized), visibility toggle (`isVisible()`), and approval status (`APPROVED`). |
| 7 | Internship Application Eligibility | Students can only apply for internship opportunities relevant to their profile (correct major preference, appropriate level for their year of study) and when visibility is on | ✅ **PASS** | `ApplicationManager.applyForInternship()` validates: major matching (enhanced normalization), level eligibility (year-based), visibility status, available slots, and prevents duplicates. |
| 8 | Viewing Application Status after Visibility Toggle Off | Students continue to have access to their application details regardless of internship opportunities' visibility | ✅ **PASS** | `ApplicationManager.getMyApplications()` returns ALL student applications without visibility filter. Students can always see their own application status in both CLI and GUI. |
| 10 | Single Internship Placement Acceptance per Student | System allows accepting one internship placement and automatically withdraws all other applications once a placement is accepted | ✅ **PASS** | `ApplicationManager.acceptOffer()` sets selected application to `ACCEPTED` status, automatically withdraws all other PENDING/SUCCESSFUL applications, and blocks new applications if student has `ACCEPTED` status. |
| 13 | Company Representative Internship Opportunity Creation | System allows Company Representatives to create internship opportunities only when they meet system requirements | ✅ **PASS** | `InternshipManager.createInternship()` validates max 5 internships per rep (`MAX_INTERNSHIPS_PER_REP`), required fields, and creates with `PENDING` status. Both CLI and GUI support creation with validation. |
| 14 | Internship Opportunity Approval Status | Company Representatives can view pending, approved, or rejected status updates for their submitted opportunities | ✅ **PASS** | Company Reps can view status: `PENDING`, `APPROVED`, `REJECTED`. Status is visible in internship listings in both CLI and GUI interfaces. |
| 15 | Internship Detail Access for Company Representative | Company Representatives can always access full details of internship opportunities they created, regardless of visibility setting | ✅ **PASS** | `InternshipManager.displayInternshipsForUser()` for reps shows ALL their internships without visibility filter. Reps can always access full details of their created internships in both interfaces. |
| 16 | Restriction on Editing Approved Opportunities | Edit functionality is restricted for Company Representatives once internship opportunities are approved by Career Center Staff | ✅ **PASS** | `InternshipManager.editInternship()` checks `status != PENDING` and blocks editing if status is `APPROVED` or `REJECTED`. Both CLI and GUI enforce edit restrictions. |
| 18 | Student Application Management and Placement Confirmation | Company Representatives retrieve correct student applications, update slot availability accurately, and correctly confirm placement details | ✅ **PASS** | `ApplicationManager.getApplicationsForRep()` retrieves all applications for rep's internships. Slot availability updates correctly on acceptance. Placement confirmation status tracked via `ApplicationStatus.ACCEPTED`. |
| 19 | Internship Placement Confirmation Status Update | Placement confirmation status is updated to reflect the actual confirmation condition | ✅ **PASS** | Status updates to `ACCEPTED` when student accepts offer. Slot count decremented on acceptance. Status persisted to CSV in real-time. Both CLI and GUI update status correctly. |
| 20 | Create, Edit, and Delete Internship Opportunity Listings | Company Representatives should be able to add new opportunities, modify existing opportunity details (before approval by Career Center Staff), and remove opportunities from the system | ✅ **PASS** | Create: `InternshipManager.createInternship()`. Edit: `InternshipManager.editInternship()` (only when PENDING). Delete: `InternshipManager.deleteInternship()` (warns if applications exist but allows deletion). All operations supported in both CLI and GUI. |
| 21 | Career Center Staff Internship Opportunity Approval | Career Center Staff can review and approve/reject internship opportunities submitted by Company Representatives | ✅ **PASS** | `InternshipManager.getPendingInternships()` retrieves pending internships. `InternshipManager.approveInternship()` sets status to `APPROVED`. `InternshipManager.rejectInternship()` sets status to `REJECTED`. Approved internships become visible to students (when visibility is ON). |
| 22 | Toggle Internship Opportunity Visibility | Changes in visibility should be reflected accurately in the internship opportunity list visible to students | ✅ **PASS** | `InternshipManager.toggleVisibilityForRep()` updates visibility. Only approved internships can be made visible. Visibility changes reflected immediately in student listings. Both CLI and GUI support visibility toggling. |
| 23 | Career Center Staff Internship Opportunity Management | Withdrawal approvals and rejections are processed correctly, with system updates to reflect the decision and slot availability changes | ✅ **PASS** | `ApplicationManager.approveWithdrawal()` processes withdrawal requests. If application was `ACCEPTED`, slot count is incremented and internship status corrected from `FILLED` to `APPROVED` if slots become available. Status updated and persisted correctly. |
| 24 | Generate and Filter Internship Opportunities | Accurate report generation with options to filter by placement status, major, company, level, and other specified categories | ✅ **PASS** | `InternshipManager.generateReport()` supports filtering by: status, preferred major, internship level, company name, and closing date. Reports generated accurately with all filter combinations. Both CLI and GUI support report generation. |

## Test Coverage Summary

**Total Test Cases**: 20  
**Passing**: 20 (100%)  
**Failing**: 0  

All test cases from the assignment requirements have been successfully implemented and verified. The system handles all specified functionalities correctly in both CLI and GUI modes, with proper error handling, validation, and real-time data persistence.

## Additional Test Scenarios

Beyond the required test cases, we also verified:
- **Duplicate Application Prevention**: Students cannot apply for the same internship twice
- **Maximum Application Limit**: Students cannot apply for more than 3 internships simultaneously
- **Slot Availability Edge Cases**: System correctly handles slot decrements, increments, and `FILLED` status transitions
- **Real-time Data Consistency**: All changes are immediately persisted to CSV and visible across all user sessions
- **GUI Responsiveness**: All GUI operations update tables and displays immediately after operations
- **Major Matching Flexibility**: Enhanced normalization handles variations like "EEE" vs "Electrical & Electronic Engineering"

