package com.heart.heart.Concrete;

public class StringResponseClass {
    private String message;
    private String code;
    private String data;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public StringResponseClass(String message, String code, String data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    @Override
    public String toString() {
        return "StringResponseClass [message=" + message + ", code=" + code + ", data=" + data + "]";
    }

}
