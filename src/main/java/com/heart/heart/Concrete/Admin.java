package com.heart.heart.Concrete;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Admin {
    @Id
    private String id;
    private String name;
    private String nickName;
    private String email;
    private String username;
    private String password;
    private String adminCode;
    private List<String> group;
    private Map<String, String> requests;
    private String type;
    private Boolean emailVerified;
    private int attemptsLeft;
    private Boolean locked;
    private LocalDateTime nextAttempt;
}
