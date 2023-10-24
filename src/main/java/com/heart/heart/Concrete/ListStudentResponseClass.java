package com.heart.heart.Concrete;

import java.util.List;

public class ListStudentResponseClass {

    private String message;
    private String code;
    private List<Student> data;

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

    public List<Student> getData() {
        return data;
    }

    public void setData(List<Student> data) {
        this.data = data;
    }

    public ListStudentResponseClass(String message, String code, List<Student> data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    @Override
    public String toString() {
        return "ListStudentResponseClass [message=" + message + ", code=" + code + ", data=" + data + "]";
    }
}
