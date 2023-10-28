package com.heart.heart.Concrete;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Student {
    @Id
    private String id;
    private String name;
    private String nickName;
    private String email;
    private String username;
    private String password;
    private String adminCode;
    private String adminAccept;
    private String type;
    private Boolean emailVerified;

    public Student(String id, String name, String nickName, String email, String username, String password,
            String adminAccept,
            String adminCode,
            String type,
            Boolean emailVerified) {
        this.id = id;
        this.name = name;
        this.nickName = nickName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.adminCode = adminCode;
        this.adminAccept = adminAccept;
        this.type = type;
        this.emailVerified = emailVerified;
    }

    public Student() {
    }

    public String getAdminAccept() {
        return adminAccept;
    }

    public void setAdminAccept(String adminAccept) {
        this.adminAccept = adminAccept;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Student [id = " + id + ", name = " + name + ", nickName = " + nickName + ", email = " + email
                + ", username = " + username
                + ", password = "
                + password + ", adMinCode = " + adminCode + ", adminAccept = " + adminAccept + ", type = " + type
                + ", emailVerified = "
                + emailVerified + "]";
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAdminCode() {
        return adminCode;
    }

    public void setAdminCode(String adminCode) {
        this.adminCode = adminCode;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

}
