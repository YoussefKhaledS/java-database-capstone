package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {
    PatientRepository patientRepository;
    AppointmentRepository appointmentRepository;
    TokenService tokenService;

    @Transactional
    public int createPatient(Patient patient){
        try{
            Patient isExisting = patientRepository.findByEmail(patient.getEmail());
            if(isExisting != null){
                return 0 ;
            }
            patientRepository.save(patient);
            return 1 ;
        }catch (Exception e){
            return 0 ;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        try {
            // 1. Decode token to get email
            String emailFromToken = tokenService.extractIdentifier(token);

            // 2. Fetch patient by email
            Patient patient = patientRepository.findByEmail(emailFromToken);
            if (patient == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token"));
            }

            // 3. Validate that token patient matches requested ID
            if (!patient.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Unauthorized access"));
            }

            // 4. Fetch appointments for the patient
            List<Appointment> appointments = appointmentRepository.findByPatientId(id);

            // 5. Map to DTOs
            List<AppointmentDTO> dtoList = appointments.stream()
                    .map(AppointmentDTO::new) // Assuming you have a constructor in AppointmentDTO that accepts Appointment
                    .collect(Collectors.toList());

            // 6. Return response
            return ResponseEntity.ok(Map.of("appointments", dtoList));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve appointments"));
        }
    }


    @Transactional
    public ResponseEntity<Map<String , Object>> filterByCondition(String condition, Long id){
        try{
            LocalDateTime now = LocalDateTime.now();
            List<Appointment> appointments = appointmentRepository.findByPatientId(id) ;

            List<Appointment> filteredAppointments  = appointments.stream().filter(a -> {
                if(a.getStatus() == 1 && condition.equalsIgnoreCase("past")){
                    return true ;
                }else if (a.getStatus() == 0 && condition.equalsIgnoreCase("future")){
                    return true ;
                }
                return false ;
            }).collect(Collectors.toList()) ;
            List<AppointmentDTO> dtoList = filteredAppointments.stream()
                    .map(AppointmentDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(Map.of("appointments", dtoList));
        }catch (Exception e ){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to filter appointments by condition"));
        }
    }

    @Transactional
    public ResponseEntity<Map<String , Object>> filterByDoctor(String doctorName,Long id){
        try{
            List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientId(doctorName , id ) ;
            List<AppointmentDTO> dtoList = appointments.stream()
                    .map(AppointmentDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(Map.of("appointments", dtoList));
        }catch (Exception e ){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to filter appointments by doctor"));
        }
    }

    @Transactional
    public ResponseEntity<Map<String , Object>> filterByDoctorAndCondition(String condition, String doctorName, Long id){
        try{
            List<Appointment> appointments = appointmentRepository.filterByDoctorNameAndPatientIdAndStatus(doctorName, id , condition.equalsIgnoreCase("past") ? 1 : 0) ;
            List<AppointmentDTO> dtoList = appointments.stream()
                    .map(AppointmentDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(Map.of("appointments", dtoList));
        }catch (Exception e ){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to filter appointments by doctor and condition"));
        }
    }

    @Transactional
    public ResponseEntity<Map<String , Object>> getPatientDetails(String token){
        try{
            String emailFromToken = tokenService.extractIdentifier(token);
            Patient patient = patientRepository.findByEmail(emailFromToken);
            if(patient == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token"));
            }
            return ResponseEntity.ok(Map.of("patient", patient));
        }catch (Exception e ){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve patient details"));
        }
    }

}
