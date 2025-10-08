package com.quizz.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDTO {
    @NotBlank
    @Size(min = 3, max = 20, message = "O nome deve ter entre 3 e 20 carcteres")
    private String firstName;
    @NotBlank
    @Size(min = 3, max = 20, message = "O segundo nome deve ter entre 3 e 20 carcteres")
    private String lastName;
    @Email(message = "Insira um email valido")
    private String email;
    @Size(min = 6, message = "A senha deve ter no minimo 06 caracteres")
    private String password;
}
