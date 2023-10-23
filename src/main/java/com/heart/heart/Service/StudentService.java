package com.heart.heart.Service;

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
            throw new Error("No Student Found");
        }
    }

    public Student addStudent(Student student) {
        try {
            if (studentRepository.existsById(student.getId())) {
                studentRepository.deleteById(student.getId());
            }
            return studentRepository.save(student);
        } catch (Exception e) {
            throw new Error("Error Adding Student");
        }
    }

    public List<Student> getAllStudents() {
        try {
            return studentRepository.findAll();
        } catch (Exception e) {
            throw new Error("Error in Getting all Students.");
        }
    }

    public Boolean userNameCheck(String username) {
        try {
            return (studentRepository.findByUsername(username).isPresent()) ? true : false;
        } catch (Exception e) {
            throw new Error("Error while checking username");
        }
    }

    public String studentLogin(String... data) {
        try {
            Optional<Student> student = studentRepository.studentLogin(data[0], data[1]);
            return student.isPresent()
                    ? (adminRepository.adminRequestCheck(student.get().getId()).isPresent() ? student.get().getId()
                            : "admin-not-accept")
                    : "null";
        } catch (Exception e) {
            throw new Error("Error in Checking Student.");
        }
    }

    public Boolean checkAdminAccept(String id) {
        try {
            return studentRepository.adminAcceptCheck(id).isPresent() ? true : false;
        } catch (Exception e) {
            throw new Error("Error in Checking admin accept status.");
        }
    }

}
