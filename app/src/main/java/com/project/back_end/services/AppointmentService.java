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

    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
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
        // Token validation is already done in the controller
        try {
            Appointment appointment = appointmentRepository.findById(id).orElse(null);
            if (appointment == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("result", "Appointment not found"));
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

        // Token validation is already done in the controller
        try {
            // 1️⃣ Extract doctor email from token
            String doctorEmail = tokenService.extractIdentifier(token);
            if (doctorEmail == null || doctorEmail.isEmpty()) {
                response.put("error", "Invalid token");
                return response;
            }

            // 2️⃣ Find doctor by email to get doctor ID
            com.project.back_end.models.Doctor doctor = doctorRepository.findByEmail(doctorEmail);
            if (doctor == null) {
                response.put("error", "Doctor not found");
                return response;
            }
            Long doctorId = doctor.getId();

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
        } catch (Exception e) {
            response.put("error", "Failed to fetch appointments: " + e.getMessage());
            return response;
        }
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
