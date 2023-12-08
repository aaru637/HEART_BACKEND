package com.heart.heart.Controller;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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

    @GetMapping("/api/admin/confirm-account/")
    public ModelAndView adminConfirmAccount(@RequestParam String id) {
        ModelAndView modelAndView = new ModelAndView("confirmation-mail");
        Admin admin = adminService.getAdmin(id);
        if (admin.getId() == null) {
            modelAndView.addAllObjects(new HashMap<>() {
                {
                    put("type", "not-expired");
                    put("source", "https://lottie.host/d4722b1c-a3cc-4d71-bb0c-cbd25d67b234/VdqG2zsuza.json");
                    put("content", "UnAuthorized User.");
                    put("result", "Please Register Yourself with us...");
                }
            });
            return modelAndView;
        }
        ConfirmationToken cToken = cTokenService.getConfirmationToken(id);
        LocalDateTime date = LocalDateTime.now(Clock.systemDefaultZone());
        if (admin.getEmailVerified()) {
            modelAndView.addAllObjects(new HashMap<>() {
                {
                    put("type", "not-expired");
                    put("source", "https://lottie.host/d7bbb351-8a1a-48d8-bc05-dda1b4e88e42/XgERJyOwAM.json");
                    put("content", "Your email has been already verified.");
                    put("result", "Thank You...");
                }
            });
            return modelAndView;
        } else {
            if (date.isBefore(cToken.getTokenExpired())) {
                admin.setEmailVerified(true);
                adminService.addAdmin(admin);
                modelAndView.addAllObjects(new HashMap<>() {
                    {
                        put("type", "not-expired");
                        put("source", "https://lottie.host/bc483178-2b7a-4ce7-8ccf-0e5f434dac87/95GaVwcaO9.json");
                        put("content", "Account Confirmed Successfully.");
                        put("result", "Thank You...");
                    }
                });
                return modelAndView;
            } else {
                modelAndView.addAllObjects(new HashMap<>() {
                    {
                        put("type", "expired");
                        put("source", "https://lottie.host/2ae609a6-1a86-401a-9ed0-51abc913e822/ZKGfV136AH.json");
                        put("content", "Session Expired.");
                        put("result", Urls.ADMIN_BASE_URL
                                + "resent-email/?id=" + admin.getId());
                    }
                });
                return modelAndView;
            }
        }
    }

    @GetMapping("/api/student/confirm-account/")
    public ModelAndView studentConfirmAccount(@RequestParam String id) {
        ModelAndView modelAndView = new ModelAndView("confirmation-mail");
        Student student = studentService.getStudent(id);
        if (student.getId() == null) {
            modelAndView.addAllObjects(new HashMap<>() {
                {
                    put("type", "not-expired");
                    put("source", "https://lottie.host/d4722b1c-a3cc-4d71-bb0c-cbd25d67b234/VdqG2zsuza.json");
                    put("content", "UnAuthorized User.");
                    put("result", "Please Register Yourself with us...");
                }
            });
            return modelAndView;
        }
        ConfirmationToken cToken = cTokenService.getConfirmationToken(id);
        LocalDateTime date = LocalDateTime.now();
        if (student.getEmailVerified()) {
            modelAndView.addAllObjects(new HashMap<>() {
                {
                    put("type", "not-expired");
                    put("source", "https://lottie.host/d7bbb351-8a1a-48d8-bc05-dda1b4e88e42/XgERJyOwAM.json");
                    put("content", "Your email has been already verified.");
                    put("result", "Thank You...");
                }
            });
            return modelAndView;
        } else {
            System.out.println(date);
            System.out.println(cToken.getTokenExpired());
            if (date.isBefore(cToken.getTokenExpired())) {
                student.setEmailVerified(true);
                studentService.addStudent(student);
                modelAndView.addAllObjects(new HashMap<>() {
                    {
                        put("type", "not-expired");
                        put("source", "https://lottie.host/bc483178-2b7a-4ce7-8ccf-0e5f434dac87/95GaVwcaO9.json");
                        put("content", "Account Confirmed Successfully.");
                        put("result", "Thank You...");
                    }
                });
                return modelAndView;
            } else {
                modelAndView.addAllObjects(new HashMap<>() {
                    {
                        put("type", "expired");
                        put("source", "https://lottie.host/2ae609a6-1a86-401a-9ed0-51abc913e822/ZKGfV136AH.json");
                        put("content", "Session Expired.");
                        put("result", Urls.STUDENT_BASE_URL
                                + "resent-email/?id=" + student.getId());
                    }
                });
                return modelAndView;
            }
        }
    }
}
