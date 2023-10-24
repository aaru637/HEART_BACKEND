package com.heart.heart.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.heart.heart.Concrete.ConfirmationToken;
import com.heart.heart.Repository.ConfirmationTokenRepository;

@Service
public class ConfirmationTokenService {
    @Autowired
    ConfirmationTokenRepository cTokenRepository;

    public ConfirmationToken getConfirmationToken(String id) {
        try {
            return cTokenRepository.findById(id).get();
        } catch (Exception e) {
            return new ConfirmationToken();
        }
    }

    public ConfirmationToken addConfirmationToken(ConfirmationToken cToken) {
        try {
            if (cTokenRepository.existsById(cToken.getid())) {
                cTokenRepository.deleteById(cToken.getid());
            }
            return cTokenRepository.save(cToken);
        } catch (Exception e) {
            return new ConfirmationToken();
        }
    }
}
