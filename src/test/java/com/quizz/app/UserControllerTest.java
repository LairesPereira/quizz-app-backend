package com.quizz.app;

import com.quizz.app.config.TestSecurityConfig;
import com.quizz.app.dto.AuthenticationDTO;
import com.quizz.app.dto.LoginResponseDTO;
import com.quizz.app.dto.UserBasicInfoDTO;
import com.quizz.app.dto.UserRegisterDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment =  SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class UserControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(UserControllerTest.class);

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCreateUserShouldReturnCreated() {
        UserRegisterDTO user = UserRegisterDTO.builder()
                .firstName("test")
                .lastName("test")
                .email("test@gmail.com")
                .password("123456")
                .build();

        ResponseEntity<Void> response = this.restTemplate.postForEntity("/auth/register", user, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testCreateUserWithIncompleteDataShouldFail() {
        List<UserRegisterDTO> users = List.of(
//              Teste para campo nulos
                UserRegisterDTO.builder().firstName(null).lastName("test").email("test@test.com").password("123456").build(),
                UserRegisterDTO.builder().firstName("test").lastName(null).email("test@test.com").password("123456").build(),
                UserRegisterDTO.builder().firstName("test").lastName("test").email(null).password("123456").build(),
                UserRegisterDTO.builder().firstName("test").lastName("test").email("test@test.com").password(null).build()
        );

        for (UserRegisterDTO user : users) {
            ResponseEntity<Void> response = this.restTemplate.postForEntity("/auth/register", user, Void.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    void testCreateUserWithInvalidPassowrdShouldFail() {
        List<UserRegisterDTO> users = List.of(
                UserRegisterDTO.builder().firstName("test").lastName("test").email("test@test.com").password("12").build(),
                UserRegisterDTO.builder().firstName("test").lastName("test").email("test@test.com").password("01031030120301203012301023012030120120310230102").build()
        );

        for (UserRegisterDTO user : users) {
            ResponseEntity<Void> response = this.restTemplate.postForEntity("/auth/register", user, Void.class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Test
    void testLoginUserWithCorrectDataShouldReturnSuccess() {
        UserRegisterDTO user = UserRegisterDTO.builder()
                .firstName("test")
                .lastName("test")
                .email("test@gmail.com")
                .password("123456")
                .build();

        restTemplate.postForEntity("/auth/register", user, Void.class);

        ResponseEntity<LoginResponseDTO> response = restTemplate.postForEntity("/auth/login", user, LoginResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().token()).isNotNull();
    }

    @Test
    void testLoginUserWithWrongDataShouldFail() {
        UserRegisterDTO user = UserRegisterDTO.builder()
                .firstName("test3")
                .lastName("test3")
                .email("test3@gmail.com")
                .password("123456")
                .build();

        restTemplate.postForEntity("/auth/register", user, Void.class);

        AuthenticationDTO authenticationDTO = new AuthenticationDTO("test3@gmail.com", "09876adasd54321");
        ResponseEntity<LoginResponseDTO> response = restTemplate.postForEntity("/auth/login", authenticationDTO, LoginResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testGetUserBasicInfoShouldReturnSuccess() {
        // 1. Registrar o usuário
        UserRegisterDTO user = UserRegisterDTO.builder()
                .firstName("test4")
                .lastName("test4")
                .email("test4@gmail.com")
                .password("123456")
                .build();
        restTemplate.postForEntity("/auth/register", user, Void.class);

        // 2. Fazer login e obter token
        AuthenticationDTO authenticationDTO = new AuthenticationDTO("test4@gmail.com", "123456");
        ResponseEntity<LoginResponseDTO> loginResponse = restTemplate.postForEntity(
                "/auth/login", authenticationDTO, LoginResponseDTO.class
        );

        Assertions.assertNotNull(loginResponse.getBody());
        String token = loginResponse.getBody().token();

        // 3. Configurar headers com token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<UserBasicInfoDTO> responseAuth = restTemplate.exchange(
                "/auth/me",
                HttpMethod.GET,
                request,
                UserBasicInfoDTO.class
        );

        assertThat(responseAuth.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertNotNull(responseAuth.getBody());
        assertThat(responseAuth.getBody().getFirstName()).isNotNull();
        assertThat(responseAuth.getBody().getLastName()).isNotNull();
    }
}
