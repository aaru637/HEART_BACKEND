package com.heart.heart.Concrete.Responses;

import com.heart.heart.Concrete.Student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListStudentResponseClass {

    private String message;
    private Boolean success;
    private List<Student> data;

}
