package com.heart.heart.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.heart.heart.Concrete.ConfirmationToken;

public interface ConfirmationTokenRepository extends MongoRepository<ConfirmationToken, String> {

}
