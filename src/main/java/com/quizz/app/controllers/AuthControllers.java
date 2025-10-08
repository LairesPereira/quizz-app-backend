package com.quizz.app.controllers;

import com.quizz.app.config.TokenService;
import com.quizz.app.dto.AuthenticationDTO;
import com.quizz.app.dto.LoginResponseDTO;
import com.quizz.app.dto.UserRegisterDTO;
import com.quizz.app.models.User;
import com.quizz.app.services.auth.AuthServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
        if (authServices.save(userRegisterDTO) != null )
            return new ResponseEntity<>(HttpStatus.CREATED);
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data) {
        var userNamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(userNamePassword);
        var token = tokenService.generateToken((User) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}
