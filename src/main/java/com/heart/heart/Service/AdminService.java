package com.heart.heart.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            throw new Error("No Admin Found");
        }
    }

    public Admin addAdmin(Admin admin) {
        try {
            if (adminRepository.existsById(admin.getId())) {
                adminRepository.deleteById(admin.getId());
            }
            return adminRepository.save(admin);
        } catch (Exception e) {
            throw new Error("Error in adding Admin");
        }
    }

    public List<Admin> getAllAdmins() {
        try {
            return adminRepository.findAll();
        } catch (Exception e) {
            throw new Error("Error in Getting All Admins.");
        }
    }

    public boolean userNameCheck(String username) {
        try {
            return (adminRepository.findByUsername(username).isPresent()) ? true : false;
        } catch (Exception e) {
            throw new Error("Error while checking username");
        }
    }

    public Boolean adminCodeCheck(String adminCode) {
        try {
            return (adminRepository.findByAdminCode(adminCode).isPresent()) ? true : false;
        } catch (Exception e) {
            throw new Error("Error while checking admin code");
        }
    }

    public String adminLogin(String... data) {
        try {
            Optional<Admin> admin = adminRepository.adminLogin(data[0], data[1]);
            return admin.isPresent() ? admin.get().getId() : "null";
        } catch (Exception e) {
            throw new Error("Error While Checking admin");
        }
    }

    public String acceptStudentRequests(String aId, String sId, Boolean value) {
        try {
            Admin admin = adminRepository.findById(aId).get();
            if (value) {
                if (!admin.getGroup().contains(sId)) {
                    admin.getGroup().add(sId);
                } else {
                    return "user-already-enrolled";
                }
            } else {
                admin.getGroup().remove(sId);
            }
            Map<String, Boolean> requests = admin.getRequests();
            requests.put(sId, value);
            Student student = studentRepository.findById(sId).get();
            student.setAdminAccept(value);
            studentRepository.save(student);
            admin.setRequests(requests);
            return (adminRepository.save(admin) != null) ? "OK" : "NOT OK";
        } catch (Exception e) {
            throw new Error("Error accepting requests.");
        }
    }
}
