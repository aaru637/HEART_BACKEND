package com.heart.heart.Concrete.Responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StringResponseClass {
    private String message;
    private String code;
    private String data;

}
