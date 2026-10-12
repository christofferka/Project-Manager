package com.example.projectcalctool.service;

import com.example.projectcalctool.model.Subtask;
import com.example.projectcalctool.model.Task;
import com.example.projectcalctool.model.TaskStatus;
import com.example.projectcalctool.model.User;
import com.example.projectcalctool.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private SubprojectService subprojectService;
    @Mock
    private SubtaskService subtaskService;
    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("defaults null status to NOT_STARTED")
        void defaultsNullStatus() {
            // Given
            Task task = new Task();
            task.setStatus(null);

            // When
            taskService.create(task);

            // Then
            assertEquals(TaskStatus.NOT_STARTED, task.getStatus());
            verify(taskRepository).create(task);
        }

        @Test
        @DisplayName("clears userId when it is zero")
        void clearsZeroUserId() {
            // Given
            Task task = new Task();
            task.setUserId(0);

            // When
            taskService.create(task);

            // Then
            assertNull(task.getUserId());
        }

        @Test
        @DisplayName("keeps non-zero userId")
        void keepsNonZeroUserId() {
            // Given
            Task task = new Task();
            task.setUserId(42);

            // When
            taskService.create(task);

            // Then
            assertEquals(42, task.getUserId());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("defaults null status to NOT_STARTED")
        void defaultsNullStatus() {
            // Given
            Task task = new Task();
            task.setStatus(null);

            // When
            taskService.update(task);

            // Then
            assertEquals(TaskStatus.NOT_STARTED, task.getStatus());
            verify(taskRepository).update(task);
        }

        @Test
        @DisplayName("clears userId when it is zero")
        void clearsZeroUserId() {
            // Given
            Task task = new Task();
            task.setUserId(0);

            // When
            taskService.update(task);

            // Then
            assertNull(task.getUserId());
        }
    }

    @Test
    @DisplayName("delete removes subtasks before task")
    void delete_removesSubtasksBeforeTask() {
        // When
        taskService.delete(5);

        // Then
        var inOrder = inOrder(subtaskService, taskRepository);
        inOrder.verify(subtaskService).deleteByTaskId(5);
        inOrder.verify(taskRepository).delete(5);
    }

    @Nested
    @DisplayName("calculateTotalHoursForTask")
    class CalculateTotalHoursForTask {

        @Test
        @DisplayName("uses actualHours when present on task")
        void usesActualHoursOnTask() {
            // Given
            Task t = new Task();
            t.setEstimatedHours(10);
            t.setActualHours(7.0);
            t.setSubtasks(List.of());

            // When / Then
            assertEquals(7.0, taskService.calculateTotalHoursForTask(t));
        }

        @Test
        @DisplayName("falls back to estimatedHours when actualHours is null")
        void fallsBackToEstimatedHours() {
            // Given
            Task t = new Task();
            t.setEstimatedHours(10);
            t.setActualHours(null);
            t.setSubtasks(List.of());

            // When / Then
            assertEquals(10.0, taskService.calculateTotalHoursForTask(t));
        }

        @Test
        @DisplayName("adds subtask hours (actual preferred over estimated)")
        void addsSubtaskHours() {
            // Given
            Task t = new Task();
            t.setEstimatedHours(5);
            t.setActualHours(null);

            Subtask s1 = new Subtask();
            s1.setEstimatedHours(2);
            s1.setActualHours(3.0);

            Subtask s2 = new Subtask();
            s2.setEstimatedHours(4);
            s2.setActualHours(null);

            t.setSubtasks(List.of(s1, s2));

            // When / Then: 5 (task est) + 3 (s1 actual) + 4 (s2 est) = 12
            assertEquals(12.0, taskService.calculateTotalHoursForTask(t));
        }
    }

    @Test
    @DisplayName("getHoursForSubproject sums task+subtask hours across tasks")
    void getHoursForSubproject_sumsAll() {
        // Given
        Task t1 = new Task();
        t1.setId(1);
        t1.setEstimatedHours(4);
        t1.setStatus(TaskStatus.NOT_STARTED);
        Task t2 = new Task();
        t2.setId(2);
        t2.setEstimatedHours(6);
        t2.setStatus(TaskStatus.NOT_STARTED);

        when(taskRepository.findBySubprojectId(10)).thenReturn(List.of(t1, t2));
        when(subtaskService.getByTaskId(anyInt())).thenReturn(List.of());

        // When
        double hours = taskService.getHoursForSubproject(10);

        // Then
        assertEquals(10.0, hours);
    }

    @Test
    @DisplayName("getById loads subtasks and employee name when userId set")
    void getById_loadsSubtasksAndEmployeeName() {
        // Given
        Task t = new Task();
        t.setId(1);
        t.setUserId(7);
        t.setStatus(TaskStatus.IN_PROGRESS);

        User user = new User(7, "alice", "a@x.dk", "pw", "USER");

        when(taskRepository.findById(1)).thenReturn(t);
        when(subtaskService.getByTaskId(1)).thenReturn(List.of(new Subtask()));
        when(userService.getById(7)).thenReturn(user);

        // When
        Task result = taskService.getById(1);

        // Then
        assertEquals("alice", result.getEmployeeName());
        assertEquals(1, result.getSubtasks().size());
    }

    @Test
    @DisplayName("getById leaves employee name null when userId is null")
    void getById_nullUserIdLeavesEmployeeNameNull() {
        // Given
        Task t = new Task();
        t.setId(2);
        t.setUserId(null);
        t.setStatus(TaskStatus.NOT_STARTED);

        when(taskRepository.findById(2)).thenReturn(t);
        when(subtaskService.getByTaskId(2)).thenReturn(List.of());

        // When
        Task result = taskService.getById(2);

        // Then
        assertNull(result.getEmployeeName());
        verify(userService, never()).getById(anyInt());
    }

    @Test
    @DisplayName("getById defaults null status to NOT_STARTED")
    void getById_defaultsNullStatus() {
        // Given
        Task t = new Task();
        t.setId(3);
        t.setStatus(null);

        when(taskRepository.findById(3)).thenReturn(t);
        when(subtaskService.getByTaskId(3)).thenReturn(List.of());

        // When
        Task result = taskService.getById(3);

        // Then
        assertEquals(TaskStatus.NOT_STARTED, result.getStatus());
    }
}
