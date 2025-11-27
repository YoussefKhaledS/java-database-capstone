package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    AppointmentService appointmentService;
    Service service ;

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, Object>> getAppointments(
            @PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable("patientName") String patientName,
            @PathVariable("token") String token) {

        try{
            // 1️⃣ Validate token
            if (service.validateToken(token, "doctor").getStatusCode()  != HttpStatus.OK) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired token"));
            }

            // 2️⃣ Fetch appointments
            Map<String, Object> appointments = appointmentService.getAppointment(patientName, date, token);

            // 3️⃣ Return response
            return ResponseEntity.ok(appointments);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while processing the request"));
        }
    }


    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(@PathVariable String token , @RequestBody Appointment appointment){
        try{
            if(service.validateToken(token , "patient").getStatusCode() != HttpStatus.OK){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error" , "Invalid or expired token"));
            }
            int isValid = service.validateAppointment(appointment);
            if(isValid == -1 ){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error" , "Doctor Does not exist"));
            }else if(isValid == 0 ){
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error" , "Appointment slot already taken"));
            }
            if(appointmentService.bookAppointment(appointment) == 1 ){
                return ResponseEntity.ok(Map.of("message" , "Appointment booked successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error" , "Failed to book appointment"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while booking the appointment"));
        }
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(
            @PathVariable("token") String token,
            @RequestBody Appointment appointment) {
        try{
            // Validate token for patient
            ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
            if (validation.getStatusCode() != HttpStatus.OK) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", validation.getBody().get("result")));
            }

            // Update appointment
            return appointmentService.updateAppointment(appointment);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while updating the appointment"));
        }
    }

    // Cancel an appointment
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
            @PathVariable("id") Long id,
            @PathVariable("token") String token) {
        try{
            // Validate token for patient
            ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
            if (validation.getStatusCode() != HttpStatus.OK) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", validation.getBody().get("result")));
            }

            // Cancel appointment
            return appointmentService.cancelAppointment(id, token);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while cancelling the appointment"));
        }
    }


}
