package com.heart.heart.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.heart.heart.Concrete.Admin;
import com.heart.heart.Concrete.ConfirmationToken;
import com.heart.heart.Concrete.Student;
import com.heart.heart.Concrete.Urls;
import com.heart.heart.Email.EmailSender;
import com.heart.heart.Repository.AdminRepository;
import com.heart.heart.Service.AdminService;
import com.heart.heart.Service.ConfirmationTokenService;
import com.heart.heart.Service.StudentService;

@RestController
public class StudentController {

    @Autowired
    StudentService studentService;

    @Autowired
    AdminService adminService;

    @Autowired
    ConfirmationTokenService cTokenService;

    @Autowired
    EmailSender emailSender;

    @Autowired
    AdminRepository adminRepository;

    @GetMapping("/student/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable String id) {
        try {
            return new ResponseEntity<Student>(studentService.getStudent(id), HttpStatus.OK);
        } catch (Exception e) {
            throw new Error("Error in getting Student");
        }
    }

    @GetMapping("/student/student-check/{data}")
    public ResponseEntity<String> studentLogin(@PathVariable String... data) {
        try {
            return new ResponseEntity<String>(studentService.studentLogin(data), HttpStatus.OK);
        } catch (Exception e) {
            throw new Error("Error in checking the Student.");
        }
    }

    @GetMapping("/student")
    public ResponseEntity<List<Student>> getAllStudent() {
        try {
            return new ResponseEntity<List<Student>>(studentService.getAllStudents(), HttpStatus.OK);
        } catch (Exception e) {
            throw new Error("Error in getting all students");
        }
    }

    @PostMapping("/student")
    public ResponseEntity<String> addStudent(@RequestBody Student student) {
        try {
            ConfirmationToken cToken = new ConfirmationToken(student.getId(), student.getEmail(), LocalDateTime.now(),
                    LocalDateTime.from(LocalDateTime.now().plusHours(1)));
            cTokenService.addConfirmationToken(cToken);
            Optional<Admin> admin = adminRepository.findByAdminCode(student.getAdminCode());
            Map<String, Boolean> requests = admin.get().getRequests();
            requests.put(student.getId(), false);
            admin.get().setRequests(requests);
            adminService.addAdmin(admin.get());
            requestEmail(student);
            return new ResponseEntity<String>(emailValidation(student), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error in Adding", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String emailValidation(Student student) {
        return emailSender.sendEmail(student.getEmail(), "Account Verification",
                emailBody(student))
                        ? ((studentService.addStudent(student) != null) ? "Email Sent"
                                : "error-add-account")
                        : "error";
    }

    private String requestEmail(Student student) {
        Optional<Admin> admin = adminRepository.findByAdminCode(student.getAdminCode());
        return emailSender.sendEmail(admin.get().getId(), "Requesting to join your group",
                requestBody(student, admin.get()))
                        ? "email-sent"
                        : "error-email-sent";
    }

    private String emailBody(Student student) {
        return "Hi, " + student.getName() + ", \n " +
                "Welcome to the Heart❤️" +
                "\n\n" +
                "This is valid only 1 hour. After it is Expired." +
                "Click the below link to verify your account :" + Urls.STUDENT_BASE_URL + "confirm-account/"
                + student.getId();
    }

    private String requestBody(Student student, Admin admin) {
        return "Hi " + admin.getName() + ", \n " +
                "Welcome to the Heart❤️" +
                "\n\n" +
                student.getName() + " wants to join your group." +
                "Click the foolowing link to join him. or go to app notifications page to join him." +
                "Click Here : " + Urls.ADMIN_BASE_URL + "accept-student/" + admin.getId() + "/" + student.getId() + "/"
                + true;
    }

    @GetMapping("/student/resent-email/{id}")
    public ResponseEntity<String> emailReSend(@PathVariable String id) {
        try {
            Student student = studentService.getStudent(id);
            ConfirmationToken cToken = new ConfirmationToken(student.getId(), student.getEmail(), LocalDateTime.now(),
                    LocalDateTime.from(LocalDateTime.now().plusHours(1)));
            cTokenService.addConfirmationToken(cToken);
            return new ResponseEntity<String>(emailValidation(student),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error in ReSending Email.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/student/student-username-check/{username}")
    public ResponseEntity<Boolean> checkUsername(@PathVariable String username) {
        return new ResponseEntity<Boolean>((studentService.userNameCheck(username) ? true : false), HttpStatus.OK);
    }
}
