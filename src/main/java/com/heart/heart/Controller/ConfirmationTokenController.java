package com.heart.heart.Controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.heart.heart.Concrete.Admin;
import com.heart.heart.Concrete.ConfirmationToken;
import com.heart.heart.Concrete.Student;
import com.heart.heart.Concrete.Urls;
import com.heart.heart.Service.AdminService;
import com.heart.heart.Service.ConfirmationTokenService;
import com.heart.heart.Service.StudentService;

@RestController
public class ConfirmationTokenController {

    @Autowired
    private ConfirmationTokenService cTokenService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private StudentService studentService;

    @GetMapping("/admin/confirm-account/{id}")
    public String adminConfirmAccount(@PathVariable String id) {
        Admin admin = adminService.getAdmin(id);
        ConfirmationToken cToken = cTokenService.getConfirmationToken(id);
        LocalDateTime date = LocalDateTime.now();
        if (admin.isEmailVerified()) {
            return "Your Email has been already verified";
        } else {
            if (date.isBefore(cToken.getTokenExpired())) {
                admin.setEmailVerified(true);
                adminService.addAdmin(admin);
                return "Congratulations. Your email has been verified successfully.";
            } else {
                return "Session Expired. Click the following link to Resend Email"
                        + Urls.ADMIN_BASE_URL + "resent-email/" + admin.getId();
            }
        }
    }

    @GetMapping("/student/confirm-account/{id}")
    public String studentConfirmAccount(@PathVariable String id) {
        Student student = studentService.getStudent(id);
        ConfirmationToken cToken = cTokenService.getConfirmationToken(id);
        LocalDateTime date = LocalDateTime.now();
        if (student.isEmailVerified()) {
            return "Your Email has been already verified";
        } else {
            if (date.isBefore(cToken.getTokenExpired())) {
                student.setEmailVerified(true);
                studentService.addStudent(student);
                return "Congratulations. Your email has been verified successfully.";
            } else {
                return "Session Expired. Click the following link to Resend Email"
                        + Urls.STUDENT_BASE_URL + "resent-email/" + student.getId();
            }
        }
    }
}
