# Internship Placement Management System - Demo Script
## Split for 5 People (30-second intro + immediate demo)

---

## **PERSON 1: Introduction & Authentication (3-4 minutes)**

### Introduction (30 seconds)
"Good [morning/afternoon]. We present the Internship Placement Management System—a Java application using BCE architecture with Swing GUI and CSV persistence. It manages internships for Students, Company Representatives, and Career Center Staff. Let's begin."

### Demo Flow:

**1. Valid User Login (Test Case 1)**
- Launch: `java -cp out main.InternshipPlacementSystem --gui`
- Login as Student: `U2345123F` / `password`
- Show: Dashboard loads successfully
- Logout

**2. Invalid ID (Test Case 2)**
- Try login: `U9999999X` / `password`
- Show: "No such user ID or email was found"
- Try login: `invalid@email.com` / `password`
- Show: Same error message

**3. Incorrect Password (Test Case 3)**
- Login: `U2345123F` / `wrongpass`
- Show: "Incorrect password. Please try again."

**4. Password Change (Test Case 4)**
- Login: `U2345123F` / `password`
- Change password: `password` → `newpass123`
- Logout
- Login: `U2345123F` / `newpass123`
- Show: Login successful with new password
- Change back: `newpass123` → `password`

**5. Company Rep Registration & Approval (Test Case 5)**
- Register new Company Rep:
  - Name: "John Smith"
  - Email: "john@techcorp.com"
  - Company: "TechCorp"
  - Department: "Engineering"
  - Position: "HR Manager"
- Show: Status is PENDING
- Try login: "john@techcorp.com" / "password"
- Show: "Account is not approved yet"
- Switch to Staff account (show approval later)

---

## **PERSON 2: Student Features - Viewing & Applications (4-5 minutes)**

### Demo Flow:

**6. Internship Visibility Based on Profile (Test Case 6)**
- Login as Student: `U2345123F` / `password` (Year 3, EEE major)
- View Available Internships
- Show: Only sees internships matching:
  - EEE major (or compatible)
  - Any level (Year 3+ can see all)
  - Visibility ON
  - Status APPROVED
- Login as Year 1 Student: `U1111111A` / `password` (Year 1, CSC)
- Show: Only sees BASIC level internships matching CSC major

**7. Application Eligibility (Test Case 7)**
- As Year 1 Student, try to apply for INTERMEDIATE internship
- Show: "You are not eligible to apply for this internship level"
- Apply for BASIC internship matching major
- Show: Application successful
- Try to apply for same internship again
- Show: "You already applied for this internship"
- Try to apply for 4th internship
- Show: "You cannot apply for more than 3 internships at once"

**8. View Applications After Visibility Toggle (Test Case 8)**
- As Year 3 Student, apply for an internship
- View My Applications
- Show: Application visible with status PENDING
- (Company Rep toggles visibility OFF - shown later)
- Refresh applications view
- Show: Application still visible even though internship visibility is OFF

**10. Single Placement Acceptance (Test Case 10)**
- As Student with 3 applications, get one approved by Company Rep
- View applications, see one is SUCCESSFUL
- Accept the successful offer
- Show: Confirmation dialog "Accepting will withdraw all other applications"
- Confirm acceptance
- Show: 
  - Selected application → ACCEPTED
  - Other applications → WITHDRAWN
  - Slot count decremented
- Try to apply for new internship
- Show: "You have already accepted an internship offer"

**Withdrawal Before/After Confirmation (Test Case 23)**
- Request withdrawal for PENDING application
- Show: Status → WITHDRAWAL_PENDING
- (Staff approval shown later)
- After accepting offer, request withdrawal for ACCEPTED application
- Show: Can request withdrawal even after acceptance

---

## **PERSON 3: Company Rep Features - Creation & Management (4-5 minutes)**

### Demo Flow:

**13. Internship Creation (Test Case 13)**
- Login as approved Company Rep: `john@techcorp.com` / `password`
- Create New Internship:
  - Title: "Software Engineering Intern"
  - Description: "Full-stack development"
  - Level: INTERMEDIATE
  - Major: "CSC"
  - Opening Date: "2024-01-01"
  - Closing Date: "2024-12-31"
  - Slots: 5
- Show: Created with PENDING status
- Try to create 6th internship
- Show: "You can only post up to 5 internships"

**14. Approval Status Visibility (Test Case 14)**
- View My Internships
- Show: Status is PENDING
- (Staff approves - shown later)
- Refresh
- Show: Status changed to APPROVED

**15. Access Own Internships (Test Case 15)**
- Toggle visibility OFF for own internship
- View My Internships
- Show: Still can see full details of own internship
- (Students can't see it - shown in Person 2)

**16. Edit Restriction (Test Case 16)**
- Try to edit PENDING internship
- Show: Edit allowed
- (After approval) Try to edit APPROVED internship
- Show: "Cannot edit. Internship has already been APPROVED"

**18. Application Management (Test Case 18)**
- View Applications for internships
- Show: List of student applications
- Approve an application
- Show: Application status → SUCCESSFUL
- Reject another application
- Show: Application status → UNSUCCESSFUL
- Refresh internship table
- Show: Slot count still same (slots only decrement on student acceptance)

**19. Placement Confirmation Status (Test Case 19)**
- After student accepts offer, refresh internship view
- Show: Slot count decremented (e.g., 5 → 4)
- When all slots filled, show: Status → FILLED

**20. Delete Internship (Test Case 20)**
- Delete a PENDING internship with no applications
- Show: Deletion successful, removed from list
- Try to delete internship with applications
- Show: Warning message but deletion allowed after confirmation
- Delete APPROVED internship
- Show: Can delete at any status

**22. Toggle Visibility (Test Case 22)**
- Toggle visibility ON for approved internship
- (Switch to Student view) Show: Internship now visible
- Toggle visibility OFF
- (Switch to Student view) Show: Internship no longer visible

---

## **PERSON 4: Staff Features - Approvals & Reports (4-5 minutes)**

### Demo Flow:

**5. Approve Company Rep (Test Case 5 - continuation)**
- Login as Staff: `STAFF001` / `password`
- View Pending Company Reps
- Show: "john@techcorp.com" in pending list
- Approve Company Rep
- Show: Status → APPROVED
- (Switch to Company Rep) Show: Can now login successfully

**21. Approve/Reject Internships (Test Case 21)**
- View All Internships
- Filter: Status = PENDING
- Show: Pending internships from Company Reps
- Approve an internship
- Show: Status → APPROVED
- Reject another internship
- Show: Status → REJECTED
- (Switch to Student) Show: Approved internship now visible

**23. Approve/Reject Withdrawals (Test Case 23)**
- View Pending Withdrawals
- Show: Withdrawal requests from students
- Approve withdrawal (before placement)
- Show: Application status → WITHDRAWN
- Approve withdrawal (after placement)
- Show: Application status → WITHDRAWN, slot freed

**24. Generate Reports (Test Case 24)**
- Generate Report
- Filter by:
  - Status: APPROVED
  - Major: "CSC"
  - Level: INTERMEDIATE
- Show: Filtered report with matching internships
- Clear filters, show: All internships
- Export/save report (if implemented)

**View All Users**
- View Students, Company Reps, Staff
- Show: Complete user lists with details

---

## **PERSON 5: Advanced Scenarios & Edge Cases (3-4 minutes)**

### Demo Flow:

**Date Restrictions**
- As Student, try to apply for internship before opening date
- Show: "The application period has not opened yet"
- Try to apply after closing date
- Show: "The application period has closed"

**Slot Management**
- Create internship with 2 slots
- Get 2 students to accept offers
- Show: Status → FILLED
- Try to have 3rd student accept
- Show: "This internship is full. All slots have been filled"

**Re-application After Withdrawal**
- Student withdraws from internship (approved by staff)
- Show: Application status → WITHDRAWN
- Student applies for same internship again
- Show: Application successful (can re-apply after withdrawal)

**Filter Persistence**
- Set filters (Status: APPROVED, Major: "EEE")
- Switch to different menu/page
- Return to internships view
- Show: Filters still applied

**Major Matching Flexibility**
- Student: Year 3, Major "EEE"
- Apply for internship with major "Electrical & Electronic Engineering"
- Show: Application successful (normalized matching works)

**Error Messages**
- Try invalid operations:
  - Edit approved internship (Company Rep)
  - Accept non-successful offer (Student)
  - Approve already-approved application (Company Rep)
- Show: Clear, specific error messages for each

**CLI Mode (Quick)**
- Run: `java -cp out main.InternshipPlacementSystem --cli`
- Show: Command-line interface works identically
- Quick login and view internships
- Show: Same functionality, different interface

**Data Persistence**
- Create new internship, approve it
- Close application
- Restart application
- Show: Data persisted, internship still exists

---

## **CLOSING (30 seconds - Person 1)**

"This demonstrates the Internship Placement Management System's core features: role-based access, internship management, application workflows, and comprehensive reporting. The system uses BCE architecture, follows SOLID principles, and provides both GUI and CLI interfaces. Thank you."

---

## **Quick Reference - Test Cases by Person:**

**Person 1:** 1, 2, 3, 4, 5 (partial)
**Person 2:** 6, 7, 8, 10, 23 (partial)
**Person 3:** 13, 14, 15, 16, 18, 19, 20, 22
**Person 4:** 5 (complete), 21, 23 (complete), 24
**Person 5:** Edge cases, date restrictions, slot management, re-application, filters, CLI mode

---

## **Tips for Smooth Demo:**

1. **Prepare test data** before demo (students, internships, applications)
2. **Use consistent test accounts** across all demos
3. **Practice transitions** between user roles
4. **Have backup scenarios** if something doesn't work
5. **Keep it concise** - focus on showing functionality, not explaining code
6. **Coordinate handoffs** - "Now I'll pass to [Name] to show Company Rep features"

