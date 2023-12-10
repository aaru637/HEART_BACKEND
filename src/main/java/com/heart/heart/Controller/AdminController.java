package com.heart.heart.Controller;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
<<<<<<< HEAD
=======
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
>>>>>>> 1a6f4af14917fc551b6fa858730aaa21401fdcf5

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heart.heart.Concrete.Admin;
import com.heart.heart.Concrete.Responses.AdminResponseClass;
import com.heart.heart.Concrete.ConfirmationToken;
import com.heart.heart.Concrete.Password;
import com.heart.heart.Concrete.Responses.ListAdminResponseClass;
import com.heart.heart.Concrete.Responses.StringResponseClass;
import com.heart.heart.Email.EmailSender;
import com.heart.heart.Repository.AdminRepository;
import com.heart.heart.Service.AdminService;
import com.heart.heart.Service.ConfirmationTokenService;

import jakarta.mail.MessagingException;

@RestController
public class AdminController {

        @Autowired
        private AdminService adminService;

        @Autowired
        private EmailSender emailSender;

        @Autowired
        private ConfirmationTokenService cTokenService;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private AdminRepository adminRepository;

        @GetMapping("/")
        public String hello() {
                return "Welcome to Deadlock";
        }

<<<<<<< HEAD
        @GetMapping("/api/admin/")
        public ResponseEntity<String> getAdmin(@RequestParam String id) throws JsonProcessingException {
=======
        @GetMapping("/admin/{id}")
        public ResponseEntity<String> getAdmin(@PathVariable String id) throws JsonProcessingException {
>>>>>>> 1a6f4af14917fc551b6fa858730aaa21401fdcf5
                try {
                        Admin admin = adminService.getAdmin(id);
                        if (admin.getId() == null) {
                                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                                .body(objectMapper
                                                                .writeValueAsString(new AdminResponseClass(
                                                                                "admin-not-found", true, new Admin())));
                        } else {
                                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                                .body(objectMapper
                                                                .writeValueAsString(new AdminResponseClass(
                                                                                "admin-found", true, admin)));
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON)
                                        .body(objectMapper
                                                        .writeValueAsString(new AdminResponseClass("failure", false,
                                                                        new Admin())));
                }
        }

<<<<<<< HEAD
        @GetMapping("/api/admin/login/")
        public ResponseEntity<String> adminLogin(@RequestParam String username, @RequestParam String password)
                        throws JsonProcessingException {
                try {
                        String result = adminService.adminLogin(new String[] { username, password });
=======
        @GetMapping("/admin/login/{data}")
        public ResponseEntity<String> adminLogin(@PathVariable String... data) throws JsonProcessingException {
                try {
                        String result = adminService.adminLogin(data);
>>>>>>> 1a6f4af14917fc551b6fa858730aaa21401fdcf5
                        if (result.startsWith("attempts")) {
                                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                                .body(objectMapper
                                                                .writeValueAsString(
                                                                                new StringResponseClass(
                                                                                                "attempts expired",
                                                                                                true,
                                                                                                result.substring(9))));
                        } else if (result.equals("null")) {
                                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                                .body(objectMapper
                                                                .writeValueAsString(
                                                                                new StringResponseClass(
                                                                                                "admin-not-found", true,
                                                                                                result)));
                        } else {
                                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                                .body(objectMapper
                                                                .writeValueAsString(
                                                                                new StringResponseClass("admin-found",
                                                                                                true, result)));
                        }
                } catch (Exception e) {
                        return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON)
                                        .body(objectMapper
                                                        .writeValueAsString(
                                                                        new StringResponseClass("failure", false,
                                                                                        "error")));
                }
        }

<<<<<<< HEAD
        @GetMapping("/api/admin")
=======
        @GetMapping("/admin")
>>>>>>> 1a6f4af14917fc551b6fa858730aaa21401fdcf5
        public ResponseEntity<String> getAllAdmin() throws JsonProcessingException {
                try {
                        List<Admin> admins = adminService.getAllAdmins();
                        if (admins.size() != 0) {
                                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                                .body(objectMapper
                                                                .writeValueAsString(
                                                                                new ListAdminResponseClass(
                                                                                                "admins-found", true,
                                                                                                admins)));
                        } else {
                                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                                .body(objectMapper
                                                                .writeValueAsString(
                                                                                new ListAdminResponseClass(
                                                                                                "admins-not-found",
                                                                                                true, admins)));
                        }
                } catch (Exception e) {
                        return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON)
                                        .body(objectMapper
                                                        .writeValueAsString(
                                                                        new ListAdminResponseClass(
                                                                                        "failure", false,
                                                                                        new ArrayList<Admin>())));
                }
        }

<<<<<<< HEAD
        @PostMapping("/api/admin")
        public ResponseEntity<String> addAdmin(@RequestBody Admin admin) throws JsonProcessingException {
                try {
                        if (adminService.getAdmin(admin.getId()) == null) {
                                admin.setPassword(adminService.encode(admin.getPassword()));
                        }
=======
        @PostMapping("/admin")
        public ResponseEntity<String> addAdmin(@RequestBody Admin admin) throws JsonProcessingException {
                try {
                        admin.setPassword(adminService.encode(admin.getPassword()));
>>>>>>> 1a6f4af14917fc551b6fa858730aaa21401fdcf5
                        addToken(admin);
                        String result = emailValidation(admin);
                        if (result.equals("email-sent")) {
                                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                                .body(objectMapper
                                                                .writeValueAsString(
                                                                                new StringResponseClass("email-sent",
                                                                                                true, result)));
                        } else if (result.equals("email-sent-error")) {
<<<<<<< HEAD
                                admin.setEmailVerified(false);
                                adminService.addAdmin(admin);
=======
                                adminService.deleteAdmin(admin.getId());
>>>>>>> 1a6f4af14917fc551b6fa858730aaa21401fdcf5
                                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                                .body(objectMapper
                                                                .writeValueAsString(
                                                                                new StringResponseClass(
                                                                                                "email-sent-error",
                                                                                                false, result)));
                        } else {
                                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                                .body(objectMapper
                                                                .writeValueAsString(
                                                                                new StringResponseClass(
                                                                                                "add-admin-error",
                                                                                                false, result)));
                        }
                } catch (Exception e) {
                        return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON)
                                        .body(objectMapper
                                                        .writeValueAsString(
                                                                        new StringResponseClass("failure", false,
                                                                                        "error")));
                }
        }

<<<<<<< HEAD
        @GetMapping("/admin/resent-email/")
        public ModelAndView emailResend(@RequestParam String id) {
                ModelAndView modelAndView = new ModelAndView("email-sent");
                try {
                        Admin admin = adminService.getAdmin(id);
                        if (adminService.getAdmin(admin.getId()) == null) {
                                admin.setPassword(adminService.encode(admin.getPassword()));
                        }
                        addToken(admin);
                        String result = emailValidation(admin);
                        if (result.equals("email-sent")) {
                                modelAndView.addAllObjects(new HashMap<>() {
                                        {
                                                put("source", "https://lottie.host/bc483178-2b7a-4ce7-8ccf-0e5f434dac87/95GaVwcaO9.json");
                                                put("content", "Email Sent Successfully.");
                                                put("result", "Thank You...");
                                        }
                                });
                                return modelAndView;
                        } else if (result.equals("email-sent-error")) {
                                admin.setEmailVerified(false);
                                adminService.addAdmin(admin);
                                cTokenService.deleteConfirmationToken(id);
                                modelAndView.addAllObjects(new HashMap<>() {
                                        {
                                                put("source", "https://lottie.host/fa0b77aa-710c-4c56-a940-f5fc482bd754/uL1Xr2VO68.json");
                                                put("content", "Error Occuring while sending email.");
                                                put("result", "Please Try again later.");
                                        }
                                });
                                return modelAndView;
                        } else {
                                cTokenService.deleteConfirmationToken(id);
                                modelAndView.addAllObjects(new HashMap<>() {
                                        {
                                                put("source", "https://lottie.host/fa0b77aa-710c-4c56-a940-f5fc482bd754/uL1Xr2VO68.json");
                                                put("content", "Error Occured.");
                                                put("result", "Please Try again later.");
                                        }
                                });
                                return modelAndView;
                        }
                } catch (Exception e) {
                        cTokenService.deleteConfirmationToken(id);
                        modelAndView.addAllObjects(new HashMap<>() {
                                {
                                        put("source", "https://lottie.host/fa0b77aa-710c-4c56-a940-f5fc482bd754/uL1Xr2VO68.json");
                                        put("content", "Error Occured.");
                                        put("result", "Please Try again later.");
                                }
                        });
                        return modelAndView;
                }
        }

        @GetMapping("/api/admin/username-check/")
        public ResponseEntity<String> checkUsername(@RequestParam String username) throws JsonProcessingException {
=======
        @GetMapping("/admin/resent-email/{id}")
        public ResponseEntity<String> emailReSend(@PathVariable String id) throws JsonProcessingException {
                try {
                        Admin admin = adminService.getAdmin(id);
                        return addAdmin(admin);
                } catch (Exception e) {
                        return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON)
                                        .body(objectMapper
                                                        .writeValueAsString(
                                                                        new StringResponseClass("failure", true,
                                                                                        "error")));
                }
        }

        @GetMapping("/admin/admin-username-check/{username}")
        public ResponseEntity<String> checkUsername(@PathVariable String username) throws JsonProcessingException {
>>>>>>> 1a6f4af14917fc551b6fa858730aaa21401fdcf5
                try {
                        String result = adminService.userNameCheck(username);
                        return adminUserAndCodeCheck(result);
                } catch (Exception e) {
                        return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON)
                                        .body(objectMapper
                                                        .writeValueAsString(
                                                                        new StringResponseClass("failure", false,
                                                                                        "error")));
                }
        }

<<<<<<< HEAD
        @GetMapping("/api/admin/admin-code-check/")
        public ResponseEntity<String> checkAdminCode(@RequestParam String adminCode) throws JsonProcessingException {
=======
        @GetMapping("/admin/admin-code-check/{adminCode}")
        public ResponseEntity<String> checkAdminCode(@PathVariable String adminCode) throws JsonProcessingException {
>>>>>>> 1a6f4af14917fc551b6fa858730aaa21401fdcf5
                try {
                        String result = adminService.adminCodeCheck(adminCode);
                        return adminUserAndCodeCheck(result);
                } catch (Exception e) {
                        return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON)
                                        .body(objectMapper
                                                        .writeValueAsString(
                                                                        new StringResponseClass("failure", false,
                                                                                        "error")));
                }
        }

<<<<<<< HEAD
        @GetMapping("/api/admin/accept-student/")
        public ResponseEntity<String> acceptStudentRequests(@RequestParam String aId, @RequestParam String sId)
                        throws JsonProcessingException {
                try {
                        String result = adminService.acceptStudentRequests(aId, sId);
=======
        @GetMapping("/admin/accept-student/{aId}/{sId}/{value}")
        public ResponseEntity<String> acceptStudentRequests(@PathVariable String aId, @PathVariable String sId,
                        @PathVariable Boolean value) throws JsonProcessingException {
                try {
                        String result = adminService.acceptStudentRequests(aId, sId, value);
>>>>>>> 1a6f4af14917fc551b6fa858730aaa21401fdcf5
                        if (result.equals("ok")) {
                                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                                .body(objectMapper
                                                                .writeValueAsString(
                                                                                new StringResponseClass(
                                                                                                "student-accepted",
                                                                                                true, result)));
                        } else if (result.equals("user-already-enrolled")) {
                                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                                .body(objectMapper
                                                                .writeValueAsString(
                                                                                new StringResponseClass(
                                                                                                "user-already-enrolled",
                                                                                                true, result)));
                        } else {
                                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                                .body(objectMapper
                                                                .writeValueAsString(
                                                                                new StringResponseClass(
                                                                                                "student-not-accepted",
                                                                                                false, result)));
                        }
                } catch (Exception e) {
                        return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON)
                                        .body(objectMapper
                                                        .writeValueAsString(
                                                                        new StringResponseClass("failure", false,
                                                                                        "error")));
                }
        }

<<<<<<< HEAD
        @GetMapping("/api/admin/email-verify-check/")
        public ResponseEntity<String> emailVerifyCheck(@RequestParam String id) throws JsonProcessingException {
=======
        @GetMapping("/admin/email-verify-check/{id}")
        public ResponseEntity<String> emailVerifyCheck(@PathVariable String id) throws JsonProcessingException {
>>>>>>> 1a6f4af14917fc551b6fa858730aaa21401fdcf5
                try {
                        Admin admin = adminService.getAdmin(id);
                        if (admin.getId() == null) {
                                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                                .body(objectMapper
                                                                .writeValueAsString(new StringResponseClass(
                                                                                "UnAuthorized User.", true, "false")));
                        }
                        Boolean result = adminService.getAdmin(id).getEmailVerified();
                        if (result) {
                                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                                .body(objectMapper
                                                                .writeValueAsString(
                                                                                new StringResponseClass(
                                                                                                "email-verified", true,
                                                                                                "true")));
                        } else {
                                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                                .body(objectMapper
                                                                .writeValueAsString(
                                                                                new StringResponseClass(
                                                                                                "email-not-verified",
                                                                                                true, "false")));
                        }
                } catch (Exception e) {
                        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                        .body(objectMapper
                                                        .writeValueAsString(
                                                                        new StringResponseClass("failure", false,
                                                                                        "error")));
                }
        }

<<<<<<< HEAD
        @GetMapping("/api/admin/forgot-password/")
        public ResponseEntity<String> forgotPassword(@RequestParam String username)
=======
        @GetMapping("/admin/forgot-password/{username}")
        public ResponseEntity<String> forgotPassword(@PathVariable String username, Model model)
>>>>>>> 1a6f4af14917fc551b6fa858730aaa21401fdcf5
                        throws JsonProcessingException {
                try {
                        Optional<Admin> admin = adminRepository.findByUsername(username);
                        if (admin.isPresent()) {
<<<<<<< HEAD
                                if (sendForgotPasswordEmail(admin.get().getEmail(), admin.get().getId())) {
                                        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                                        .body(objectMapper.writeValueAsString(
                                                                        new StringResponseClass(
                                                                                        "admin-found-email-sent", true,
                                                                                        admin.get().getEmail())));
                                } else {
                                        return ResponseEntity.internalServerError()
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .body(objectMapper.writeValueAsString(new StringResponseClass(
                                                                        "failure", false, "error")));
                                }

=======
                                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                                .body(objectMapper.writeValueAsString(
                                                                new StringResponseClass("admin-found-email-sent", true,
                                                                                admin.get().getEmail())));
>>>>>>> 1a6f4af14917fc551b6fa858730aaa21401fdcf5
                        } else {
                                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                                .body(objectMapper.writeValueAsString(new StringResponseClass(
                                                                "admin-not-found", true, username)));
                        }
                } catch (Exception e) {
                        return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON)
                                        .body(objectMapper.writeValueAsString(new StringResponseClass(
                                                        "failure", false, "error")));
                }
        }

<<<<<<< HEAD
        @GetMapping("/api/admin/reset-password/")
        public ModelAndView resetPassword(@RequestParam String id) {
                ModelAndView modelAndView = new ModelAndView("reset-password");
                modelAndView.addAllObjects(new HashMap<>() {
                        {
                                put("username", adminService.getAdmin(id).getUsername());
                                put("id", id);
                                put("password", new Password());
                                put("role", "admin");
                        }
                });
                return modelAndView;
        }

        @PostMapping("/admin/update-password")
        public ModelAndView updatePassword(@ModelAttribute Password password) {
                ModelAndView modelAndView = new ModelAndView("password-updated");
                Admin admin = adminService.getAdmin(password.getId());
                admin.setPassword(adminService.encode(password.getPassword()));
                return modelAndView;
        }

        @GetMapping("/admin/accept-student/")
        public ModelAndView acceptStudentRequestsEmail(@RequestParam String aId, @RequestParam String sId) {
                ModelAndView modelAndView = new ModelAndView("student-accepted");
                try {
                        String result = adminService.acceptStudentRequests(aId, sId);
                        if (result.equals("ok")) {
                                modelAndView.addAllObjects(new HashMap<>() {
                                        {
                                                put("source", "https://lottie.host/bc483178-2b7a-4ce7-8ccf-0e5f434dac87/95GaVwcaO9.json");
                                                put("content", "Student Accepted Successfully.");
                                                put("result", "Thank You...");
                                        }
                                });
                                return modelAndView;
                        } else if (result.equals("user-already-enrolled")) {
                                modelAndView.addAllObjects(new HashMap<>() {
                                        {
                                                put("source", "https://lottie.host/d7bbb351-8a1a-48d8-bc05-dda1b4e88e42/XgERJyOwAM.json");
                                                put("content", "Student Already Enrolled.");
                                                put("result", "Thank You...");
                                        }
                                });
                                return modelAndView;
                        } else {
                                modelAndView.addAllObjects(new HashMap<>() {
                                        {
                                                put("source", "https://lottie.host/fa0b77aa-710c-4c56-a940-f5fc482bd754/uL1Xr2VO68.json");
                                                put("content", "Error Occured.");
                                                put("result", "Please Try again later.");
                                        }
                                });
                                return modelAndView;
                        }
                } catch (Exception e) {
                        modelAndView.addAllObjects(new HashMap<>() {
                                {
                                        put("source", "https://lottie.host/fa0b77aa-710c-4c56-a940-f5fc482bd754/uL1Xr2VO68.json");
                                        put("content", "Error Occured.");
                                        put("result", "Please Try again later.");
                                }
                        });
                        return modelAndView;
                }
        }

        // Methods for used above to reduce code and to understand code.

        private String emailValidation(Admin admin) {
                return emailSender.sendConfirmationHTMLEmail(admin.getEmail(), "Account Confirmation", "admin",
                                admin.getId(), admin.getName())
=======
        @GetMapping("/admin/forgot/{id}")
        public ModelAndView forgot(@PathVariable String id, Model model) {
                Admin admin = adminService.getAdmin(id);
                ModelAndView modelAndView = new ModelAndView("index");
                modelAndView.addObject("username", admin.getUsername());
                return modelAndView;
        }

        private String emailBody(Admin admin) {
                return "Hi, " + admin.getName() + ", \n " +
                                "Welcome to the Heart❤️" +
                                "\n\n" +
                                "This is valid only 1 hour." +
                                "Click the below link to verify your account :" + Urls.ADMIN_BASE_URL
                                + "confirm-account/"
                                + admin.getId();
        }

        private String emailValidation(Admin admin) {
                return emailSender.sendEmail(admin.getEmail(), "Account Verification",
                                emailBody(admin))
>>>>>>> 1a6f4af14917fc551b6fa858730aaa21401fdcf5
                                                ? ((adminService.addAdmin(admin) != null) ? "email-sent"
                                                                : "add-admin-error")
                                                : "email-sent-error";
        }

        private void addToken(Admin admin) {
                ConfirmationToken cToken = new ConfirmationToken(admin.getId(), admin.getEmail(), LocalDateTime.now(),
                                LocalDateTime.from(LocalDateTime.now(Clock.systemDefaultZone()).plusHours(1)));
                cTokenService.addConfirmationToken(cToken);
        }

        private ResponseEntity<String> adminUserAndCodeCheck(String result) throws JsonProcessingException {
                if (result.equals("true")) {
                        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                        .body(objectMapper
                                                        .writeValueAsString(
                                                                        new StringResponseClass("admin-found", true,
                                                                                        result)));
                } else if (result.equals("false")) {
                        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                        .body(objectMapper
                                                        .writeValueAsString(
                                                                        new StringResponseClass("admin-not-found", true,
                                                                                        result)));
                } else {
                        return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON)
                                        .body(objectMapper
                                                        .writeValueAsString(
                                                                        new StringResponseClass("failure", false,
                                                                                        "error")));
                }
<<<<<<< HEAD
        }

        private boolean sendForgotPasswordEmail(String mail, String id)
                        throws MessagingException, IOException {
                return emailSender.sendResetPasswordHTMLEmail(mail, "Reset Password", id, "admin");
=======
>>>>>>> 1a6f4af14917fc551b6fa858730aaa21401fdcf5
        }

}
