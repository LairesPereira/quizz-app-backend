package com.quizz.app.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserRegisterDTO {
    private String firstName;
    private String lastName;
    @Email(message = "Formato de email valido")
    private String email;
    private String password;
}
