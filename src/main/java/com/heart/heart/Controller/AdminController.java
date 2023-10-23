package com.heart.heart.Controller;

import java.time.LocalDateTime;
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
            return new ResponseEntity<AdminResponseClass>(
                    new AdminResponseClass("Admin Data Got Successfully", "success", adminService.getAdmin(id)),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<AdminResponseClass>(
                    new AdminResponseClass("Error in Getting Admin.", "failure", new Admin()),
                    HttpStatus.OK);
        }
    }

    @GetMapping("/admin/admin-check/{data}")
    public ResponseEntity<String> adminLogin(@PathVariable String... data) {
        try {
            return new ResponseEntity<String>(adminService.adminLogin(data), HttpStatus.OK);
        } catch (Exception e) {
            throw new Error("Error");
        }
    }

    @GetMapping("/admin")
    public ResponseEntity<List<Admin>> getAllAdmin() {
        return new ResponseEntity<List<Admin>>(adminService.getAllAdmins(), HttpStatus.OK);
    }

    @PostMapping("/admin")
    public ResponseEntity<String> addAdmin(@RequestBody Admin admin) {
        try {
            ConfirmationToken cToken = new ConfirmationToken(admin.getId(), admin.getEmail(), LocalDateTime.now(),
                    LocalDateTime.from(LocalDateTime.now().plusHours(1)));
            cTokenService.addConfirmationToken(cToken);
            return new ResponseEntity<String>(emailValidation(admin), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error in Adding", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/resent-email/{id}")
    public ResponseEntity<String> emailReSend(@PathVariable String id) {
        try {
            Admin admin = adminService.getAdmin(id);
            ConfirmationToken cToken = new ConfirmationToken(admin.getId(), admin.getEmail(), LocalDateTime.now(),
                    LocalDateTime.from(LocalDateTime.now().plusHours(1)));
            cTokenService.addConfirmationToken(cToken);
            return new ResponseEntity<String>(emailValidation(admin),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error in ReSending Email.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/admin-username-check/{username}")
    public ResponseEntity<Boolean> checkUsername(@PathVariable String username) {
        return new ResponseEntity<Boolean>((adminService.userNameCheck(username) ? true : false), HttpStatus.OK);
    }

    @GetMapping("/admin/admin-code-check/{adminCode}")
    public ResponseEntity<Boolean> checkAdminCode(@PathVariable String adminCode) {
        return new ResponseEntity<Boolean>(adminService.adminCodeCheck(adminCode), HttpStatus.OK);
    }

    private String emailBody(Admin admin) {
        return "Hi, " + admin.getName() + ", \n " +
                "Welcome to the Heart❤️" +
                "\n\n" +
                "This is valid only 1 hour. After it is Expired." +
                "Click the below link to verify your account :" + Urls.ADMIN_BASE_URL + "confirm-account/"
                + admin.getId();
    }

    private String emailValidation(Admin admin) {
        return emailSender.sendEmail(admin.getEmail(), "Account Verification",
                emailBody(admin))
                        ? ((adminService.addAdmin(admin) != null) ? "Email Sent"
                                : "error-add-account")
                        : "error";
    }

    @GetMapping("/admin/accept-student/{aId}/{sId}/{value}")
    public ResponseEntity<String> acceptStudentRequests(@PathVariable String aId, @PathVariable String sId,
            @PathVariable Boolean value) {
        try {
            System.out.println();
            return new ResponseEntity<String>(adminService.acceptStudentRequests(aId, sId, value), HttpStatus.OK);
        } catch (Exception e) {
            throw new Error("Error in accepting request.");
        }
    }

}
