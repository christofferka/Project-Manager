package com.example.projectcalctool.service;

import com.example.projectcalctool.model.Subtask;
import com.example.projectcalctool.model.TaskStatus;
import com.example.projectcalctool.repository.SubtaskRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class SubtaskServiceTest {

    // Mock repository
    private final SubtaskRepository subtaskRepository =
            Mockito.mock(SubtaskRepository.class);

    // Service der testes
    private final SubtaskService subtaskService =
            new SubtaskService(subtaskRepository);

    @Test
    void Test5() {

        // Opret subtask uden status
        Subtask subtask = new Subtask();
        subtask.setTaskId(1);
        subtask.setName("Test subtask");
        subtask.setStatus(null);

        // Opret subtask via service
        subtaskService.create(subtask);

        // Status skal sættes automatisk
        assertEquals(TaskStatus.NOT_STARTED, subtask.getStatus());

        // Repository create skal kaldes
        verify(subtaskRepository).create(subtask);
    }
}
