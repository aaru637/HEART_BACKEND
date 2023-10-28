package com.heart.heart.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.heart.heart.Concrete.Admin;
import com.heart.heart.Concrete.AdminResponseClass;
import com.heart.heart.Concrete.ConfirmationToken;
import com.heart.heart.Concrete.ListAdminResponseClass;
import com.heart.heart.Concrete.StringResponseClass;
import com.heart.heart.Concrete.Urls;
import com.heart.heart.Email.EmailSender;
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

    @GetMapping("/")
    public String hello() {
        return "Welcome to Deadlock";
    }

    @GetMapping("/admin/{id}")
    public ResponseEntity<AdminResponseClass> getAdmin(@PathVariable String id) {
        try {
            Admin admin = adminService.getAdmin(id);
            if (admin.getId() == null) {
                return new ResponseEntity<AdminResponseClass>(
                        new AdminResponseClass("admin-not-found", "success", new Admin()),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<AdminResponseClass>(
                        new AdminResponseClass("admin-found", "success", admin),
                        HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<AdminResponseClass>(
                    new AdminResponseClass("error", "failure", new Admin()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/admin-check/{data}")
    public ResponseEntity<StringResponseClass> adminLogin(@PathVariable String... data) {
        try {
            String result = adminService.adminLogin(data);
            if (!result.equals("null")) {
                return new ResponseEntity<StringResponseClass>(
                        new StringResponseClass("admin-found", "success", result),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<StringResponseClass>(
                        new StringResponseClass("admin-not-found", "success", result),
                        HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<StringResponseClass>(
                    new StringResponseClass("error", "failure", "error"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin")
    public ResponseEntity<ListAdminResponseClass> getAllAdmin() {
        try {
            List<Admin> admins = adminService.getAllAdmins();
            if (admins.size() != 0) {
                return new ResponseEntity<ListAdminResponseClass>(new ListAdminResponseClass(
                        "admins-found", "success", admins), HttpStatus.OK);
            } else {
                return new ResponseEntity<ListAdminResponseClass>(new ListAdminResponseClass(
                        "admins-not-found", "success", admins), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<ListAdminResponseClass>(new ListAdminResponseClass(
                    "error", "failure", new ArrayList<Admin>()), HttpStatus.OK);
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<StringResponseClass> addAdmin(@RequestBody Admin admin) {
        try {
            addToken(admin);
            String result = emailValidation(admin);
            if (result.equals("email-sent")) {
                return new ResponseEntity<StringResponseClass>(new StringResponseClass("email-sent", "success", result),
                        HttpStatus.OK);
            } else if (result.equals("email-sent-error")) {
                adminService.deleteAdmin(admin.getId());
                return new ResponseEntity<StringResponseClass>(
                        new StringResponseClass("email-sent-error", "failure", result),
                        HttpStatus.SERVICE_UNAVAILABLE);
            } else {
                return new ResponseEntity<StringResponseClass>(
                        new StringResponseClass("add-admin-error", "failure", result),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<StringResponseClass>(new StringResponseClass("error", "failure", "error"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/resent-email/{id}")
    public ResponseEntity<StringResponseClass> emailReSend(@PathVariable String id) {
        try {
            Admin admin = adminService.getAdmin(id);
            return addAdmin(admin);
        } catch (Exception e) {
            return new ResponseEntity<StringResponseClass>(new StringResponseClass("error", "failure", "error"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/admin-username-check/{username}")
    public ResponseEntity<StringResponseClass> checkUsername(@PathVariable String username) {
        try {
            String result = adminService.userNameCheck(username);
            return adminUserAndCodeCheck(result);
        } catch (Exception e) {
            return new ResponseEntity<StringResponseClass>(new StringResponseClass("error", "failure", "error"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/admin-code-check/{adminCode}")
    public ResponseEntity<StringResponseClass> checkAdminCode(@PathVariable String adminCode) {
        try {
            String result = adminService.adminCodeCheck(adminCode);
            return adminUserAndCodeCheck(result);
        } catch (Exception e) {
            return new ResponseEntity<StringResponseClass>(new StringResponseClass("error", "failure", "error"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/accept-student/{aId}/{sId}/{value}")
    public ResponseEntity<StringResponseClass> acceptStudentRequests(@PathVariable String aId, @PathVariable String sId,
            @PathVariable String value) {
        try {
            String result = adminService.acceptStudentRequests(aId, sId, value);
            if (result.equals("ok")) {
                return new ResponseEntity<StringResponseClass>(
                        new StringResponseClass("student-accepted", "success", result), HttpStatus.OK);
            } else if (result.equals("user-already-enrolled")) {
                return new ResponseEntity<StringResponseClass>(
                        new StringResponseClass("user-already-enrolled", "success", result), HttpStatus.OK);
            } else {
                return new ResponseEntity<StringResponseClass>(
                        new StringResponseClass("student-not-accepted", "failure", result),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<StringResponseClass>(
                    new StringResponseClass("student-not-accepted", "failure", "error"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/email-verify-check/{id}")
    public ResponseEntity<StringResponseClass> emailVerifyCheck(@PathVariable String id) {
        try {
            Boolean result = adminService.getAdmin(id).getEmailVerified();
            if (result) {
                return new ResponseEntity<StringResponseClass>(
                        new StringResponseClass("email-verified", "success", "true"), HttpStatus.OK);
            } else {
                return new ResponseEntity<StringResponseClass>(
                        new StringResponseClass("email-not-verified", "success", "false"), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<StringResponseClass>(
                    new StringResponseClass("error", "failure", "error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String emailBody(Admin admin) {
        return "Hi, " + admin.getName() + ", \n " +
                "Welcome to the Heart❤️" +
                "\n\n" +
                "This is valid only 1 hour." +
                "Click the below link to verify your account :" + Urls.ADMIN_BASE_URL + "confirm-account/"
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
                LocalDateTime.from(LocalDateTime.now().plusHours(1)));
        cTokenService.addConfirmationToken(cToken);
    }

    private ResponseEntity<StringResponseClass> adminUserAndCodeCheck(String result) {
        if (result.equals("true")) {
            return new ResponseEntity<StringResponseClass>(
                    new StringResponseClass("admin-found", "success", result),
                    HttpStatus.OK);
        } else if (result.equals("false")) {
            return new ResponseEntity<StringResponseClass>(
                    new StringResponseClass("admin-not-found", "success", result),
                    HttpStatus.OK);
        } else {
            return new ResponseEntity<StringResponseClass>(new StringResponseClass("error", "failure", result),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
