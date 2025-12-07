package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Services;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "prescription")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Services services;
    private final AppointmentService appointmentService;

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @PathVariable String token,
            @Valid @RequestBody Prescription prescription) {
        try {
            ResponseEntity<Map<String, String>> validation = services.validateToken(token, "doctor");
            if (!validation.getStatusCode().is2xxSuccessful()) {
                Map<String, String> body = validation.getBody() != null
                        ? new HashMap<>(validation.getBody())
                        : Map.of("error", "Invalid token");
                return ResponseEntity.status(validation.getStatusCode()).body(body);
            }

            ResponseEntity<Map<String, String>> response = prescriptionService.savePrescription(prescription);
            if (response.getStatusCode().is2xxSuccessful()) {
                appointmentService.changeStatus(prescription.getAppointmentId(), 1);
            }

            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to save prescription"));
        }
    }

    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<Map<String, Object>> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token) {
        try {
            ResponseEntity<Map<String, String>> validation = services.validateToken(token, "doctor");
            if (!validation.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> body = validation.getBody() != null
                        ? new HashMap<>(validation.getBody())
                        : Map.of("error", "Invalid token");
                return ResponseEntity.status(validation.getStatusCode()).body(body);
            }

            return prescriptionService.getPrescription(appointmentId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to fetch prescription"));
        }
    }
}
