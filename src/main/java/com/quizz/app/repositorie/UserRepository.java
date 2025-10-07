package com.quizz.app.repositorie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<com.quizz.app.models.User, Long> {
    UserDetails findByEmail(String email);
    com.quizz.app.models.User save(com.quizz.app.models.User user);
}
