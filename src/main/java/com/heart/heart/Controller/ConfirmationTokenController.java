package com.heart.heart.Controller;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

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
        if (admin.getId() == null) {
            return "UnAuthorized User. Please request to resent a verification mail in your app.";
        }
        ConfirmationToken cToken = cTokenService.getConfirmationToken(id);
        LocalDateTime date = LocalDateTime.now(Clock.systemDefaultZone());
        if (admin.getEmailVerified()) {
            return "Your Email has been already verified";
        } else {
            if (date.isBefore(cToken.getTokenExpired())) {
                admin.setEmailVerified(true);
                adminService.addAdmin(admin);
                return "Congratulations. Your email has been verified successfully.";
            } else {
                return "Session Expired. Click the following link to Resend Email <a href =" + Urls.ADMIN_BASE_URL
                        + "resent-email/" + admin.getId() + ">"
                        + Urls.ADMIN_BASE_URL + "resent-email/" + admin.getId() + "</a>";
            }
        }
    }

    @GetMapping("/student/confirm-account/{id}")
    public String studentConfirmAccount(@PathVariable String id) {
        Student student = studentService.getStudent(id);
        if (student.getId() == null) {
            return "UnAuthorized User. Please request to resent a verification mail in your app.";
        }
        ConfirmationToken cToken = cTokenService.getConfirmationToken(id);
        LocalDateTime date = LocalDateTime.now();
        if (student.getEmailVerified()) {
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
