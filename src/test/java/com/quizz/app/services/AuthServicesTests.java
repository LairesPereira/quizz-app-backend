package com.quizz.app.services;

import com.quizz.app.Mocks.UserMock;
import com.quizz.app.dto.UserRegisterDTO;
import com.quizz.app.models.User;
import com.quizz.app.repositorie.UserRepository;
import com.quizz.app.services.auth.AuthServices;
import com.quizz.app.utils.DefaultUserValues;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServicesTests {

    @Spy
    @InjectMocks
    AuthServices authServices;

    @Mock
    UserRepository userRepository;

    @Test
    void testSaveUser_CorrectData_ShouldSuccess() {
        UserRegisterDTO userRegisterDTO = UserRegisterDTO.builder()
                .firstName(DefaultUserValues.FIRST_NAME.getValue())
                .lastName(DefaultUserValues.LAST_NAME.getValue())
                .email(DefaultUserValues.EMAIL.getValue())
                .password(DefaultUserValues.PASSWORD.getValue())
                .build();

        when(userRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        var response = authServices.save(userRegisterDTO);

        assertNotNull(response);
        assertEquals(userRegisterDTO.getFirstName(), response.getFirstName());
        assertEquals(userRegisterDTO.getLastName(), response.getLastName());
        assertEquals(userRegisterDTO.getEmail(), response.getEmail());
        assertNotEquals(userRegisterDTO.getPassword(), response.getPassword());
    }

    @Test
    void testSaveUser_EmailAlreadyExists_ShouldThrowException() {
        UserRegisterDTO userRegisterDTO = UserRegisterDTO.builder()
                .firstName(DefaultUserValues.FIRST_NAME.getValue())
                .lastName(DefaultUserValues.LAST_NAME.getValue())
                .email(DefaultUserValues.EMAIL.getValue())
                .password(DefaultUserValues.PASSWORD.getValue())
                .build();

        User user = User.builder().build();

        when(userRepository.findByEmail(userRegisterDTO.getEmail())).thenReturn(user);

        assertThrows(Exception.class, () -> authServices.save(userRegisterDTO));
    }
}
