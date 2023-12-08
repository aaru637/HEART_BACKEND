package com.heart.heart.Concrete.Responses;

import com.heart.heart.Concrete.Admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponseClass {
    private String message;
    private Boolean success;
    private Admin data;

}
