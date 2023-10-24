package com.heart.heart.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public String studentLogin(String... data) {
        try {
            Optional<Student> student = studentRepository.studentLogin(data[0], data[1]);
            return student.isPresent()
                    ? (checkAdminAccept(student.get().getId()) == "true" ? student.get().getId()
                            : "admin-not-accept")
                    : "null";
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

}
