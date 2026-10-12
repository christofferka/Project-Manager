package com.example.projectcalctool.service;

import com.example.projectcalctool.model.Project;
import com.example.projectcalctool.repository.ProjectRepository;
import com.example.projectcalctool.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project(Double rate, Double hours) {
        Project p = new Project();
        p.setHourlyRate(rate);
        p.setEstimatedHours(hours);
        return p;
    }

    @Test
    @DisplayName("totalCost sums rate*hours across all projects")
    void totalCost_sumsAll() {
        // Given
        when(projectRepository.findAll())
                .thenReturn(List.of(project(500.0, 10.0), project(1000.0, 4.0)));

        // When / Then: 5000 + 4000
        assertEquals(9000.0, projectService.totalCost());
    }

    @Test
    @DisplayName("totalCost returns 0 when no projects exist")
    void totalCost_zeroWhenEmpty() {
        // Given
        when(projectRepository.findAll()).thenReturn(List.of());

        // When / Then
        assertEquals(0.0, projectService.totalCost());
    }

    @Test
    @DisplayName("create delegates and returns generated id")
    void create_delegates() {
        // Given
        Project p = new Project();
        when(projectRepository.create(p)).thenReturn(42);

        // When
        int id = projectService.create(p);

        // Then
        assertEquals(42, id);
    }

    @Test
    @DisplayName("getTotalTaskHours delegates to taskRepository")
    void getTotalTaskHours_delegates() {
        // Given
        when(taskRepository.getTotalHoursForProject(1)).thenReturn(123.5);

        // When / Then
        assertEquals(123.5, projectService.getTotalTaskHours(1));
    }

    @Test
    @DisplayName("getById / update / delete delegate to repository")
    void simpleDelegation() {
        // Given
        Project p = new Project();
        when(projectRepository.findById(1)).thenReturn(p);

        // When
        projectService.getById(1);
        projectService.update(p);
        projectService.delete(1);

        // Then
        verify(projectRepository).findById(1);
        verify(projectRepository).update(p);
        verify(projectRepository).delete(1);
    }

    @Test
    @DisplayName("getDefaultRoadmap returns the five canonical phases in order")
    void defaultRoadmap_hasFivePhasesInOrder() {
        // When
        List<Map<String, String>> roadmap = projectService.getDefaultRoadmap();

        // Then
        assertEquals(5, roadmap.size());
        assertEquals("Projekt kickoff", roadmap.get(0).get("title"));
        assertEquals("Deployment", roadmap.get(4).get("title"));
        // Every entry has a title and a description
        roadmap.forEach(entry -> {
            assertNotNull(entry.get("title"));
            assertNotNull(entry.get("description"));
        });
    }
}
