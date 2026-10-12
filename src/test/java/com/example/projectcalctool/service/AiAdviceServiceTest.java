package com.example.projectcalctool.service;

import com.example.projectcalctool.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AiAdviceServiceTest {

    @Mock
    private ProjectService projectService;
    @Mock
    private SubprojectService subprojectService;
    @Mock
    private TaskService taskService;
    @Mock
    private ProjectUserService projectUserService;
    @Mock
    private UserService userService;

    @InjectMocks
    private AiAdviceService aiAdviceService;

    @Test
    @DisplayName("ask throws IllegalStateException when user is null")
    void ask_nullUser_throws() {
        // When / Then
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> aiAdviceService.ask(null, List.of(), "hej"));
        assertTrue(ex.getMessage().contains("AI-nøgle"));
    }

    @Test
    @DisplayName("ask throws IllegalStateException when user has no AI configured")
    void ask_userWithoutAi_throws() {
        // Given
        User user = new User(1, "alice", "a@x.dk", "pw", "USER");
        user.setAiProvider(null);
        user.setAiApiKey(null);

        // When / Then
        assertThrows(IllegalStateException.class,
                () -> aiAdviceService.ask(user, List.of(), "hej"));
    }

    @Test
    @DisplayName("ask throws IllegalStateException when API key is blank")
    void ask_blankApiKey_throws() {
        // Given
        User user = new User(1, "alice", "a@x.dk", "pw", "USER");
        user.setAiProvider("anthropic");
        user.setAiApiKey("   ");

        // When / Then
        assertThrows(IllegalStateException.class,
                () -> aiAdviceService.ask(user, List.of(), "hej"));
    }

    @Test
    @DisplayName("ask throws IllegalStateException for unknown provider")
    void ask_unknownProvider_throws() {
        // Given
        User user = new User(1, "alice", "a@x.dk", "pw", "USER");
        user.setAiProvider("mystery-llm");
        user.setAiApiKey("sk-123");

        // When / Then
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> aiAdviceService.ask(user, List.of(), "hej"));
        assertTrue(ex.getMessage().toLowerCase().contains("ukendt"));
    }
}
