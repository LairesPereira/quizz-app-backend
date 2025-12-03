package com.quizz.app.controllers;

import com.quizz.app.config.TokenService;
import com.quizz.app.dto.AuthenticationDTO;
import com.quizz.app.dto.LoginResponseDTO;
import com.quizz.app.dto.UserBasicInfoDTO;
import com.quizz.app.dto.UserRegisterDTO;
import com.quizz.app.models.User;
import com.quizz.app.services.auth.AuthServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthControllers {

    @Autowired
    AuthServices authServices;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        if (authServices.save(userRegisterDTO) != null)
            return ResponseEntity.status(HttpStatus.CREATED).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        var userNamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(userNamePassword);
        var token = tokenService.generateToken((User) auth.getPrincipal());

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(new LoginResponseDTO(token));
    }

    @GetMapping("/me")
    public ResponseEntity<UserBasicInfoDTO> me() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserBasicInfoDTO userBasicInfoDTO = UserBasicInfoDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
        return ResponseEntity.ok().body(userBasicInfoDTO);
    }
}
