package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Services;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    private final Services services;

    // 1. Get Doctor Availability
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String token) {
        try {
            ResponseEntity<Map<String, String>> validation = services.validateToken(token, user);
            if (validation.getStatusCode() != HttpStatus.OK) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", validation.getBody().get("result")));
            }

            List<String> slots = doctorService.getDoctorAvailability(doctorId, date);
            return ResponseEntity.ok(Map.of("availability", slots));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch doctor availability"));
        }
    }

    // 2. Get List of Doctors
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDoctors() {
        try {
            List<Doctor> doctors = doctorService.getDoctors();
            return ResponseEntity.ok(Map.of("doctors", doctors));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch doctors"));
        }
    }

    // 3. Add New Doctor
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> addDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {
        try {
            ResponseEntity<Map<String, String>> validation = services.validateToken(token, "admin");
            if (validation.getStatusCode() != HttpStatus.OK) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", validation.getBody().get("result")));
            }

            int added = doctorService.saveDoctor(doctor);
            if (added == 1) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of("result", "Doctor added to db"));
            } else if (added == -1) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("result", "Doctor already exists"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Failed to add doctor"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Some internal error occurred"));
        }
    }

    // 4. Doctor Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginDoctor(@RequestBody Map<String, String> login) {
        try {
            return doctorService.validateDoctor(login.get("email"), login.get("password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Login failed"));
        }
    }

    // 5. Update Doctor Details
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {
        try {
            ResponseEntity<Map<String, String>> validation = services.validateToken(token, "admin");
            if (validation.getStatusCode() != HttpStatus.OK) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", validation.getBody().get("result")));
            }

            int updated = doctorService.updateDoctor(doctor);
            if (updated != 1 ) {
                return ResponseEntity.ok(Map.of("result", "Doctor updated"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("result", "Doctor not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Some internal error occurred"));
        }
    }

    // 6. Delete Doctor
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable Long id,
            @PathVariable String token) {
        try {
            ResponseEntity<Map<String, String>> validation = services.validateToken(token, "admin");
            if (validation.getStatusCode() != HttpStatus.OK) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", validation.getBody().get("result")));
            }

            int deleted = doctorService.deleteDoctor(id);
            if (deleted!=1) {
                return ResponseEntity.ok(Map.of("result", "Doctor deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("result", "Doctor not found with id"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Some internal error occurred"));
        }
    }

    // 7. Filter Doctors
    // Uses query parameters so callers can omit any combination safely:
    // /doctor/filter?name=Emily&time=AM&speciality=Dermatologist
    // /doctor/filter?speciality=Dermatologist
    // /doctor/filter (no params -> all doctors)
    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> filterDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String time,
            @RequestParam(required = false) String speciality) {
        try {
            Map<String, Object> filtered = services.filterDoctor(name, speciality, time);
            return ResponseEntity.ok(filtered);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to filter doctors"));
        }
    }
}
