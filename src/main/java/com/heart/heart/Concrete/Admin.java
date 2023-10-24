package com.heart.heart.Concrete;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
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
    private Map<String, Boolean> requests;
    private String type;
    private boolean emailVerified;

    public Admin() {
    }

    public Admin(String id, String name, String nickName, String email, String username, String password,
            String adminCode, List<String> group, Map<String, Boolean> requests, String type,
            boolean emailVerified) {
        this.id = id;
        this.name = name;
        this.nickName = nickName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.adminCode = adminCode;
        this.group = group;
        this.requests = requests;
        this.type = type;
        this.emailVerified = emailVerified;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
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
        return "Admin [id = " + id + ", name = " + name + ", nickName = " + nickName + ", email = " + email
                + ", username = " + username
                + ", password = "
                + password + ", adMinCode = " + adminCode + ", group = [" + group + "], requests = [" + requests
                + "], type = " + type
                + ", emailVerified = "
                + emailVerified + "]";
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAdminCode() {
        return adminCode;
    }

    public void setAdminCode(String adminCode) {
        this.adminCode = adminCode;
    }

    public List<String> getGroup() {
        return group;
    }

    public void setGroup(List<String> group) {
        this.group = group;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Map<String, Boolean> getRequests() {
        return requests;
    }

    public void setRequests(Map<String, Boolean> requests) {
        this.requests = requests;
    }
}
