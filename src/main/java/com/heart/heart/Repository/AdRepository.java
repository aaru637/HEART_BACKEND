package com.heart.heart.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import com.heart.heart.Concrete.Admin;

@Repository
public interface AdRepository extends MongoRepository<Admin, String> {

    @Update("{_id : ?0}, {$set : {requests : {?1 : ?2}}}")
    Integer acceptStudentRequest(String aId, String sId, Boolean value);
}