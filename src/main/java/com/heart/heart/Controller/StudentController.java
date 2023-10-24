package com.heart.heart.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import com.heart.heart.Concrete.ListStudentResponseClass;
import com.heart.heart.Concrete.StringResponseClass;
import com.heart.heart.Concrete.Student;
import com.heart.heart.Concrete.StudentResponseClass;
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
    public ResponseEntity<StudentResponseClass> getStudent(@PathVariable String id) {
        try {
            Student student = studentService.getStudent(id);
            if (student.getId() == null) {
                return new ResponseEntity<StudentResponseClass>(
                        new StudentResponseClass("student-not-found", "success", new Student()), HttpStatus.OK);
            } else {
                return new ResponseEntity<StudentResponseClass>(
                        new StudentResponseClass("student-found", "success", student), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<StudentResponseClass>(
                    new StudentResponseClass("error", "failure", new Student()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/student/student-check/{data}")
    public ResponseEntity<StringResponseClass> studentLogin(@PathVariable String... data) {
        try {
            String result = studentService.studentLogin(data);
            if (result.equals("admin-not-accept")) {
                return new ResponseEntity<StringResponseClass>(
                        new StringResponseClass("admin-not-accept", "success", result), HttpStatus.OK);
            } else if (result.equals("null")) {
                return new ResponseEntity<StringResponseClass>(
                        new StringResponseClass("student-not-found", "success", "student-not-found"), HttpStatus.OK);
            } else {
                return new ResponseEntity<StringResponseClass>(
                        new StringResponseClass("admin-accept-student-found", "success", result), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<StringResponseClass>(
                    new StringResponseClass("error", "failure", "error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/student")
    public ResponseEntity<ListStudentResponseClass> getAllStudent() {
        try {
            List<Student> students = studentService.getAllStudents();
            if (students.size() == 0) {
                return new ResponseEntity<ListStudentResponseClass>(
                        new ListStudentResponseClass("students-not-found", "success", students), HttpStatus.OK);
            } else {
                return new ResponseEntity<ListStudentResponseClass>(
                        new ListStudentResponseClass("students-found", "success", students), HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<ListStudentResponseClass>(
                    new ListStudentResponseClass("error", "failure", new ArrayList<Student>()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/student")
    public ResponseEntity<StringResponseClass> addStudent(@RequestBody Student student) {
        try {
            addToken(student);
            Optional<Admin> admin = adminRepository.findByAdminCode(student.getAdminCode());
            Map<String, Boolean> requests = admin.get().getRequests();
            requests.put(student.getId(), false);
            admin.get().setRequests(requests);
            adminService.addAdmin(admin.get());
            requestEmail(student);
            String result = emailValidation(student);
            if (result.equals("email-sent")) {
                return new ResponseEntity<StringResponseClass>(new StringResponseClass("email-sent", "success", result),
                        HttpStatus.OK);
            } else if (result.equals("email-sent-error")) {
                studentService.deleteStudent(student.getId());
                return new ResponseEntity<StringResponseClass>(
                        new StringResponseClass("email-sent-error", "failure", result),
                        HttpStatus.SERVICE_UNAVAILABLE);
            } else {
                return new ResponseEntity<StringResponseClass>(
                        new StringResponseClass("add-admin-error", "failure", result),
                        HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<StringResponseClass>(
                    new StringResponseClass("error", "failure", "error"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/student/resent-email/{id}")
    public ResponseEntity<StringResponseClass> emailReSend(@PathVariable String id) {
        try {
            Student student = studentService.getStudent(id);
            addToken(student);
            return addStudent(student);
        } catch (Exception e) {
            return new ResponseEntity<StringResponseClass>(
                    new StringResponseClass("error", "failure", "error"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/student/student-username-check/{username}")
    public ResponseEntity<StringResponseClass> checkUsername(@PathVariable String username) {
        try {
            String result = studentService.userNameCheck(username);
            if (result.equals("true")) {
                return new ResponseEntity<StringResponseClass>(
                        new StringResponseClass("student-found", "success", result),
                        HttpStatus.OK);
            } else if (result.equals("false")) {
                return new ResponseEntity<StringResponseClass>(
                        new StringResponseClass("student-not-found", "success", result),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<StringResponseClass>(new StringResponseClass("error", "failure", result),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<StringResponseClass>(new StringResponseClass("error", "failure", "error"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String emailValidation(Student student) {
        return emailSender.sendEmail(student.getEmail(), "Account Verification",
                emailBody(student))
                        ? ((studentService.addStudent(student) != null) ? "email-sent"
                                : "add-student-error")
                        : "email-sent-error";
    }

    private void requestEmail(Student student) {
        Optional<Admin> admin = adminRepository.findByAdminCode(student.getAdminCode());
        emailSender.sendEmail(admin.get().getEmail(), "Requesting to join your group",
                requestBody(student, admin.get()));
    }

    private String emailBody(Student student) {
        return "Hi, " + student.getName() + ", \n " +
                "Welcome to the Heart❤️" +
                "\n\n" +
                "This is valid only 1 hour." +
                "Click the below link to verify your account :" + Urls.STUDENT_BASE_URL + "confirm-account/"
                + student.getId();
    }

    private String requestBody(Student student, Admin admin) {
        return "Hi " + admin.getName() + ", \n " +
                "Welcome to the Heart❤️" +
                "\n\n" +
                student.getName() + " wants to join your group." +
                "Click the folowing link to join him/her or go to app notifications page to join him/her." +
                "Click Here : " + Urls.ADMIN_BASE_URL + "accept-student/" + admin.getId() + "/" + student.getId() + "/"
                + true;
    }

    private void addToken(Student student) {
        ConfirmationToken cToken = new ConfirmationToken(student.getId(), student.getEmail(), LocalDateTime.now(),
                LocalDateTime.from(LocalDateTime.now().plusHours(1)));
        cTokenService.addConfirmationToken(cToken);
    }
}
