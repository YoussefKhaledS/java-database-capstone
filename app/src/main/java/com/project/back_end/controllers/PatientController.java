package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    @GetMapping("/{token}")
    public ResponseEntity<Map<String, Object>> getPatient(@PathVariable String token) {
        try {
            ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
            if (!validation.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> body = validation.getBody() != null
                        ? new HashMap<>(validation.getBody())
                        : Map.of("error", "Invalid token");
                return ResponseEntity.status(validation.getStatusCode()).body(body);
            }
            return patientService.getPatientDetails(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve patient details"));
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@Valid @RequestBody Patient patient) {
        try {
            Boolean exists = service.validatePatient(patient.getEmail(), patient.getPhone());
            if (Boolean.TRUE.equals(exists)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("result", "Patient with email id or phone no already exist"));
            }
            int created = patientService.createPatient(patient);
            if (created == 1) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of("result", "Signup successful"));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("result", "Internal server error"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("result", "Internal server error"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
        try {
            return service.validatePatientLogin(login);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Login failed"));
        }
    }

    @GetMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> getPatientAppointment(
            @PathVariable Long id,
            @PathVariable String token) {
        try {
            ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
            if (!validation.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> body = validation.getBody() != null
                        ? new HashMap<>(validation.getBody())
                        : Map.of("error", "Invalid token");
                return ResponseEntity.status(validation.getStatusCode()).body(body);
            }
            return patientService.getPatientAppointment(id, token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve patient appointments"));
        }
    }

    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<Map<String, Object>> filterPatientAppointment(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token) {
        try {
            ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
            if (!validation.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> body = validation.getBody() != null
                        ? new HashMap<>(validation.getBody())
                        : Map.of("error", "Invalid token");
                return ResponseEntity.status(validation.getStatusCode()).body(body);
            }
            return service.filterPatient(condition, name, token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to filter patient appointments"));
        }
    }
}
