package com.heart.heart.Concrete.Responses;

import java.util.List;

import com.heart.heart.Concrete.Admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListAdminResponseClass {
    private String message;
    private String code;
    private List<Admin> data;

}
