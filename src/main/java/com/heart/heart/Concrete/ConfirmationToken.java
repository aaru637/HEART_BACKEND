package com.heart.heart.Concrete;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ConfirmationToken {
    @Id
    private String id;
    private String email;
    private LocalDateTime tokenCreated;
    private LocalDateTime tokenExpired;

    public ConfirmationToken(String id, String email, LocalDateTime tokenCreated, LocalDateTime tokenExpired) {
        this.id = id;
        this.email = email;
        this.tokenCreated = tokenCreated;
        this.tokenExpired = tokenExpired;
    }

    public ConfirmationToken() {
    }

    public String getid() {
        return id;
    }

    public void setid(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getTokenCreated() {
        return tokenCreated;
    }

    public void setTokenCreated(LocalDateTime tokenCreated) {
        this.tokenCreated = tokenCreated;
    }

    public LocalDateTime getTokenExpired() {
        return tokenExpired;
    }

    public void setTokenExpired(LocalDateTime tokenExpired) {
        this.tokenExpired = tokenExpired;
    }

    @Override
    public String toString() {
        return "ConfirmationToken [id = " + id + ", email = " + email + ", tokenCreated = " + tokenCreated
                + ", tokenExpired = " + tokenExpired + "]";
    }

}
