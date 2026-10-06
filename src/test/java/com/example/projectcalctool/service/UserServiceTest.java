package com.example.projectcalctool.service;

import com.example.projectcalctool.model.User;
import com.example.projectcalctool.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserServiceTest {

    // Mock repository
    private final UserRepository userRepository =
            Mockito.mock(UserRepository.class);

    // Mock password encoder
    private final PasswordEncoder passwordEncoder =
            Mockito.mock(PasswordEncoder.class);

    // Service der testes
    private final UserService userService =
            new UserService(userRepository, passwordEncoder);

    @Test
    void Test6() {

        // Forventet bruger
        User user =
                new User(1, "testuser", "test@test.dk", "hash", "USER");

        // Simuler repository svar
        when(userRepository.findByUsername("testuser"))
                .thenReturn(user);

        // Hent bruger via service
        User found = userService.getByUsername("testuser");

        // Verificer resultat
        assertNotNull(found);
        assertEquals("testuser", found.getUsername());
    }
}
