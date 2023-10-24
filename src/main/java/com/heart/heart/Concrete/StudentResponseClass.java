package com.heart.heart.Concrete;

public class StudentResponseClass {

    private String message;
    private String code;
    private Student data;

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

    public Student getData() {
        return data;
    }

    public void setData(Student data) {
        this.data = data;
    }

    public StudentResponseClass(String message, String code, Student data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    @Override
    public String toString() {
        return "StudentResponseClass [message=" + message + ", code=" + code + ", data=" + data + "]";
    }

}
