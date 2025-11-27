package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    AppointmentRepository appointmentRepository;
    TokenService tokenService;
    PatientRepository patientRepository;
    DoctorRepository doctorRepository;


// 1. **Add @Service Annotation**:
//    - To indicate that this class is a service layer class for handling business logic.
//    - The `@Service` annotation should be added before the class declaration to mark it as a Spring service component.
//    - Instruction: Add `@Service` above the class definition.

// 2. **Constructor Injection for Dependencies**:
//    - The `AppointmentService` class requires several dependencies like `AppointmentRepository`, `Service`, `TokenService`, `PatientRepository`, and `DoctorRepository`.
//    - These dependencies should be injected through the constructor.
//    - Instruction: Ensure constructor injection is used for proper dependency management in Spring.

// 3. **Add @Transactional Annotation for Methods that Modify Database**:
//    - The methods that modify or update the database should be annotated with `@Transactional` to ensure atomicity and consistency of the operations.
//    - Instruction: Add the `@Transactional` annotation above methods that interact with the database, especially those modifying data.
    @Transactional
    public int bookAppointment(Appointment appointment) throws Exception{
        try {
            appointmentRepository.save(appointment);
        } catch (Exception e) {
            return 0;
        }
        return 1;
    }
    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) throws Exception {
        try{
            if(appointmentRepository.findById(appointment.getId()).isEmpty()){
                return ResponseEntity.ok(Map.of("result","Appointment not found"));
            }else {
                appointmentRepository.save(appointment);
                return ResponseEntity.ok(Map.of("result","1"));
            }
        }catch (Exception e){
            return ResponseEntity.ok(Map.of("result","Error updating appointment"));
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(Long id, String token) throws Exception {
        // 1️⃣ Validate the token first
        Map<String, String> validationResult = null ; // tokenService.validateToken(token, "patient"); // or "admin" depending on the role

        if (!validationResult.isEmpty()) {
            // Token is invalid
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("result", "Invalid or expired token"));
        }

        // 2️⃣ Proceed with appointment cancellation
        try {
            Appointment appointment = appointmentRepository.findById(id).orElse(null);
            if (appointment == null) {
                return ResponseEntity.ok(Map.of("result", "Appointment not found"));
            } else {
                appointmentRepository.delete(appointment);
                return ResponseEntity.ok(Map.of("result", "Deleted Successfully"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("result", "Error cancelling appointment"));
        }
    }

    @Transactional
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) throws Exception {
        Map<String, Object> response = new HashMap<>();

        // 1️⃣ Validate token (doctor role assumed)
        Map<String, String> validationResult = null ; //tokenService.validateToken(token, "doctor");
        if (!validationResult.isEmpty()) {
            response.put("error", "Invalid or expired token");
            return response;
        }

        // 2️⃣ Extract doctor ID from token or validation result
        Long doctorId = Long.parseLong(validationResult.get("userId")); // assuming token returns userId

        // 3️⃣ Determine start and end of the day
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Appointment> appointments;

        // 4️⃣ Fetch appointments for the doctor and date, filter by patient name if provided
        if (pname == null || pname.isBlank()) {
            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, startOfDay, endOfDay);
        } else {
            appointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                    doctorId, pname, startOfDay, endOfDay);
        }

        // 5️⃣ Return appointments in a map
        response.put("appointments", appointments);
        return response;
    }

    @Transactional
    public void changeStatus(Long appointmentId, int status) {
        appointmentRepository.updateStatus(status, appointmentId);
    }
// 4. **Book Appointment Method**:
//    - Responsible for saving the new appointment to the database.
//    - If the save operation fails, it returns `0`; otherwise, it returns `1`.
//    - Instruction: Ensure that the method handles any exceptions and returns an appropriate result code.

// 5. **Update Appointment Method**:
//    - This method is used to update an existing appointment based on its ID.
//    - It validates whether the patient ID matches, checks if the appointment is available for updating, and ensures that the doctor is available at the specified time.
//    - If the update is successful, it saves the appointment; otherwise, it returns an appropriate error message.
//    - Instruction: Ensure proper validation and error handling is included for appointment updates.

// 6. **Cancel Appointment Method**:
//    - This method cancels an appointment by deleting it from the database.
//    - It ensures the patient who owns the appointment is trying to cancel it and handles possible errors.
//    - Instruction: Make sure that the method checks for the patient ID match before deleting the appointment.

// 7. **Get Appointments Method**:
//    - This method retrieves a list of appointments for a specific doctor on a particular day, optionally filtered by the patient's name.
//    - It uses `@Transactional` to ensure that database operations are consistent and handled in a single transaction.
//    - Instruction: Ensure the correct use of transaction boundaries, especially when querying the database for appointments.

// 8. **Change Status Method**:
//    - This method updates the status of an appointment by changing its value in the database.
//    - It should be annotated with `@Transactional` to ensure the operation is executed in a single transaction.
//    - Instruction: Add `@Transactional` before this method to ensure atomicity when updating appointment status.


}
