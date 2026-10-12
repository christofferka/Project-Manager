package com.example.projectcalctool.service;

import com.example.projectcalctool.model.User;
import com.example.projectcalctool.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("Delegation to repository")
    class Delegation {

        @Test
        @DisplayName("getById delegates to repository")
        void getById_delegatesToRepository() {
            // Given
            User user = new User(1, "alice", "a@x.dk", "pw", "USER");
            when(userRepository.findById(1)).thenReturn(user);

            // When
            User result = userService.getById(1);

            // Then
            assertSame(user, result);
            verify(userRepository).findById(1);
        }

        @Test
        @DisplayName("getByUsername returns matching user")
        void getByUsername_returnsUser() {
            // Given
            User user = new User(1, "alice", "a@x.dk", "pw", "USER");
            when(userRepository.findByUsername("alice")).thenReturn(user);

            // When
            User result = userService.getByUsername("alice");

            // Then
            assertEquals("alice", result.getUsername());
        }

        @Test
        @DisplayName("getByUsername returns null for unknown user")
        void getByUsername_returnsNullForUnknown() {
            // Given
            when(userRepository.findByUsername("ghost")).thenReturn(null);

            // When / Then
            assertNull(userService.getByUsername("ghost"));
        }

        @Test
        @DisplayName("getByEmail delegates to repository")
        void getByEmail_delegates() {
            // Given
            User user = new User(1, "alice", "a@x.dk", "pw", "USER");
            when(userRepository.findByEmail("a@x.dk")).thenReturn(user);

            // When / Then
            assertSame(user, userService.getByEmail("a@x.dk"));
        }

        @Test
        @DisplayName("getAll delegates to repository")
        void getAll_delegates() {
            // Given
            List<User> users = List.of(new User(), new User());
            when(userRepository.findAll()).thenReturn(users);

            // When / Then
            assertEquals(2, userService.getAll().size());
        }

        @Test
        @DisplayName("isUserTableEmpty delegates to repository")
        void isUserTableEmpty_delegates() {
            // Given
            when(userRepository.isUserTableEmpty()).thenReturn(true);

            // When / Then
            assertTrue(userService.isUserTableEmpty());
        }
    }

    @Nested
    @DisplayName("Password hashing on create")
    class PasswordHashingOnCreate {

        @Test
        @DisplayName("hashes plain-text password before persisting")
        void create_hashesPlainTextPassword() {
            // Given
            User user = new User("alice", "a@x.dk", "plaintext");
            when(passwordEncoder.encode("plaintext")).thenReturn("$2a$HASHED");

            // When
            userService.create(user);

            // Then
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).create(captor.capture());
            assertEquals("$2a$HASHED", captor.getValue().getPassword());
            verify(passwordEncoder).encode("plaintext");
        }

        @Test
        @DisplayName("does not re-hash a bcrypt $2a$ password")
        void create_doesNotRehashAlreadyHashed2a() {
            // Given
            User user = new User("alice", "a@x.dk", "$2a$10$abc");

            // When
            userService.create(user);

            // Then
            verify(passwordEncoder, never()).encode(any());
            verify(userRepository).create(user);
            assertEquals("$2a$10$abc", user.getPassword());
        }

        @Test
        @DisplayName("does not re-hash a bcrypt $2b$ password")
        void create_doesNotRehash2b() {
            // Given
            User user = new User("alice", "a@x.dk", "$2b$10$abc");

            // When
            userService.create(user);

            // Then
            verify(passwordEncoder, never()).encode(any());
        }

        @Test
        @DisplayName("does not re-hash a bcrypt $2y$ password")
        void create_doesNotRehash2y() {
            // Given
            User user = new User("alice", "a@x.dk", "$2y$10$abc");

            // When
            userService.create(user);

            // Then
            verify(passwordEncoder, never()).encode(any());
        }

        @Test
        @DisplayName("passes null password through untouched")
        void create_passesNullPassword() {
            // Given
            User user = new User();
            user.setUsername("bob");
            user.setPassword(null);

            // When
            userService.create(user);

            // Then
            verify(passwordEncoder, never()).encode(any());
            assertNull(user.getPassword());
        }

        @Test
        @DisplayName("passes blank password through untouched")
        void create_passesBlankPassword() {
            // Given
            User user = new User();
            user.setPassword("   ");

            // When
            userService.create(user);

            // Then
            verify(passwordEncoder, never()).encode(any());
        }
    }

    @Nested
    @DisplayName("Password hashing on update")
    class PasswordHashingOnUpdate {

        @Test
        @DisplayName("hashes new plain-text password before updating")
        void update_hashesPlainTextPassword() {
            // Given
            User user = new User(1, "alice", "a@x.dk", "newplain", "USER");
            when(passwordEncoder.encode("newplain")).thenReturn("$2a$NEWHASH");

            // When
            userService.update(user);

            // Then
            assertEquals("$2a$NEWHASH", user.getPassword());
            verify(userRepository).update(user);
        }

        @Test
        @DisplayName("keeps existing hash when password already hashed")
        void update_keepsExistingHash() {
            // Given
            User user = new User(1, "alice", "a@x.dk", "$2a$10$keep", "USER");

            // When
            userService.update(user);

            // Then
            verify(passwordEncoder, never()).encode(any());
            assertEquals("$2a$10$keep", user.getPassword());
        }
    }
}
