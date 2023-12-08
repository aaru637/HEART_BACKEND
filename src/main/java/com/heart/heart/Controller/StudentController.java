package com.heart.heart.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heart.heart.Concrete.Admin;
import com.heart.heart.Concrete.ConfirmationToken;
import com.heart.heart.Concrete.Responses.ListStudentResponseClass;
import com.heart.heart.Concrete.Responses.StringResponseClass;
import com.heart.heart.Concrete.Student;
import com.heart.heart.Concrete.Responses.StudentResponseClass;
import com.heart.heart.Concrete.Urls;
import com.heart.heart.Email.EmailSender;
import com.heart.heart.Repository.AdminRepository;
import com.heart.heart.Service.AdminService;
import com.heart.heart.Service.ConfirmationTokenService;
import com.heart.heart.Service.StudentService;

@RestController
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private ConfirmationTokenService cTokenService;

    @Autowired
    private EmailSender emailSender;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/student/{id}")
    public ResponseEntity<String> getStudent(@PathVariable String id) throws JsonProcessingException {
        try {
            Student student = studentService.getStudent(id);
            if (student.getId() == null) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(objectMapper
                        .writeValueAsString(new StudentResponseClass("student-not-found", true, new Student())));
            } else {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(objectMapper
                        .writeValueAsString(new StudentResponseClass("student-found", true, student)));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON).body(objectMapper
                    .writeValueAsString(new StudentResponseClass("failure", false, new Student())));
        }
    }

    @GetMapping("/student/login/{data}")
    public ResponseEntity<String> studentLogin(@PathVariable String... data) throws JsonProcessingException {
        try {
            String result = studentService.studentLogin(data);
            if (result.equals("admin-not-accept")) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(objectMapper
                        .writeValueAsString(new StringResponseClass("admin-not-accept", true, result)));
            } else if (result.equals("null")) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(objectMapper
                        .writeValueAsString(new StringResponseClass("student-not-found", true, result)));
            } else {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(objectMapper
                        .writeValueAsString(new StringResponseClass("admin-accept-student-found", true, result)));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON).body(objectMapper
                            .writeValueAsString(new StringResponseClass("failure", true, "error")));
        }
    }

    @GetMapping("/student")
    public ResponseEntity<String> getAllStudent() throws JsonProcessingException {
        try {
            List<Student> students = studentService.getAllStudents();
            if (students.size() == 0) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(objectMapper
                        .writeValueAsString(new ListStudentResponseClass("students-not-found", true, students)));
            } else {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(objectMapper
                        .writeValueAsString(new ListStudentResponseClass("students-found", true, students)));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON).body(objectMapper
                    .writeValueAsString(new ListStudentResponseClass("failure", false, new ArrayList<Student>())));
        }
    }

    @PostMapping("/student")
    public ResponseEntity<String> addStudent(@RequestBody Student student) throws JsonProcessingException {
        try {
            student.setPassword(studentService.encode(student.getPassword()));
            addToken(student);
            Optional<Admin> admin = adminRepository.findByAdminCode(student.getAdminCode());
            if (admin.isPresent()) {
                Map<String, String> requests = admin.get().getRequests();
                requests.put(student.getId(), "false");
                admin.get().setRequests(requests);
                adminService.addAdmin(admin.get());
                requestEmail(student);
                String result = emailValidation(student);
                if (result.equals("email-sent")) {
                    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(objectMapper
                            .writeValueAsString(new StringResponseClass("email-sent", true, result)));
                } else if (result.equals("email-sent-error")) {
                    studentService.deleteStudent(student.getId());
                    cTokenService.deleteConfirmationToken(student.getId());
                    return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON)
                            .body(objectMapper
                                    .writeValueAsString(new StringResponseClass("email-sent-error", false, result)));
                } else {
                    cTokenService.deleteConfirmationToken(student.getId());
                    return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON)
                            .body(objectMapper
                                    .writeValueAsString(new StringResponseClass("add-student-error", false, result)));
                }
            } else {
                cTokenService.deleteConfirmationToken(student.getId());
                return ResponseEntity.status(404).contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper
                                .writeValueAsString(new StringResponseClass("failure", false, "admin-not-found")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper
                            .writeValueAsString(new StringResponseClass("failure", false, "error")));
        }
    }

    @GetMapping("/student/resent-email/{id}")
    public ResponseEntity<String> emailReSend(@PathVariable String id) throws JsonProcessingException {
        try {
            Student student = studentService.getStudent(id);
            addToken(student);
            return addStudent(student);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper
                            .writeValueAsString(new StringResponseClass("failure", false, "error")));
        }
    }

    @GetMapping("/student/student-username-check/{username}")
    public ResponseEntity<String> checkUsername(@PathVariable String username) throws JsonProcessingException {
        try {
            String result = studentService.userNameCheck(username);
            if (result.equals("true")) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper
                                .writeValueAsString(new StringResponseClass("student-found", true, result)));
            } else if (result.equals("false")) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper
                                .writeValueAsString(new StringResponseClass("student-not-found", true, result)));
            } else {
                return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper
                                .writeValueAsString(new StringResponseClass("failure", false, result)));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper
                            .writeValueAsString(new StringResponseClass("failure", false, "error")));
        }
    }

    @GetMapping("/student/email-verify-check/{id}")
    public ResponseEntity<String> emailVerifyCheck(@PathVariable String id) throws JsonProcessingException {
        try {
            Student student = studentService.getStudent(id);
            if (student.getId() == null) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper
                                .writeValueAsString(new StringResponseClass("UnAuthorized User.", true, "false")));
            }
            Boolean result = student.getEmailVerified();
            if (result) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper
                                .writeValueAsString(new StringResponseClass("email-verified", true, "true")));
            } else {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper
                                .writeValueAsString(new StringResponseClass("email-not-verified", true, "false")));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper
                            .writeValueAsString(new StringResponseClass("failure", false, "error")));
        }
    }

    @GetMapping("/student/resent-request-email/{sId}")
    public ResponseEntity<String> resentRequestEmail(@PathVariable String sId) throws JsonProcessingException {
        try {
            Student student = studentService.getStudent(sId);
            requestEmail(student);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper
                            .writeValueAsString(new StringResponseClass("email-sent", true, "email-sent")));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper
                            .writeValueAsString(new StringResponseClass("failure", false, "error")));
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
