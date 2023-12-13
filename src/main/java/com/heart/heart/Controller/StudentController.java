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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heart.heart.Concrete.Admin;
import com.heart.heart.Concrete.ConfirmationToken;
import com.heart.heart.Concrete.Password;
import com.heart.heart.Concrete.Responses.ListStudentResponseClass;
import com.heart.heart.Concrete.Responses.StringResponseClass;
import com.heart.heart.Concrete.Student;
import com.heart.heart.Concrete.StudentRequest;
import com.heart.heart.Concrete.Responses.StudentResponseClass;
import com.heart.heart.Email.EmailSender;
import com.heart.heart.Repository.AdminRepository;
import com.heart.heart.Repository.StudentRepository;
import com.heart.heart.Service.AdminService;
import com.heart.heart.Service.ConfirmationTokenService;
import com.heart.heart.Service.StudentService;

import jakarta.mail.MessagingException;

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
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/api/student/")
    public ResponseEntity<String> getStudent(@RequestParam String id) throws JsonProcessingException {
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

    @GetMapping("/api/student/login/")
    public ResponseEntity<String> studentLogin(@RequestParam String username, @RequestParam String password)
            throws JsonProcessingException {
        try {
            String result = studentService.studentLogin(new String[] { username, password });
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

    @GetMapping("/api/student")
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

    @PostMapping("/api/student")
    public ResponseEntity<String> addStudent(@RequestBody Student student) throws JsonProcessingException {
        try {
            student.setPassword(studentService.encode(student.getPassword()));
            addToken(student);
            Optional<Admin> admin = adminRepository.findByAdminCode(student.getAdminCode());
            if (admin.isPresent()) {
                StudentRequest studentRequest = new StudentRequest(student.getId(), student.getName(), false,
                        LocalDateTime.now(Clock.systemDefaultZone()));
                List<StudentRequest> requests = admin.get().getRequests();
                requests.add(studentRequest);
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

    @GetMapping("/student/resent-email/")
    public ModelAndView emailResend(@RequestParam String id) {
        ModelAndView modelAndView = new ModelAndView("email-sent");
        try {
            Student student = studentService.getStudent(id);
            if (studentService.getStudent(student.getId()) == null) {
                student.setPassword(adminService.encode(student.getPassword()));
            }
            addToken(student);
            String result = emailValidation(student);
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
                student.setEmailVerified(false);
                studentService.addStudent(student);
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

    @GetMapping("/api/student/resent-email/")
    public ResponseEntity<String> emailResendAPI(@RequestParam String id) throws JsonProcessingException {
        try {
            Student student = studentService.getStudent(id);
            if (studentService.getStudent(student.getId()) == null) {
                student.setPassword(adminService.encode(student.getPassword()));
            }
            addToken(student);
            String result = emailValidation(student);
            if (result.equals("email-sent")) {
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper
                                .writeValueAsString(
                                        new StringResponseClass("email-sent",
                                                true, result)));
            } else if (result.equals("email-sent-error")) {
                student.setEmailVerified(false);
                studentService.addStudent(student);
                cTokenService.deleteConfirmationToken(id);
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper
                                .writeValueAsString(
                                        new StringResponseClass(
                                                "email-sent-error",
                                                false, result)));
            } else {
                cTokenService.deleteConfirmationToken(id);
                return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper
                                .writeValueAsString(
                                        new StringResponseClass("error",
                                                false, result)));
            }
        } catch (Exception e) {
            cTokenService.deleteConfirmationToken(id);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(objectMapper
                            .writeValueAsString(
                                    new StringResponseClass("error",
                                            false, "error-occured")));
        }
    }

    @GetMapping("/api/student/username-check/")
    public ResponseEntity<String> checkUsername(@RequestParam String username) throws JsonProcessingException {
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

    @GetMapping("/api/student/email-verify-check/")
    public ResponseEntity<String> emailVerifyCheck(@RequestParam String id) throws JsonProcessingException {
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

    @GetMapping("/api/student/resent-request-email/")
    public ResponseEntity<String> resentRequestEmail(@RequestParam String id) throws JsonProcessingException {
        try {
            Student student = studentService.getStudent(id);
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

    @GetMapping("/api/student/forgot-password/")
    public ResponseEntity<String> forgotPassword(@RequestParam String username)
            throws JsonProcessingException {
        try {
            Optional<Student> student = studentRepository.findByUsername(username);
            if (student.isPresent()) {
                if (sendForgotPasswordEmail(student.get().getEmail(), student.get().getId())) {
                    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                            .body(objectMapper.writeValueAsString(
                                    new StringResponseClass(
                                            "student-found-email-sent", true,
                                            student.get().getEmail())));
                } else {
                    return ResponseEntity.internalServerError()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(objectMapper.writeValueAsString(new StringResponseClass(
                                    "failure", false, "error")));
                }

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

    @GetMapping("/api/student/reset-password/")
    public ModelAndView resetPassword(@RequestParam String id) {
        ModelAndView modelAndView = new ModelAndView("reset-password");
        modelAndView.addAllObjects(new HashMap<>() {
            {
                put("username", studentService.getStudent(id).getUsername());
                put("id", id);
                put("password", new Password());
                put("role", "student");
            }
        });
        return modelAndView;
    }

    @PostMapping("/student/update-password")
    public ModelAndView updatePassword(@ModelAttribute Password password) {
        ModelAndView modelAndView = new ModelAndView("password-updated");
        Student student = studentService.getStudent(password.getId());
        student.setPassword(studentService.encode(password.getPassword()));
        return modelAndView;
    }

    // Methods for used above to reduce code and to understand code.

    private String emailValidation(Student student) {
        return emailSender.sendConfirmationHTMLEmail(student.getEmail(), "Account Confirmation", "student",
                student.getId(), student.getName())
                        ? ((studentService.addStudent(student) != null) ? "email-sent"
                                : "add-student-error")
                        : "email-sent-error";
    }

    private void requestEmail(Student student) {
        Optional<Admin> admin = adminRepository.findByAdminCode(student.getAdminCode());
        emailSender.sendRequestHTMLEmail(admin.get().getEmail(), "Request To Join Group", admin.get().getId(),
                student.getId(), admin.get().getName(), student.getName());
    }

    private void addToken(Student student) {
        ConfirmationToken cToken = new ConfirmationToken(student.getId(), student.getEmail(), LocalDateTime.now(),
                LocalDateTime.from(LocalDateTime.now().plusHours(1)));
        cTokenService.addConfirmationToken(cToken);
    }

    private boolean sendForgotPasswordEmail(String mail, String id)
            throws MessagingException, IOException {
        return emailSender.sendResetPasswordHTMLEmail(mail, "Reset Password", id, "student");
    }

}