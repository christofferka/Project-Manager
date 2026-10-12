package com.example.projectcalctool.repository;

import com.example.projectcalctool.model.Subtask;
import com.example.projectcalctool.model.TaskStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SubtaskRepositoryTest {

    @Autowired
    private SubtaskRepository subtaskRepository;

    private String unique(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    @DisplayName("create persists a subtask retrievable by taskId")
    void create_persistsSubtask() {
        // Given: seed data provides task with id 1
        String name = unique("sub");
        Subtask subtask = new Subtask();
        subtask.setTaskId(1);
        subtask.setName(name);
        subtask.setDescription("desc");
        subtask.setEstimatedHours(4);
        subtask.setRate(800);
        subtask.setStatus(TaskStatus.NOT_STARTED);

        // When
        subtaskRepository.create(subtask);

        // Then
        Subtask found = subtaskRepository.findByTaskId(1).stream()
                .filter(s -> name.equals(s.getName()))
                .findFirst()
                .orElse(null);

        assertNotNull(found);
        assertEquals(TaskStatus.NOT_STARTED, found.getStatus());
        assertEquals(4.0, found.getEstimatedHours());
    }

    @Test
    @DisplayName("findByTaskId returns empty list when task has no subtasks")
    void findByTaskId_emptyForUnknownTask() {
        // When
        List<Subtask> result = subtaskRepository.findByTaskId(99_999);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("delete removes a subtask so it is no longer returned")
    void delete_removesSubtask() {
        // Given
        String name = unique("del");
        Subtask subtask = new Subtask();
        subtask.setTaskId(1);
        subtask.setName(name);
        subtask.setEstimatedHours(1);
        subtask.setRate(100);
        subtask.setStatus(TaskStatus.NOT_STARTED);
        subtaskRepository.create(subtask);

        int id = subtaskRepository.findByTaskId(1).stream()
                .filter(s -> name.equals(s.getName()))
                .findFirst()
                .orElseThrow()
                .getId();

        // When
        subtaskRepository.delete(id);

        // Then: subtask no longer appears in findByTaskId
        boolean stillPresent = subtaskRepository.findByTaskId(1).stream()
                .anyMatch(s -> s.getId() == id);
        assertFalse(stillPresent);
    }
}
