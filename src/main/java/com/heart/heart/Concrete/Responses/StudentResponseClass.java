package com.heart.heart.Concrete.Responses;

import com.heart.heart.Concrete.Student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponseClass {

    private String message;
    private String code;
    private Student data;

}
