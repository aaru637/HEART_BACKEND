package com.heart.heart.Concrete;

import java.util.List;

public class ListAdminResponseClass {
    private String message;
    private String code;
    private List<Admin> data;

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

    public List<Admin> getData() {
        return data;
    }

    public void setData(List<Admin> data) {
        this.data = data;
    }

    public ListAdminResponseClass(String message, String code, List<Admin> data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    @Override
    public String toString() {
        return "ListAdminResponseClass [message=" + message + ", code=" + code + ", data=" + data + "]";
    }

}
