package com.heart.heart.Concrete;

public class AdminResponseClass {
    private String message;
    private String code;
    private Admin data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Admin getAdmin() {
        return data;
    }

    public void setAdmin(Admin data) {
        this.data = data;
    }

    public AdminResponseClass(String message, String code, Admin data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    @Override
    public String toString() {
        return "AdminResponseClass [message=" + message + ", code=" + code + ", data=" + data + "]";
    }

}
