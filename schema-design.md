# Schema Design

## MySQL Database Design

Below is the relational database design for the clinic management system.  
The schema focuses on structured, validated, and interconnected data.

---

### Table: patients
- id: INT, PRIMARY KEY, AUTO_INCREMENT  
- full_name: VARCHAR(100), NOT NULL  
- email: VARCHAR(120), UNIQUE, NOT NULL
- password_hash: VARCHAR(255), NOT NULL
- phone: VARCHAR(20), NOT NULL  
- date_of_birth: DATE  
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP  

**Notes:**  
- Emails must be unique.  
- Patients should not be fully deleted; instead consider soft delete if needed to preserve history.

---

### Table: doctors
- id: INT, PRIMARY KEY, AUTO_INCREMENT  
- full_name: VARCHAR(100), NOT NULL
- password_hash: VARCHAR(255), NOT NULL
- specialization: VARCHAR(100), NOT NULL  
- email: VARCHAR(120), UNIQUE, NOT NULL  
- phone: VARCHAR(20)  
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP  

**Notes:**  
- Doctors must have a specialization.  
- Phone validation handled at application layer.

---

### Table: appointments
- id: INT, PRIMARY KEY, AUTO_INCREMENT  
- doctor_id: INT, FOREIGN KEY → doctors(id), NOT NULL  
- patient_id: INT, FOREIGN KEY → patients(id), NOT NULL  
- appointment_time: DATETIME NOT NULL  
- status: INT NOT NULL  
  - 0 = Scheduled  
  - 1 = Completed  
  - 2 = Cancelled  
- notes: TEXT NULL  

**Notes:**  
- If a patient is deleted, DO NOT delete appointments → use ON DELETE RESTRICT.  
- Prevent overlapping appointments in application logic.

---

### Table: admin
- id: INT, PRIMARY KEY, AUTO_INCREMENT  
- username: VARCHAR(50), UNIQUE, NOT NULL  
- password_hash: VARCHAR(255), NOT NULL  
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP  

**Notes:**  
- Passwords stored as hashes only.  
- Admin table should be small and tightly controlled.

---

### Table: doctor_availability
- id: INT, PRIMARY KEY, AUTO_INCREMENT  
- doctor_id: INT, FOREIGN KEY → doctors(id), NOT NULL  
- available_from: DATETIME, NOT NULL  
- available_to: DATETIME, NOT NULL  

**Notes:**  
- Helps avoid overlapping manual schedules.  
- Patients can only book within availability ranges.

---

## MongoDB Collection Design

MongoDB is used for flexible, evolving, or unstructured data such as notes, logs, or prescriptions.

### Collection: prescriptions

Example document:

```json
{
  "_id": "ObjectId('64abc123456')",
  "appointmentId": 51,
  "patientId": 12,
  "doctorId": 4,
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "instructions": "Take one tablet every 6 hours"
    },
    {
      "name": "Vitamin D",
      "dosage": "1000 IU",
      "instructions": "Once daily after lunch"
    }
  ],
  "doctorNotes": "Patient should rest for 3 days and avoid heavy lifting.",
  "refillAllowed": true,
  "tags": ["fever", "general"],
  "pharmacy": {
    "name": "Good Health Pharmacy",
    "location": "Downtown Clinic Branch"
  },
  "createdAt": "2025-02-18T10:30:00Z"
}
