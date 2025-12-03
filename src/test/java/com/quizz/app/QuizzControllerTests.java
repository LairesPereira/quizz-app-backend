//package com.quizz.app;
//
//import com.quizz.app.Mocks.QuizzMockFactory;
//import com.quizz.app.config.TestSecurityConfig;
//import com.quizz.app.dto.AuthenticationDTO;
//import com.quizz.app.dto.CreateQuizzDTO;
//import com.quizz.app.dto.LoginResponseDTO;
//import com.quizz.app.dto.UserRegisterDTO;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.MethodSource;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.*;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.Map;
//import java.util.stream.Stream;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ActiveProfiles("test")
//@Import(TestSecurityConfig.class)
//public class QuizzControllerTests {
//
//    private static final Logger logger = LoggerFactory.getLogger(QuizzControllerTests.class);
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    private static UserRegisterDTO user;
//    private static LoginResponseDTO loginResponse;
//
//    @BeforeEach
//    void setUp() {
//        user = UserRegisterDTO.builder()
//                .firstName("Test")
//                .lastName("User")
//                .email("test@gmail.com")
//                .password("123456")
//                .build();
//
//        restTemplate.postForEntity("/auth/register", user, Void.class);
//
//        AuthenticationDTO login = new AuthenticationDTO(user.getEmail(), user.getPassword());
//        ResponseEntity<LoginResponseDTO> response = restTemplate.postForEntity("/auth/login", login, LoginResponseDTO.class);
//        loginResponse = response.getBody();
//    }
//
////    @Test
////    void createQuizzWithRealAuthShouldReturnSuccess() {
////        CreateQuizzDTO quizz = QuizzMockFactory.createSimpleQuizz();
////
////        ResponseEntity<Void> response = restTemplate.postForEntity(
////                "/quizzes",
////                quizz,
////                Void.class
////        );
////
////        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
////    }
////
////    @Test
////    void toggleQuizzStatusShouldReturnSuccess() {
////
////    }
//}
