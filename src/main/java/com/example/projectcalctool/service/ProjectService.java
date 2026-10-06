package com.example.projectcalctool.service;

import com.example.projectcalctool.model.Project;
import com.example.projectcalctool.repository.ProjectRepository;
import com.example.projectcalctool.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProjectService {

    // Repository til projekter
    private final ProjectRepository projectRepository;

    // Repository til tasks
    private final TaskRepository taskRepository;

    // Constructor injection
    public ProjectService(ProjectRepository projectRepository,
                          TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    // Henter alle projekter
    public List<Project> getAll() {
        return projectRepository.findAll();
    }

    // Henter projekt via id
    public Project getById(int id) {
        return projectRepository.findById(id);
    }

    // Opretter nyt projekt
    public int create(Project project) {
        return projectRepository.create(project);
    }

    // Opdaterer eksisterende projekt
    public void update(Project project) {
        projectRepository.update(project);
    }

    // Sletter projekt
    public void delete(int id) {
        projectRepository.delete(id);
    }

    // Beregner samlede task-timer for projekt
    public double getTotalTaskHours(int projectId) {
        return taskRepository.getTotalHoursForProject(projectId);
    }

    // Beregner samlet estimeret projektomkostning
    public double totalCost() {
        return getAll().stream()
                .mapToDouble(p -> p.getEstimatedHours() * p.getHourlyRate())
                .sum();
    }

    // Returnerer standard roadmap til visning
    public List<Map<String, String>> getDefaultRoadmap() {
        return List.of(
                Map.of(
                        "title", "Projekt kickoff",
                        "description", "Planlægning og etablering af projektstruktur"
                ),
                Map.of(
                        "title", "Designfase",
                        "description", "Mockups og brugerflow"
                ),
                Map.of(
                        "title", "Udviklingsfase",
                        "description", "Backend, API og datamodel bygges"
                ),
                Map.of(
                        "title", "Testfase",
                        "description", "Test, rettelser og kvalitetssikring"
                ),
                Map.of(
                        "title", "Deployment",
                        "description", "Release og dokumentation"
                )
        );
    }
}
