package com.example.projectcalctool.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("no-arg constructor defaults role to USER")
    void noArgConstructor_defaultsRole() {
        assertEquals("USER", new User().getRole());
    }

    @Test
    @DisplayName("registration constructor defaults role to USER")
    void registrationConstructor_defaultsRole() {
        User user = new User("alice", "a@x.dk", "pw");
        assertEquals("USER", user.getRole());
    }

    @Test
    @DisplayName("hasAiConfigured true when provider and api key set")
    void hasAiConfigured_trueWhenBothSet() {
        // Given
        User user = new User();
        user.setAiProvider("openai");
        user.setAiApiKey("sk-123");

        // When / Then
        assertTrue(user.hasAiConfigured());
    }

    @Test
    @DisplayName("hasAiConfigured false when provider is null")
    void hasAiConfigured_falseWhenProviderNull() {
        // Given
        User user = new User();
        user.setAiApiKey("sk-123");

        // When / Then
        assertFalse(user.hasAiConfigured());
    }

    @Test
    @DisplayName("hasAiConfigured false when provider is blank")
    void hasAiConfigured_falseWhenProviderBlank() {
        // Given
        User user = new User();
        user.setAiProvider("  ");
        user.setAiApiKey("sk-123");

        // When / Then
        assertFalse(user.hasAiConfigured());
    }

    @Test
    @DisplayName("hasAiConfigured false when API key is null")
    void hasAiConfigured_falseWhenKeyNull() {
        // Given
        User user = new User();
        user.setAiProvider("openai");

        // When / Then
        assertFalse(user.hasAiConfigured());
    }

    @Test
    @DisplayName("hasAiConfigured false when API key is blank")
    void hasAiConfigured_falseWhenKeyBlank() {
        // Given
        User user = new User();
        user.setAiProvider("openai");
        user.setAiApiKey("");

        // When / Then
        assertFalse(user.hasAiConfigured());
    }
}
