package com.example.projectcalctool.service;

import com.example.projectcalctool.model.Subproject;
import com.example.projectcalctool.repository.SubprojectRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalculationService {

    private final SubprojectRepository subprojectRepository;
    private final TaskService taskService;

    public CalculationService(SubprojectRepository subprojectRepository,
                              TaskService taskService) {
        this.subprojectRepository = subprojectRepository;
        this.taskService = taskService;
    }

    // TOTAL HOURS FOR PROJECT
    public double calculateTotalHours(int projectId) {

        List<Subproject> subs = subprojectRepository.findByProjectId(projectId);

        return subs.stream()
                .mapToDouble(sub -> taskService.getHoursForSubproject(sub.getId()))
                .sum();
    }

    // TOTAL COST FOR PROJECT
    public double calculateTotalCost(int projectId) {

        List<Subproject> subs = subprojectRepository.findByProjectId(projectId);

        return subs.stream()
                .flatMap(sub ->
                        taskService.getBySubprojectId(sub.getId()).stream())
                .mapToDouble(t -> {
                    double hours = t.getActualHours() != null
                            ? t.getActualHours()
                            : t.getEstimatedHours();
                    return hours * t.getRate();
                })
                .sum();
    }
}
