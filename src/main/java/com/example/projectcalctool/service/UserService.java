package com.example.projectcalctool.service;

import com.example.projectcalctool.model.User;
import com.example.projectcalctool.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    // Repository til brugere
    private final UserRepository userRepository;

    // Encoder til passwords
    private final PasswordEncoder passwordEncoder;

    // Constructor injection
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Henter bruger via id
    public User getById(int id) {
        return userRepository.findById(id);
    }

    // Henter bruger via brugernavn
    public User getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Henter bruger via email
    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Henter alle brugere
    public List<User> getAll() {
        return userRepository.findAll();
    }

    // Opretter ny bruger
    public void create(User user) {
        user.setPassword(hashIfNeeded(user.getPassword()));
        userRepository.create(user);
    }

    // Opdaterer eksisterende bruger
    public void update(User user) {
        user.setPassword(hashIfNeeded(user.getPassword()));
        userRepository.update(user);
    }

    // Tjekker om brugertabellen er tom
    public boolean isUserTableEmpty() {
        return userRepository.isUserTableEmpty();
    }

    // Hasher password hvis det ikke allerede er hashet
    private String hashIfNeeded(String password) {

        if (password == null || password.isBlank()) return password;

        boolean looksHashed =
                password.startsWith("$2a$")
                        || password.startsWith("$2b$")
                        || password.startsWith("$2y$");

        if (looksHashed) return password;

        return passwordEncoder.encode(password);
    }
}
