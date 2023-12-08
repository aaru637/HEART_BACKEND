package com.heart.heart.Concrete;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentRequest {
    private String id;
    private String name;
    private Boolean accepted;
    private LocalDateTime joinedDate;
}
