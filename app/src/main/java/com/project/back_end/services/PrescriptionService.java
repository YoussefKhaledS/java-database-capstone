package com.project.back_end.services;


import com.project.back_end.models.Prescription;
import com.project.back_end.repo.PrescriptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    PrescriptionRepository prescriptionRepository;

    @Transactional
    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {
        try {
            prescriptionRepository.save(prescription);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Prescription saved"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error saving prescription"));
        }
    }

    @Transactional
    public ResponseEntity<Map<String , Object>> getPrescription(Long appointmentId) {
        try {
            List<Prescription> prescription = prescriptionRepository.findByAppointmentId(appointmentId);
            if (Objects.nonNull(prescription)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(Map.of("prescription", prescription));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "No prescription found for the given appointment"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error fetching prescription"));
        }
    }

}
