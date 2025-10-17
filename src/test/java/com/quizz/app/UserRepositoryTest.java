package com.quizz.app;

import com.quizz.app.dto.UserRegisterDTO;
import com.quizz.app.enums.Roles;
import com.quizz.app.models.User;
import com.quizz.app.repositorie.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment =  SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class UserRepositoryTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCreateUserShouldReturnCreated() {
        UserRegisterDTO user = UserRegisterDTO.builder()
                .firstName("Laires")
                .lastName("Pereira")
                .email("lairespsoares@gmail.com")
                .password("123456")
                .build();

        ResponseEntity<Void> response = this.restTemplate.postForEntity("/auth/register", user, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testCreateUserWithIncompleteDataShouldFail() {
        List<UserRegisterDTO> users = List.of(
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


}
