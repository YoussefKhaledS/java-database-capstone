package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorService {
    DoctorRepository doctorRepository;
    AppointmentRepository appointmentRepository;
    TokenService tokenService ;

// 3. **Add @Transactional Annotation for Methods that Modify or Fetch Database Data**:
//    - Methods like `getDoctorAvailability`, `getDoctors`, `findDoctorByName`, `filterDoctorsBy*` should be annotated with `@Transactional`.
//    - The `@Transactional` annotation ensures that database operations are consistent and wrapped in a single transaction.
//    - Instruction: Add the `@Transactional` annotation above the methods that perform database operations or queries.
    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date){
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if(doctor == null)return List.of() ;

        List<String> allSlots = doctor.getAvailableTimes();
        if(allSlots.isEmpty() || allSlots == null) return List.of();

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        List<Appointment> bookedSlots = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId , start, end);

        List<String> bookedTimes = bookedSlots.stream()
                .map(appointment -> appointment.getAppointmentTime().toLocalTime().toString())
                .toList();
        List<String> availableSlots = allSlots.stream()
                .filter(slot -> !bookedTimes.contains(slot))
                .toList();
        return availableSlots;
    }
    @Transactional
    public int saveDoctor(Doctor doctor){
        try{
            String email = doctor.getEmail();
            Doctor existingDoctor = doctorRepository.findByEmail(email) ;
            if(existingDoctor != null){
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        }catch (Exception e){
            return 0;
        }
    }

    @Transactional
    public int updateDoctor(Doctor doctor){
        try{
            Long id = doctor.getId();
            Doctor existingDoctor = doctorRepository.findById(id).orElse(null) ;
            if(existingDoctor == null){
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        }catch (Exception e){
            return 0;
        }
    }

    @Transactional
    public List<Doctor> getDoctors(){
        return doctorRepository.findAll();
    }

    @Transactional
    public int deleteDoctor(Long doctorId){
        try{
            Doctor existingDoctor = doctorRepository.findById(doctorId).orElse(null) ;
            if(existingDoctor == null){
                return -1;
            }
            appointmentRepository.deleteAllByDoctorId(doctorId);
            doctorRepository.deleteById(doctorId);
            return 1;
        }catch (Exception e){
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String,String >> validateDoctor(String email, String password){
        try{
            Doctor existingDoctor = doctorRepository.findByEmail(email) ;
            if(existingDoctor == null){
                return ResponseEntity.status(404).body(Map.of("error","Doctor not found"));
            }
            if(!existingDoctor.getPassword().equals(password)){
                return ResponseEntity.status(401).body(Map.of("error","Invalid password"));
            }
            String token = tokenService.generateToken(existingDoctor.getId(), "DOCTOR");
            return ResponseEntity.ok(Map.of("token",token));
        }catch (Exception e){
            return ResponseEntity.status(500).body(Map.of("error","Internal server error"));
        }
    }

    @Transactional
    public Map<String , Object> findDoctorByName(String name ){
        List<Doctor> doctors = doctorRepository.findByNameLike(name) ;
        return Map.of("Doctors",doctors);
    }

    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        List<Doctor> filtered = filterDoctorByTime(doctors, amOrPm);
        return Map.of("doctors", filtered);
    }

    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameLike("%" + name + "%");
        List<Doctor> filtered = filterDoctorByTime(doctors, amOrPm);
        return Map.of("doctors", filtered);
    }

    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        List<Doctor> doctors = doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        return Map.of("doctors", doctors);
    }

    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        List<Doctor> filtered = filterDoctorByTime(doctors, amOrPm);
        return Map.of("doctors", filtered);
    }

    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        return Map.of("doctors", doctors);
    }

    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        List<Doctor> doctors = doctorRepository.findAll();
        List<Doctor> filtered = filterDoctorByTime(doctors, amOrPm);
        return Map.of("doctors", filtered);
    }

    // ------------------ Private helper method ------------------

    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        if (amOrPm == null || amOrPm.isEmpty()) return doctors;

        boolean isAM = amOrPm.equalsIgnoreCase("AM");

        return doctors.stream()
                .filter(doc -> doc.getAvailableTimes() != null && doc.getAvailableTimes().stream()
                        .anyMatch(slot -> {
                            int hour = Integer.parseInt(slot.split(":")[0]);
                            return isAM ? hour < 12 : hour >= 12;
                        }))
                .collect(Collectors.toList());
    }

   
}
