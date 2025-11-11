package com.quizz.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegisterDTO {
    @NotBlank(message = "O primeiro nome é obrigatório")
    private String firstName;

    @NotBlank(message = "O sobrenome é obrigatório")
    private String lastName;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Insira um email válido")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, max = 30, message = "A senha deve ter no mínimo 6 caracteres e no maximo 30")
    private String password;

}
