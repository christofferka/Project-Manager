package com.example.projectcalctool.repository;

import com.example.projectcalctool.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private String unique(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    @DisplayName("create persists a user retrievable by username")
    void create_thenFindByUsername() {
        // Given
        String username = unique("user");
        User user = new User(0, username, username + "@x.dk", "hash", "USER");

        // When
        userRepository.create(user);
        User found = userRepository.findByUsername(username);

        // Then
        assertNotNull(found);
        assertEquals(username, found.getUsername());
        assertEquals("USER", found.getRole());
    }

    @Test
    @DisplayName("findByUsername returns null for unknown user")
    void findByUsername_nullForUnknown() {
        // When / Then
        assertNull(userRepository.findByUsername(unique("ghost")));
    }

    @Test
    @DisplayName("findByEmail returns the user when email matches")
    void findByEmail_returnsUser() {
        // Given
        String username = unique("mailuser");
        String email = username + "@mail.dk";
        userRepository.create(new User(0, username, email, "hash", "USER"));

        // When
        User found = userRepository.findByEmail(email);

        // Then
        assertNotNull(found);
        assertEquals(username, found.getUsername());
    }

    @Test
    @DisplayName("findById returns the user by generated id")
    void findById_returnsUser() {
        // Given
        String username = unique("iduser");
        User u = new User(0, username, username + "@x.dk", "hash", "USER");
        userRepository.create(u);
        int id = userRepository.findByUsername(username).getId();

        // When
        User found = userRepository.findById(id);

        // Then
        assertNotNull(found);
        assertEquals(username, found.getUsername());
    }

    @Test
    @DisplayName("update changes mutable fields")
    void update_changesFields() {
        // Given
        String username = unique("upd");
        userRepository.create(new User(0, username, username + "@x.dk", "hash", "USER"));
        User persisted = userRepository.findByUsername(username);
        persisted.setRole("ADMIN");

        // When
        userRepository.update(persisted);

        // Then
        assertEquals("ADMIN", userRepository.findById(persisted.getId()).getRole());
    }

    @Test
    @DisplayName("updateAiSettings stores provider, key and model")
    void updateAiSettings_persistsAiFields() {
        // Given
        String username = unique("ai");
        userRepository.create(new User(0, username, username + "@x.dk", "hash", "USER"));
        int id = userRepository.findByUsername(username).getId();

        // When
        userRepository.updateAiSettings(id, "openai", "sk-xyz", "gpt-4o");

        // Then
        User found = userRepository.findById(id);
        assertEquals("openai", found.getAiProvider());
        assertEquals("sk-xyz", found.getAiApiKey());
        assertEquals("gpt-4o", found.getAiModel());
    }

    @Test
    @DisplayName("delete removes the user")
    void delete_removesUser() {
        // Given
        String username = unique("del");
        userRepository.create(new User(0, username, username + "@x.dk", "hash", "USER"));
        int id = userRepository.findByUsername(username).getId();

        // When
        userRepository.delete(id);

        // Then
        assertNull(userRepository.findByUsername(username));
    }

    @Test
    @DisplayName("isUserTableEmpty is false after creating a user")
    void isUserTableEmpty_falseAfterInsert() {
        // Given
        userRepository.create(new User(0, unique("notempty"),
                unique("notempty") + "@x.dk", "hash", "USER"));

        // When / Then
        assertFalse(userRepository.isUserTableEmpty());
    }
}
