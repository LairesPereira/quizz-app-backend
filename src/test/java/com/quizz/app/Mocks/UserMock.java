package com.quizz.app.Mocks;

import com.quizz.app.dto.UserRegisterDTO;
import com.quizz.app.enums.Roles;
import com.quizz.app.models.User;

import java.util.List;
import java.util.UUID;

public class UserMock {
    public static UserRegisterDTO validUser() {
        return UserRegisterDTO.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@gmail.com")
                .password("123456")
                .build();
    }

    public static User validModelUser() {
        return User.builder()
                .id(UUID.randomUUID().toString())
                .firstName("Test")
                .lastName("User")
                .email("test@gmail.com")
                .password("123456")
                .role(Roles.TEACHER)
                .build();
    }

    public static User completeFieldsUser() {
        return User.builder()
                .id(UUID.randomUUID().toString())
                .firstName("Test")
                .lastName("User")
                .email("test@gmail.com")
                .password("123456")
                .quizzList(List.of(QuizzMockFactory.createQuizzModel()))
                .role(Roles.TEACHER)
                .build();
    }

    public static List<UserRegisterDTO> invalidFields() {
        return List.of(
                // null fields
                UserRegisterDTO.builder().firstName(null).lastName("test").email("test@test.com").password("123456").build(),
                UserRegisterDTO.builder().firstName("test").lastName(null).email("test@test.com").password("123456").build(),
                UserRegisterDTO.builder().firstName("test").lastName("test").email(null).password("123456").build(),

                // invalid password lengths
                UserRegisterDTO.builder().firstName("test").lastName("test").email("test@test.com").password("123").build(),
                UserRegisterDTO.builder().firstName("test").lastName("test").email("test@test.com").password("12312312312312312312312312313213").build()
        );
    }

    public static UserRegisterDTO invalidPassword() {
        return UserRegisterDTO.builder()
                .firstName("test")
                .lastName("test")
                .email("test@test.com")
                .password("123")
                .build();
    }


}
