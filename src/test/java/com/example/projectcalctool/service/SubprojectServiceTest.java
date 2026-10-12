package com.example.projectcalctool.service;

import com.example.projectcalctool.model.Subproject;
import com.example.projectcalctool.model.Task;
import com.example.projectcalctool.model.TaskStatus;
import com.example.projectcalctool.repository.SubprojectRepository;
import com.example.projectcalctool.repository.TaskRepository;
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
class SubprojectServiceTest {

    @Mock
    private SubprojectRepository subprojectRepository;
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private SubprojectService subprojectService;

    private Task task(TaskStatus status) {
        Task t = new Task();
        t.setStatus(status);
        return t;
    }

    @Test
    @DisplayName("getById sets progress to 0 when subproject has no tasks")
    void getById_noTasksGivesZeroPercent() {
        // Given
        Subproject sub = new Subproject();
        sub.setId(1);
        when(subprojectRepository.findById(1)).thenReturn(sub);
        when(taskRepository.getTasksBySubprojectId(1)).thenReturn(List.of());

        // When
        Subproject result = subprojectService.getById(1);

        // Then
        assertEquals(0, result.getProgressPercent());
    }

    @Test
    @DisplayName("getById sets progress to 100 when every task is DONE")
    void getById_allDoneGivesHundredPercent() {
        // Given
        Subproject sub = new Subproject(); sub.setId(1);
        when(subprojectRepository.findById(1)).thenReturn(sub);
        when(taskRepository.getTasksBySubprojectId(1))
                .thenReturn(List.of(task(TaskStatus.DONE), task(TaskStatus.DONE)));

        // When
        Subproject result = subprojectService.getById(1);

        // Then
        assertEquals(100, result.getProgressPercent());
    }

    @Test
    @DisplayName("getById computes percentage of DONE tasks (integer truncation)")
    void getById_mixedProgress() {
        // Given: 1 of 3 done = 33 (truncated)
        Subproject sub = new Subproject(); sub.setId(1);
        when(subprojectRepository.findById(1)).thenReturn(sub);
        when(taskRepository.getTasksBySubprojectId(1))
                .thenReturn(List.of(
                        task(TaskStatus.DONE),
                        task(TaskStatus.IN_PROGRESS),
                        task(TaskStatus.NOT_STARTED)
                ));

        // When
        Subproject result = subprojectService.getById(1);

        // Then
        assertEquals(33, result.getProgressPercent());
    }

    @Test
    @DisplayName("getByProjectId calculates progress for every subproject")
    void getByProjectId_calculatesForEach() {
        // Given
        Subproject a = new Subproject(); a.setId(1);
        Subproject b = new Subproject(); b.setId(2);
        when(subprojectRepository.findByProjectId(10)).thenReturn(List.of(a, b));
        when(taskRepository.getTasksBySubprojectId(1))
                .thenReturn(List.of(task(TaskStatus.DONE)));
        when(taskRepository.getTasksBySubprojectId(2)).thenReturn(List.of());

        // When
        List<Subproject> result = subprojectService.getByProjectId(10);

        // Then
        assertEquals(100, result.get(0).getProgressPercent());
        assertEquals(0, result.get(1).getProgressPercent());
    }

    @Test
    @DisplayName("getAll calculates progress for every subproject")
    void getAll_calculatesForEach() {
        // Given
        Subproject a = new Subproject(); a.setId(1);
        when(subprojectRepository.findAll()).thenReturn(List.of(a));
        when(taskRepository.getTasksBySubprojectId(1))
                .thenReturn(List.of(task(TaskStatus.DONE), task(TaskStatus.NOT_STARTED)));

        // When
        List<Subproject> result = subprojectService.getAll();

        // Then
        assertEquals(50, result.get(0).getProgressPercent());
    }

    @Test
    @DisplayName("create delegates to repository")
    void create_delegates() {
        // Given
        Subproject sub = new Subproject();

        // When
        subprojectService.create(sub);

        // Then
        verify(subprojectRepository).create(sub);
    }

    @Test
    @DisplayName("update delegates to repository")
    void update_delegates() {
        // Given
        Subproject sub = new Subproject();

        // When
        subprojectService.update(sub);

        // Then
        verify(subprojectRepository).update(sub);
    }

    @Test
    @DisplayName("delete delegates to repository")
    void delete_delegates() {
        // When
        subprojectService.delete(42);

        // Then
        verify(subprojectRepository).delete(42);
    }
}
