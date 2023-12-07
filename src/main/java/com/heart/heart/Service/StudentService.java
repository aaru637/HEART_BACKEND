package com.heart.heart.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.heart.heart.Concrete.Admin;
import com.heart.heart.Concrete.Student;
import com.heart.heart.Repository.AdminRepository;
import com.heart.heart.Repository.StudentRepository;

@Service
public class StudentService {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    AdminRepository adminRepository;

    public Student getStudent(String id) {
        try {
            return studentRepository.findById(id).get();
        } catch (Exception e) {
            return new Student();
        }
    }

    public Student addStudent(Student student) {
        try {
            if (studentRepository.existsById(student.getId())) {
                studentRepository.deleteById(student.getId());
            }
            return studentRepository.save(student);
        } catch (Exception e) {
            return new Student();
        }
    }

    public String deleteStudent(String id) {
        try {
            studentRepository.deleteById(id);
            return "true";
        } catch (Exception e) {
            return "false";
        }
    }

    public List<Student> getAllStudents() {
        try {
            return studentRepository.findAll();
        } catch (Exception e) {
            return new ArrayList<Student>();
        }
    }

    public String userNameCheck(String username) {
        try {
            return (studentRepository.findByUsername(username).isPresent()) ? "true" : "false";
        } catch (Exception e) {
            return "error";
        }
    }

    public String checkAdminAccept(String id) {
        try {
            return studentRepository.adminAcceptCheck(id).isPresent() ? "true" : "false";
        } catch (Exception e) {
            return "error";
        }
    }

    public String studentLogin(String... data) {
        try {
            Optional<Student> student = studentRepository.findByUsername(data[0]);
            if (student.isPresent()) {
                if (attemptsCheck(data[0])) {
                    if (verifyPassword(data[1], student.get().getPassword())) {
                        student.get().setAttemptsLeft(3);
                        student.get().setLocked(false);
                        student.get().setNextAttempt(LocalDateTime.now(Clock.systemDefaultZone()));
                        addStudent(student.get());
                        return (checkAdminAccept(student.get().getId()) == "true" ? student.get().getId()
                                : "admin-not-accept");
                    } else {
                        reduceAttempts(data[0]);
                        return "null";
                    }
                } else {
                    return new String("attempts " + nextAttempt(data[0]).toString());
                }
            }
            return "null";
        } catch (Exception e) {
            return "error";
        }
    }

    public Boolean attemptsCheck(String username) {
        try {
            Student student = studentRepository.findByUsername(username).get();
            int attempts = student.getAttemptsLeft();
            LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());
            if (attempts > 0) {
                if (!student.getLocked() && student.getNextAttempt().until(now, ChronoUnit.HOURS) >= 1) {
                    student.setAttemptsLeft(3);
                    student.setNextAttempt(now);
                    student.setLocked(false);
                    addStudent(student);
                }
                return true;
            } else if (student.getLocked()) {
                if (now.isAfter(student.getNextAttempt())) {
                    student.setAttemptsLeft(3);
                    student.setNextAttempt(now);
                    student.setLocked(false);
                    addStudent(student);
                    return true;
                } else {
                    return false;
                }
            } else {
                student.setLocked(true);
                student.setNextAttempt(now.plusHours(1));
                addStudent(student);
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public LocalDateTime nextAttempt(String username) {
        return studentRepository.findByUsername(username).get().getNextAttempt();
    }

    public void reduceAttempts(String username) {
        Student student = studentRepository.findByUsername(username).get();
        int attempts = student.getAttemptsLeft();
        attempts = (attempts > 0) ? --attempts : attempts;
        student.setAttemptsLeft(attempts);
        addStudent(student);
    }

    public String encode(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public Boolean verifyPassword(String plain, String encoded) {
        return BCrypt.checkpw(plain, encoded);
    }
}
