package com.example.projectcalctool.service;

import com.example.projectcalctool.model.Subproject;
import com.example.projectcalctool.repository.SubprojectRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculationServiceTest {

    @Test
    void Test4() {

        // Mock repositories og services
        SubprojectRepository subprojectRepository =
                Mockito.mock(SubprojectRepository.class);

        TaskService taskService =
                Mockito.mock(TaskService.class);

        // Opret delprojekter
        Subproject s1 = new Subproject();
        s1.setId(1);

        Subproject s2 = new Subproject();
        s2.setId(2);

        // Simuler database svar
        Mockito.when(subprojectRepository.findByProjectId(10))
                .thenReturn(List.of(s1, s2));

        Mockito.when(taskService.getHoursForSubproject(1))
                .thenReturn(5.0);

        Mockito.when(taskService.getHoursForSubproject(2))
                .thenReturn(3.5);

        // Opret service der testes
        CalculationService service =
                new CalculationService(subprojectRepository, taskService);

        // Beregn samlede timer
        double result =
                service.calculateTotalHours(10);

        // Forventet sum
        assertEquals(8.5, result);
    }
}
