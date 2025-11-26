package com.project.back_end.DTO;

import com.project.back_end.models.Appointment;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class AppointmentDTO {

    private Long id ;
    private Long doctorId ;
    private String doctorName ;
    private Long patientId ;
    private String patientName ;
    private String patientEmail ;
    private String patientPhone ;
    private String patientAddress ;
    private LocalDateTime appointmentTime ;
    private int status ;
    private LocalDate appointmentDate ;
    private LocalTime appointmentTimeOnly ;
    private LocalDateTime endTime ;

    public AppointmentDTO(
            Long id,
            Long doctorId,
            String doctorName,
            Long patientId,
            String patientName,
            String patientEmail,
            String patientPhone,
            String patientAddress,
            LocalDateTime appointmentTime,
            int status
    ) {
        this.id = id;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.patientPhone = patientPhone;
        this.patientAddress = patientAddress;
        this.appointmentTime = appointmentTime;
        this.status = status;

        // Automatically computed fields
        this.appointmentDate = appointmentTime.toLocalDate();
        this.appointmentTimeOnly = appointmentTime.toLocalTime();
        this.endTime = appointmentTime.plusHours(1);
    }
    public AppointmentDTO(Appointment appointment) {
        this.id = appointment.getId();

        // Doctor info
        this.doctorId = appointment.getDoctor().getId();
        this.doctorName = appointment.getDoctor().getName();

        // Patient info
        this.patientId = appointment.getPatient().getId();
        this.patientName = appointment.getPatient().getName();
        this.patientEmail = appointment.getPatient().getEmail();
        this.patientPhone = appointment.getPatient().getPhone();
        this.patientAddress = appointment.getPatient().getAddress();

        // Appointment info
        this.appointmentTime = appointment.getAppointmentTime();
        this.status = appointment.getStatus();

        // Automatically computed fields
        this.appointmentDate = appointment.getAppointmentTime().toLocalDate();
        this.appointmentTimeOnly = appointment.getAppointmentTime().toLocalTime();
        this.endTime = appointment.getAppointmentTime().plusHours(1);
    }

// 14. Constructor:
//    - The constructor accepts all the relevant fields for the AppointmentDTO, including simplified fields for the doctor and patient (ID, name, etc.).
//    - It also calculates custom fields: 'appointmentDate', 'appointmentTimeOnly', and 'endTime' based on the 'appointmentTime' field.

// 15. Getters:
//    - Standard getter methods are provided for all fields: id, doctorId, doctorName, patientId, patientName, patientEmail, patientPhone, patientAddress, appointmentTime, status, appointmentDate, appointmentTimeOnly, and endTime.
//    - These methods allow access to the values of the fields in the AppointmentDTO object.

}
