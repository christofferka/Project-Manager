package com.example.projectcalctool.service;

import com.example.projectcalctool.model.Subtask;
import com.example.projectcalctool.model.TaskStatus;
import com.example.projectcalctool.repository.SubtaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubtaskServiceTest {

    @Mock
    private SubtaskRepository subtaskRepository;

    @InjectMocks
    private SubtaskService subtaskService;

    @Test
    @DisplayName("create sets NOT_STARTED when status is null")
    void create_defaultsNullStatusToNotStarted() {
        // Given
        Subtask s = new Subtask();
        s.setStatus(null);

        // When
        subtaskService.create(s);

        // Then
        assertEquals(TaskStatus.NOT_STARTED, s.getStatus());
        verify(subtaskRepository).create(s);
    }

    @Test
    @DisplayName("create keeps caller-provided status")
    void create_keepsExistingStatus() {
        // Given
        Subtask s = new Subtask();
        s.setStatus(TaskStatus.IN_PROGRESS);

        // When
        subtaskService.create(s);

        // Then
        assertEquals(TaskStatus.IN_PROGRESS, s.getStatus());
    }

    @Test
    @DisplayName("update sets NOT_STARTED when status is null")
    void update_defaultsNullStatus() {
        // Given
        Subtask s = new Subtask();
        s.setStatus(null);

        // When
        subtaskService.update(s);

        // Then
        assertEquals(TaskStatus.NOT_STARTED, s.getStatus());
        verify(subtaskRepository).update(s);
    }

    @Test
    @DisplayName("getByTaskId backfills null statuses with NOT_STARTED")
    void getByTaskId_backfillsNullStatuses() {
        // Given
        Subtask withStatus = new Subtask();
        withStatus.setStatus(TaskStatus.DONE);

        Subtask withoutStatus = new Subtask();
        withoutStatus.setStatus(null);

        when(subtaskRepository.findByTaskId(1))
                .thenReturn(List.of(withStatus, withoutStatus));

        // When
        List<Subtask> result = subtaskService.getByTaskId(1);

        // Then
        assertEquals(TaskStatus.DONE, result.get(0).getStatus());
        assertEquals(TaskStatus.NOT_STARTED, result.get(1).getStatus());
    }

    @Test
    @DisplayName("getById backfills null status with NOT_STARTED")
    void getById_backfillsNullStatus() {
        // Given
        Subtask s = new Subtask();
        s.setStatus(null);
        when(subtaskRepository.findById(5)).thenReturn(s);

        // When
        Subtask result = subtaskService.getById(5);

        // Then
        assertEquals(TaskStatus.NOT_STARTED, result.getStatus());
    }

    @Test
    @DisplayName("delete delegates to repository")
    void delete_delegates() {
        // When
        subtaskService.delete(42);

        // Then
        verify(subtaskRepository).delete(42);
    }

    @Test
    @DisplayName("deleteByTaskId removes every subtask of the task")
    void deleteByTaskId_removesAll() {
        // Given
        Subtask a = new Subtask(); a.setId(10);
        Subtask b = new Subtask(); b.setId(11);
        when(subtaskRepository.findByTaskId(7)).thenReturn(List.of(a, b));

        // When
        subtaskService.deleteByTaskId(7);

        // Then
        verify(subtaskRepository).delete(10);
        verify(subtaskRepository).delete(11);
    }

    @Test
    @DisplayName("deleteByTaskId is a no-op when there are no subtasks")
    void deleteByTaskId_noOpWhenEmpty() {
        // Given
        when(subtaskRepository.findByTaskId(7)).thenReturn(List.of());

        // When
        subtaskService.deleteByTaskId(7);

        // Then
        verify(subtaskRepository, never()).delete(anyInt());
    }
}
