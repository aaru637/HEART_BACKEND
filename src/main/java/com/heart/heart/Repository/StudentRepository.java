package com.heart.heart.Repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.heart.heart.Concrete.Student;

public interface StudentRepository extends MongoRepository<Student, String> {

    @Query("{username : {$eq : ?0}}")
    Optional<Student> findByUsername(String username);

    @Query("{$and : [{username : {$eq : ?0}}, {password : {$eq : ?1}}]}")
    Optional<Student> studentLogin(String username, String password);

    @Query("{$and : [{id : {$eq : ?0}}, {adminAccept : {$eq : true}}]}")
    Optional<Student> adminAcceptCheck(String id);
}
