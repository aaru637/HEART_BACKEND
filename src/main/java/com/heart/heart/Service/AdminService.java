package com.heart.heart.Service;

import java.security.MessageDigest;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.heart.heart.Concrete.Admin;
import com.heart.heart.Concrete.Student;
import com.heart.heart.Repository.AdminRepository;
import com.heart.heart.Repository.StudentRepository;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private StudentRepository studentRepository;

    public Admin getAdmin(String id) {
        try {
            return adminRepository.findById(id).get();
        } catch (Exception e) {
            return new Admin();
        }
    }

    public Admin addAdmin(Admin admin) {
        try {
            if (adminRepository.existsById(admin.getId())) {
                adminRepository.deleteById(admin.getId());
            }
            return adminRepository.save(admin);
        } catch (Exception e) {
            return new Admin();
        }
    }

    public String deleteAdmin(String id) {
        try {
            adminRepository.deleteById(id);
            return "true";
        } catch (Exception e) {
            return "false";
        }
    }

    public List<Admin> getAllAdmins() {
        try {
            return adminRepository.findAll();
        } catch (Exception e) {
            return new ArrayList<Admin>();
        }
    }

    public String userNameCheck(String username) {
        try {
            return (adminRepository.findByUsername(username).isPresent()) ? "true" : "false";
        } catch (Exception e) {
            return "error";
        }
    }

    public String adminCodeCheck(String adminCode) {
        try {
            return (adminRepository.findByAdminCode(adminCode).isPresent()) ? "true" : "false";
        } catch (Exception e) {
            return "error";
        }
    }

    public String acceptStudentRequests(String aId, String sId, Boolean value) {
        try {
            Admin admin = adminRepository.findById(aId).get();
            System.out.println(value);
            Map<String, String> requests = admin.getRequests();
            requests.put(sId, Boolean.toString(value));
            Student student = studentRepository.findById(sId).get();
            student.setAdminAccept(value);
            studentRepository.save(student);
            admin.setRequests(requests);
            if (value) {
                if (!(admin.getGroup().contains(sId))) {
                    List<String> group = admin.getGroup();
                    group.add(sId);
                    admin.setGroup(group);
                } else {
                    return "user-already-enrolled";
                }
            } else {
                List<String> group = admin.getGroup();
                group.remove(sId);
                admin.setGroup(group);
            }
            return (adminRepository.save(admin) != null) ? "ok" : "not-ok";
        } catch (Exception e) {
            return "error";
        }
    }

    public String adminLogin(String... data) {
        try {
            Optional<Admin> admin = adminRepository.findByUsername(data[0]);
            if (admin.isPresent()) {
                if (attemptsCheck(data[0])) {
                    if (verifyPassword(data[1], admin.get().getPassword())) {
                        admin.get().setAttemptsLeft(3);
                        admin.get().setLocked(false);
                        admin.get().setNextAttempt(LocalDateTime.now(Clock.systemDefaultZone()));
                        addAdmin(admin.get());
                        return admin.get().getId();
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
            Admin admin = adminRepository.findByUsername(username).get();
            int attempts = admin.getAttemptsLeft();
            LocalDateTime now = LocalDateTime.now(Clock.systemDefaultZone());
            if (attempts > 0) {
                if (!admin.getLocked() && admin.getNextAttempt().until(now, ChronoUnit.HOURS) >= 1) {
                    admin.setAttemptsLeft(3);
                    admin.setNextAttempt(now);
                    admin.setLocked(false);
                    addAdmin(admin);
                }
                return true;
            } else if (admin.getLocked()) {
                if (now.isAfter(admin.getNextAttempt())) {
                    admin.setAttemptsLeft(3);
                    admin.setNextAttempt(now);
                    admin.setLocked(false);
                    addAdmin(admin);
                    return true;
                } else {
                    return false;
                }
            } else {
                admin.setLocked(true);
                admin.setNextAttempt(now.plusHours(1));
                addAdmin(admin);
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public LocalDateTime nextAttempt(String username) {
        return adminRepository.findByUsername(username).get().getNextAttempt();
    }

    public void reduceAttempts(String username) {
        Admin admin = adminRepository.findByUsername(username).get();
        int attempts = admin.getAttemptsLeft();
        attempts = (attempts > 0) ? --attempts : attempts;
        admin.setAttemptsLeft(attempts);
        addAdmin(admin);
    }

    public String encode(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public Boolean verifyPassword(String plain, String encoded) {
        return BCrypt.checkpw(plain, encoded);
    }
}
