package com.heart.heart.Concrete;

public class AdminResponseClass {
    private String message;
    private String code;
    private Admin data;

    public AdminResponseClass(String message, String code, Admin data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

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

    @Override
    public String toString() {
        return "AdminResponseClass [message=" + message + ", code=" + code + ", data=" + data + "]";
    }

    public Admin getData() {
        return data;
    }

    public void setData(Admin data) {
        this.data = data;
    }

}
