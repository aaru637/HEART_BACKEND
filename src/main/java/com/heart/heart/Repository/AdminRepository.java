package com.heart.heart.Repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.heart.heart.Concrete.Admin;

@Repository
public interface AdminRepository extends MongoRepository<Admin, String> {

    @Query("{username : {$eq : ?0}}")
    Optional<Admin> findByUsername(String username);

    @Query("{adminCode : {$eq : ?0}}")
    Optional<Admin> findByAdminCode(String adminCode);

    @Query("{$and : [{username : {$eq : ?0}}, {password : {$eq : ?1}}]}")
    Optional<Admin> adminLogin(String username, String password);

    @Query("{requests : {?0 : true}}")
    Optional<Admin> adminRequestCheck(String id);
}
