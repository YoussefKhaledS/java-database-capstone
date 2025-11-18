# User Stories

# Admin User Stories

## 1. Admin Login

**Title:**  
_As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely._

**Acceptance Criteria:**
1. Admin can access the login page.
2. System validates username and password.
3. Successful login redirects to the dashboard.
4. Invalid credentials show an appropriate error.

**Priority:** High  
**Story Points:** 3

**Notes:**
- Implement using Spring Security.
- Consider lockout policy for repeated failed logins.

---

## 2. Admin Logout

**Title:**  
_As an admin, I want to log out of the portal, so that I can protect system access after finishing my work._

**Acceptance Criteria:**
1. Logout button available on all admin pages.
2. Clicking logout terminates the session.
3. Admin is redirected to the login page.
4. Admin cannot access authenticated pages after logout.

**Priority:** High  
**Story Points:** 2

**Notes:**
- Use Spring Security logout handler.

---

## 3. Add Doctors

**Title:**  
_As an admin, I want to add new doctors to the portal, so that they can access their accounts and manage appointments._

**Acceptance Criteria:**
1. Admin can open the "Add Doctor" page.
2. Doctor information is validated before submission.
3. System stores the doctor record in the database.
4. Admin receives a confirmation message.

**Priority:** High  
**Story Points:** 5

**Notes:**
- Validate duplicate email addresses.
- Optional: Send credentials automatically.

---

## 4. Delete Doctor Profile

**Title:**  
_As an admin, I want to delete a doctor’s profile, so that inactive or incorrect accounts are removed from the system._

**Acceptance Criteria:**
1. Admin can view a list of doctors with a delete option.
2. System prompts for confirmation before deletion.
3. On confirmation, system removes the doctor record.
4. System handles related appointments according to business rules.

**Priority:** Medium  
**Story Points:** 4

**Notes:**
- Confirm cascading delete requirements.
- Add warning if doctor has active appointments.

---

## 5. Run Stored Procedure for Monthly Stats

**Title:**  
_As an admin, I want to run a stored procedure in MySQL CLI, so that I can retrieve the number of appointments per month and track usage statistics._

**Acceptance Criteria:**
1. Stored procedure exists in MySQL.
2. Admin can run it manually via CLI.
3. The output lists appointment counts grouped by month.
4. Results reflect the latest data.

**Priority:** Medium  
**Story Points:** 3

**Notes:**
- Procedure may later be automated for reporting.

# Patient User Stories

## 1. View Doctors Without Login

**Title:**  
_As a patient, I want to view a list of doctors without logging in, so that I can explore options before registering._

**Acceptance Criteria:**
1. Patients can access the public doctors list page.
2. Page displays doctor name, specialty, and basic information.
3. No login is required to access the list.

**Priority:** High  
**Story Points:** 3

**Notes:**
- Consider pagination if the list is long.
- Only non-sensitive doctor info should be shown.

---

## 2. Patient Signup

**Title:**  
_As a patient, I want to sign up using my email and password, so that I can book appointments._

**Acceptance Criteria:**
1. Patient can open the signup page.
2. System validates email, password, and required fields.
3. Account is created and stored in the database.
4. Patient receives a confirmation message upon successful signup.

**Priority:** High  
**Story Points:** 5

**Notes:**
- Validate duplicate emails.
- Password complexity validation recommended.

---

## 3. Patient Login

**Title:**  
_As a patient, I want to log into the portal, so that I can manage my bookings._

**Acceptance Criteria:**
1. Login page is accessible to all users.
2. Patient enters valid email and password.
3. Successful login redirects to the patient dashboard.
4. Invalid credentials show an error message.

**Priority:** High  
**Story Points:** 3

**Notes:**
- Implement using Spring Security.
- Consider “Remember Me” functionality.

---

## 4. Patient Logout

**Title:**  
_As a patient, I want to log out of the portal, so that I can secure my account._

**Acceptance Criteria:**
1. Logout button appears on all authenticated pages.
2. Clicking logout ends the session.
3. Patient is redirected to the login or home page.
4. Patient cannot access dashboard pages after logout.

**Priority:** Medium  
**Story Points:** 2

**Notes:**
- Use Spring Security logout handler.

---

## 5. Book Appointment

**Title:**  
_As a patient, I want to log in and book an hour-long appointment, so that I can consult with a doctor._

**Acceptance Criteria:**
1. Patient must be logged in to book.
2. Patient can choose a doctor and see available time slots.
3. Selected slot is reserved for one hour.
4. System saves the appointment and sends a confirmation.

**Priority:** High  
**Story Points:** 8

**Notes:**
- Prevent double-booking overlapping time slots.
- Consider time-zone or availability rules.

---

## 6. View Upcoming Appointments

**Title:**  
_As a patient, I want to view my upcoming appointments, so that I can prepare accordingly._

**Acceptance Criteria:**
1. Patient dashboard shows a list of upcoming appointments.
2. Appointments must be sorted by date/time.
3. Only future appointments are displayed.
4. System shows doctor name, date, time, and location/mode of visit.

**Priority:** Medium  
**Story Points:** 4

**Notes:**
- Consider adding reschedule or cancel options in future stories.

# Doctor User Stories

## 1. Doctor Login

**Title:**  
_As a doctor, I want to log into the portal, so that I can manage my appointments._

**Acceptance Criteria:**
1. Doctor can access the login page.
2. System validates email/username and password.
3. Successful login redirects to the doctor dashboard.
4. Invalid credentials display an error message.

**Priority:** High  
**Story Points:** 3

**Notes:**
- Authentication should use Spring Security.
- Consider enforcing strong password requirements.

---

## 2. Doctor Logout

**Title:**  
_As a doctor, I want to log out of the portal, so that I can protect my data._

**Acceptance Criteria:**
1. Logout option is available on all doctor pages.
2. Session is terminated upon logout.
3. Doctor is redirected to the login or home page.
4. Access to dashboard pages is blocked after logout.

**Priority:** Medium  
**Story Points:** 2

**Notes:**
- Use Spring Security logout handler.

---

## 3. View Appointment Calendar

**Title:**  
_As a doctor, I want to view my appointment calendar, so that I can stay organized._

**Acceptance Criteria:**
1. Doctor can view a calendar or list of upcoming appointments.
2. Appointments are displayed with date, time, and patient name.
3. Calendar is sorted by date and time.
4. Only appointments assigned to the logged-in doctor are shown.

**Priority:** High  
**Story Points:** 5

**Notes:**
- Consider calendar UI (monthly/weekly view).
- Add filters for appointment types if applicable.

---

## 4. Mark Unavailability

**Title:**  
_As a doctor, I want to mark my unavailability, so that patients can see only the available slots._

**Acceptance Criteria:**
1. Doctor can open the unavailability settings page.
2. Doctor can choose date(s) or time ranges to mark unavailable.
3. Patients cannot book appointments during unavailable times.
4. System updates availability in real time.

**Priority:** High  
**Story Points:** 8

**Notes:**
- Handle overlapping unavailability ranges.
- Sync with booking scheduler logic.

---

## 5. Update Profile Information

**Title:**  
_As a doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information._

**Acceptance Criteria:**
1. Doctor can access the profile settings page.
2. Doctor can update specialization, contact info, and bio.
3. System validates and saves changes.
4. Patients can see updated information on the doctor profile page.

**Priority:** Medium  
**Story Points:** 5

**Notes:**
- Validate email and phone format.
- Consider restricting edits to certain verified fields.

---

## 6. View Patient Details for Upcoming Appointments

**Title:**  
_As a doctor, I want to view patient details for upcoming appointments, so that I can be prepared._

**Acceptance Criteria:**
1. Doctor can click on an appointment to view details.
2. System displays patient name, contact info, and medical notes (if applicable).
3. Only information for appointments assigned to the doctor is accessible.
4. Sensitive data follows privacy/security rules.

**Priority:** High  
**Story Points:** 5

**Notes:**
- Ensure authorization checks for patient privacy.
- Optionally allow doctors to add notes after appointment.
