package com.heart.heart.Concrete;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConfirmationToken {
    @Id
    private String id;
    private String email;
    private LocalDateTime tokenCreated;
    private LocalDateTime tokenExpired;

}
