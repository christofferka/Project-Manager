package com.example.projectcalctool.service;

import com.example.projectcalctool.model.Task;
import com.example.projectcalctool.model.TaskStatus;
import com.example.projectcalctool.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class TaskServiceTest {

    // Mock af repository
    private final TaskRepository taskRepository =
            Mockito.mock(TaskRepository.class);

    // Mock af afhængigheder
    private final SubprojectService subprojectService =
            Mockito.mock(SubprojectService.class);

    private final SubtaskService subtaskService =
            Mockito.mock(SubtaskService.class);

    private final UserService userService =
            Mockito.mock(UserService.class);

    // Service der testes
    private final TaskService taskService =
            new TaskService(taskRepository, subprojectService, subtaskService, userService);

    @Test
    void Test7() {

        // Opret task uden status
        Task task = new Task();
        task.setSubprojectId(1);
        task.setName("Test task");
        task.setEstimatedHours(5);
        task.setRate(700);
        task.setStatus(null);

        // Opret task via service
        taskService.create(task);

        // Status skal automatisk sættes
        assertEquals(TaskStatus.NOT_STARTED, task.getStatus());

        // Repository create skal kaldes
        verify(taskRepository).create(task);
    }
}
