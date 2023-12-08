package com.heart.heart.Controller;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heart.heart.Concrete.Admin;
import com.heart.heart.Concrete.Responses.AdminResponseClass;
import com.heart.heart.Concrete.ConfirmationToken;
import com.heart.heart.Concrete.Responses.ListAdminResponseClass;
import com.heart.heart.Concrete.Responses.StringResponseClass;
import com.heart.heart.Concrete.Urls;
import com.heart.heart.Email.EmailSender;
import com.heart.heart.Repository.AdminRepository;
import com.heart.heart.Service.AdminService;
import com.heart.heart.Service.ConfirmationTokenService;

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

        @GetMapping("/admin/{id}")
        public ResponseEntity<String> getAdmin(@PathVariable String id) throws JsonProcessingException {
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

        @GetMapping("/admin/login/{data}")
        public ResponseEntity<String> adminLogin(@PathVariable String... data) throws JsonProcessingException {
                try {
                        String result = adminService.adminLogin(data);
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

        @GetMapping("/admin")
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

        @PostMapping("/admin")
        public ResponseEntity<String> addAdmin(@RequestBody Admin admin) throws JsonProcessingException {
                try {
                        admin.setPassword(adminService.encode(admin.getPassword()));
                        addToken(admin);
                        String result = emailValidation(admin);
                        if (result.equals("email-sent")) {
                                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                                .body(objectMapper
                                                                .writeValueAsString(
                                                                                new StringResponseClass("email-sent",
                                                                                                true, result)));
                        } else if (result.equals("email-sent-error")) {
                                adminService.deleteAdmin(admin.getId());
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

        @GetMapping("/admin/admin-code-check/{adminCode}")
        public ResponseEntity<String> checkAdminCode(@PathVariable String adminCode) throws JsonProcessingException {
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

        @GetMapping("/admin/accept-student/{aId}/{sId}/{value}")
        public ResponseEntity<String> acceptStudentRequests(@PathVariable String aId, @PathVariable String sId,
                        @PathVariable Boolean value) throws JsonProcessingException {
                try {
                        String result = adminService.acceptStudentRequests(aId, sId, value);
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

        @GetMapping("/admin/email-verify-check/{id}")
        public ResponseEntity<String> emailVerifyCheck(@PathVariable String id) throws JsonProcessingException {
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

        @GetMapping("/admin/forgot-password/{username}")
        public ResponseEntity<String> forgotPassword(@PathVariable String username, Model model)
                        throws JsonProcessingException {
                try {
                        Optional<Admin> admin = adminRepository.findByUsername(username);
                        if (admin.isPresent()) {
                                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                                                .body(objectMapper.writeValueAsString(
                                                                new StringResponseClass("admin-found-email-sent", true,
                                                                                admin.get().getEmail())));
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
        }

}
