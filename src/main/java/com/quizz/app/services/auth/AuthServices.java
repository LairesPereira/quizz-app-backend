package com.quizz.app.services.auth;

import com.quizz.app.dto.UserRegisterDTO;
import com.quizz.app.enums.Roles;
import com.quizz.app.errors.AlreadyExistsException;
import com.quizz.app.repositorie.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.quizz.app.models.User;
import java.util.List;

@Service
public class AuthServices {
    
    @Autowired
    UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByEmail(String email) {
        return (User) userRepository.findByEmail(email);
    }

    public User save(UserRegisterDTO user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new AlreadyExistsException("Email já cadastrado!");
        }

        User userEntity = User.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(new BCryptPasswordEncoder().encode(user.getPassword()))
                .role(Roles.TEACHER)
                .build();

        return userRepository.save(userEntity);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
