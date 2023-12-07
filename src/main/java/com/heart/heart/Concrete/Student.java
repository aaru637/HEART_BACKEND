package com.heart.heart.Concrete;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Student {
    @Id
    private String id;
    private String name;
    private String nickName;
    private String email;
    private String username;
    private String password;
    private String adminCode;
    private Boolean adminAccept;
    private String type;
    private Boolean emailVerified;
    private int attemptsLeft;
    private Boolean locked;
    private LocalDateTime nextAttempt;
}
