package com.example.projectcalctool.repository;

import com.example.projectcalctool.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryTest {

    // Repository der testes
    @Autowired
    private UserRepository userRepository;

    @Test
    void Test2() {

        // Opret bruger
        User user = new User();
        user.setUsername("testuser_tc1");
        user.setEmail("test_tc1@example.com");
        user.setPassword("hashedpassword");
        user.setRole("USER");

        // Gem bruger i databasen
        userRepository.create(user);

        // Hent bruger via brugernavn
        User found =
                userRepository.findByUsername("testuser_tc1");

        // Verificer at brugeren findes og data er korrekt
        assertNotNull(found);
        assertEquals("testuser_tc1", found.getUsername());
        assertEquals("test_tc1@example.com", found.getEmail());
        assertEquals("USER", found.getRole());
    }

    @Test
    void Test3() {

        // Forsøg at hente bruger der ikke findes
        User found =
                userRepository.findByUsername("does_not_exist_tc1");

        // Skal returnere null
        assertNull(found);
    }
}
