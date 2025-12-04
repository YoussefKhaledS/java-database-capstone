package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Services {
    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        // Call your token service to validate the token
        Boolean validationResponse = tokenService.validateToken(token, user);

        if (validationResponse) {
            // Token is valid
            return ResponseEntity.ok(Map.of("message", "Token is valid"));
        } else {
            // Token is invalid or expired
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid Token"));
        }
    }

    @Transactional
    public ResponseEntity<Map<String , String>> validateAdmin(Admin admin){
        try{
            Admin foundAdmin = adminRepository.findByUsername(admin.getUsername());
            if(foundAdmin == null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Admin not found"));
            }
            if(foundAdmin.getPasswordHash().equals(admin.getPasswordHash())){
                String token = tokenService.generateToken(foundAdmin.getUsername());
                return ResponseEntity.ok(Map.of("token", token));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid password"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        Map<String, Object> result = new HashMap<>();


        if (name != null && !name.isEmpty() && specialty != null && !specialty.isEmpty() && time != null && !time.isEmpty()) {
            result = doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        } else if (name != null && !name.isEmpty() && specialty != null && !specialty.isEmpty()) {
            result = doctorService.filterDoctorByNameAndSpecility(name, specialty);
        } else if (name != null && !name.isEmpty() && time != null && !time.isEmpty()) {
            result = doctorService.filterDoctorByNameAndTime(name, time);
        } else if (specialty != null && !specialty.isEmpty() && time != null && !time.isEmpty()) {
            result = doctorService.filterDoctorByTimeAndSpecility(specialty, time);
        } else if (name != null && !name.isEmpty()) {
            result = doctorService.findDoctorByName(name);
        } else if (specialty != null && !specialty.isEmpty()) {
            result = doctorService.filterDoctorBySpecility(specialty);
        } else if (time != null && !time.isEmpty()) {
            result = doctorService.filterDoctorsByTime(time);
        } else {
            result = Map.of("Doctors" , doctorService.getDoctors()); // fallback to all doctors
        }

        return result;
    }

    public int validateAppointment(Appointment appointment){
        Optional<Doctor> doctor = doctorRepository.findById(appointment.getDoctor().getId()) ;
        if(!doctor.isPresent()){
            return -1; // doctor doesn't exist
        }
        List<String> availableSlots = doctorService.getDoctorAvailability(doctor.get().getId(), appointment.getAppointmentDate()) ;
        if(availableSlots.contains(appointment.getAppointmentTime().toString())){
            return 1; // valid appointment time
        } else {
            return 0; // invalid appointment time
        }
    }


    public Boolean validatePatient(String email , String phoneNumber){
        return patientRepository.findByEmailOrPhone(email, phoneNumber) != null;
    }

    public ResponseEntity<Map<String , String> > validatePatientLogin(Login login){
        Patient patient = patientRepository.findByEmail(login.getEmail());
        if(patient == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Patient not found"));
        }
        if(patient.getPassword().equals(login.getPassword())){
            String token = tokenService.generateToken(patient.getEmail());
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid password"));
        }
    }
    public ResponseEntity<Map<String , Object>> filterPatient(String condition, String doctorName, String token){
        String email = tokenService.extractIdentifier(token);
        if(email == null || email.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid token"));
        }
        Patient patient = patientRepository.findByEmail(email);
        if(patient == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Patient not found"));
        }

        if (condition != null && !condition.isEmpty() && doctorName != null && !doctorName.isEmpty()) {
            return patientService.filterByDoctorAndCondition(condition, doctorName, patient.getId());
        } else if (condition != null && !condition.isEmpty()) {
            return patientService.filterByCondition(condition, patient.getId());
        } else if (doctorName != null && !doctorName.isEmpty()) {
            return patientService.filterByDoctor(doctorName , patient.getId());
        } else {
            return patientService.getPatientAppointment(patient.getId(), token);
        }
    }



}
