package com.project.back_end.mvc;


import com.project.back_end.models.Admin;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    TokenService tokenService ;


    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable("token") String token) {
        String role = "admin";
        String validationResponse = tokenService.validateToken(token, role);

        if (validationResponse.isEmpty()) {
            return "admin/adminDashboard";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable("token") String token) {
        String role = "doctor";
        String validationResponse = tokenService.validateToken(token, role);

        if (validationResponse.isEmpty()) {
            return "doctor/doctorDashboard";
        } else {
            return "redirect:/";
        }
    }


}
