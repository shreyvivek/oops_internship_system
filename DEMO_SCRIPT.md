# Internship Placement Management System - 15-Minute Demo Script

## **PERSON 1: Introduction & Authentication (3 minutes)**

### Introduction (30 seconds)
"Good [morning/afternoon]. We present the Internship Placement Management System—a Java application using BCE architecture with Swing GUI and CSV persistence. It manages internships for Students, Company Representatives, and Career Center Staff. Let's begin."

### Demo Flow:

**• Valid Login** → Login as Student `U2345123F` / `password` → Dashboard loads

**• Invalid ID** → Try `U9999999X` → "No such user ID or email was found"

**• Wrong Password** → Try correct ID with wrong password → "Incorrect password. Please try again."

**• Password Change** → Change password → Logout → Login with new password → Works

**• Company Rep Registration** → Register new rep → Show PENDING status → Try login → "Account not approved yet" → Switch to Staff

---

## **PERSON 2: Student Features (3 minutes)**

**• View Filtered Internships** → Year 3 EEE student sees only EEE-compatible, any level → Year 1 CSC sees only BASIC level, CSC major

**• Apply for Internship** → Apply → Shows in "My Applications" as PENDING → Internship disappears from available list

**• Application Limits** → Try 4th application → "Cannot apply for more than 3 at once"

**• View Applications After Visibility Toggle** → Company rep toggles visibility OFF → Student still sees application in "My Applications"

**• Accept Offer** → Company rep approves → Student sees SUCCESSFUL → Accept → Other applications auto-withdrawn → Slot decremented

**• Withdrawal** → Request withdrawal for PENDING application → Status → WITHDRAWAL_PENDING → Internship becomes visible again after staff approval

---

## **PERSON 3: Company Rep Features (3 minutes)**

**• Create Internship** → Create with all details → Status PENDING → Try 6th → "Limit of 5 internships"

**• View Status** → Show PENDING → Staff approves → Refresh → Status APPROVED

**• Edit Restriction** → Edit PENDING → Works → Try edit APPROVED → "Cannot edit after approval"

**• View Applications** → See student applications → Approve one → Status → SUCCESSFUL → Reject another → Status → UNSUCCESSFUL

**• Delete Internship** → Delete PENDING with no apps → Works → Delete with applications → Warning shown but allowed

**• Toggle Visibility** → Toggle ON → Student sees it → Toggle OFF → Student can't see it (but can see their application)

---

## **PERSON 4: Staff Features (3 minutes)**

**• Approve Company Rep** → View pending reps → Approve → Rep can now login

**• Approve/Reject Internships** → View pending internships → Approve → Status → APPROVED → Reject another → Status → REJECTED

**• Approve Withdrawals** → View withdrawal requests → Approve → Application → WITHDRAWN → If was ACCEPTED, slot freed

**• Generate Reports** → Filter by Status: APPROVED, Major: CSC, Level: INTERMEDIATE → Show filtered results

**• View All Users** → Show students, reps, staff lists

---

## **PERSON 5: Edge Cases & Advanced (3 minutes)**

**• Slot Management** → Create internship with 2 slots → 2 students accept → Status → FILLED → 3rd student tries → "Internship is full"

**• Re-application After Withdrawal** → Student withdraws → Staff approves → Student applies for same internship again → Works

**• Date Restrictions** → Try apply before opening date → "Application period not opened" → Try after closing → "Application period closed"

**• Major Matching** → EEE student applies for "Electrical & Electronic Engineering" → Works (normalized matching)

**• Filter Persistence** → Set filters → Switch pages → Return → Filters still applied

**• CLI Mode** → Quick demo of CLI interface → Same functionality

**• Data Persistence** → Create internship → Close app → Restart → Data persists

---

## **CLOSING (30 seconds - Person 1)**

"This demonstrates the system's core features: role-based access, internship management, application workflows, and reporting. The system uses BCE architecture, follows SOLID principles, and provides both GUI and CLI interfaces. Thank you."

---

## **Quick Handoff Phrases:**

- "Now [Name] will demonstrate student features..."
- "Let me pass to [Name] to show company rep capabilities..."
- "[Name] will now show staff approval workflows..."
- "Finally, [Name] will cover edge cases and advanced scenarios..."
