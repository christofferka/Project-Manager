package com.example.projectcalctool.service;

import com.example.projectcalctool.model.Subproject;
import com.example.projectcalctool.model.Task;
import com.example.projectcalctool.repository.SubprojectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculationServiceTest {

    @Mock
    private SubprojectRepository subprojectRepository;
    @Mock
    private TaskService taskService;

    @InjectMocks
    private CalculationService calculationService;

    @Test
    @DisplayName("calculateTotalHours sums hours across all subprojects")
    void calculateTotalHours_sumsSubprojects() {
        // Given
        Subproject s1 = new Subproject(); s1.setId(1);
        Subproject s2 = new Subproject(); s2.setId(2);
        when(subprojectRepository.findByProjectId(10)).thenReturn(List.of(s1, s2));
        when(taskService.getHoursForSubproject(1)).thenReturn(5.0);
        when(taskService.getHoursForSubproject(2)).thenReturn(3.5);

        // When / Then
        assertEquals(8.5, calculationService.calculateTotalHours(10));
    }

    @Test
    @DisplayName("calculateTotalHours returns 0 when there are no subprojects")
    void calculateTotalHours_returnsZeroWhenNoSubprojects() {
        // Given
        when(subprojectRepository.findByProjectId(99)).thenReturn(List.of());

        // When / Then
        assertEquals(0.0, calculationService.calculateTotalHours(99));
    }

    @Test
    @DisplayName("calculateTotalCost uses actualHours when present")
    void calculateTotalCost_usesActualHours() {
        // Given
        Subproject sub = new Subproject(); sub.setId(1);
        when(subprojectRepository.findByProjectId(10)).thenReturn(List.of(sub));

        Task task = new Task();
        task.setEstimatedHours(10);
        task.setActualHours(8.0);
        task.setRate(500);
        when(taskService.getBySubprojectId(1)).thenReturn(List.of(task));

        // When / Then: 8 * 500
        assertEquals(4000.0, calculationService.calculateTotalCost(10));
    }

    @Test
    @DisplayName("calculateTotalCost falls back to estimatedHours when actual is null")
    void calculateTotalCost_fallsBackToEstimatedHours() {
        // Given
        Subproject sub = new Subproject(); sub.setId(1);
        when(subprojectRepository.findByProjectId(10)).thenReturn(List.of(sub));

        Task task = new Task();
        task.setEstimatedHours(10);
        task.setActualHours(null);
        task.setRate(500);
        when(taskService.getBySubprojectId(1)).thenReturn(List.of(task));

        // When / Then: 10 * 500
        assertEquals(5000.0, calculationService.calculateTotalCost(10));
    }

    @Test
    @DisplayName("calculateTotalCost sums tasks across all subprojects")
    void calculateTotalCost_sumsAcrossSubprojects() {
        // Given
        Subproject s1 = new Subproject(); s1.setId(1);
        Subproject s2 = new Subproject(); s2.setId(2);
        when(subprojectRepository.findByProjectId(10)).thenReturn(List.of(s1, s2));

        Task t1 = new Task();
        t1.setEstimatedHours(2); t1.setActualHours(null); t1.setRate(100);
        Task t2 = new Task();
        t2.setEstimatedHours(4); t2.setActualHours(3.0); t2.setRate(200);

        when(taskService.getBySubprojectId(1)).thenReturn(List.of(t1));
        when(taskService.getBySubprojectId(2)).thenReturn(List.of(t2));

        // When / Then: 2*100 + 3*200 = 800
        assertEquals(800.0, calculationService.calculateTotalCost(10));
    }
}
