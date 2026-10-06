package com.example.projectcalctool.service;

import com.example.projectcalctool.model.Subproject;
import com.example.projectcalctool.model.Task;
import com.example.projectcalctool.model.TaskStatus;
import com.example.projectcalctool.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    // Repository til tasks
    private final TaskRepository taskRepository;

    // Service til delprojekter
    private final SubprojectService subprojectService;

    // Service til subtasks
    private final SubtaskService subtaskService;

    // Service til brugere
    private final UserService userService;

    // Constructor injection
    public TaskService(TaskRepository taskRepository,
                       SubprojectService subprojectService,
                       SubtaskService subtaskService,
                       UserService userService) {

        this.taskRepository = taskRepository;
        this.subprojectService = subprojectService;
        this.subtaskService = subtaskService;
        this.userService = userService;
    }

    // GETTERS

    // Henter tasks for et delprojekt
    public List<Task> getBySubprojectId(int subprojectId) {
        List<Task> tasks = taskRepository.findBySubprojectId(subprojectId);
        loadSubtasksIntoTasks(tasks);
        return tasks;
    }

    // Matcher controllerens metode
    public List<Task> getTasksBySubprojectId(int subprojectId) {
        return getBySubprojectId(subprojectId);
    }

    // Henter alle tasks
    public List<Task> getAll() {
        List<Task> tasks = taskRepository.findAll();
        loadSubtasksIntoTasks(tasks);
        return tasks;
    }

    // Henter task via id
    public Task getById(int id) {
        Task t = taskRepository.findById(id);

        // Sikrer standard status
        if (t.getStatus() == null) {
            t.setStatus(TaskStatus.NOT_STARTED);
        }

        // Indlæser subtasks
        t.setSubtasks(subtaskService.getByTaskId(id));

        // Indlæser medarbejdernavn
        if (t.getUserId() != null) {
            var user = userService.getById(t.getUserId());
            t.setEmployeeName(user != null ? user.getUsername() : null);
        } else {
            t.setEmployeeName(null);
        }

        return t;
    }

    // CREATE UPDATE DELETE

    // Opretter ny task
    public void create(Task task) {

        // Sætter standard status
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.NOT_STARTED);
        }

        // Rydder tom bruger
        if (task.getUserId() != null && task.getUserId() == 0) {
            task.setUserId(null);
        }

        taskRepository.create(task);
    }

    // Opdaterer eksisterende task
    public void update(Task task) {

        // Sætter standard status
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.NOT_STARTED);
        }

        // Rydder tom bruger
        if (task.getUserId() != null && task.getUserId() == 0) {
            task.setUserId(null);
        }

        taskRepository.update(task);
    }

    // Sletter task og tilhørende subtasks
    public void delete(int id) {
        subtaskService.deleteByTaskId(id);
        taskRepository.delete(id);
    }

    // CALCULATIONS

    // Beregner timer for et delprojekt
    public double getHoursForSubproject(int subprojectId) {
        return getBySubprojectId(subprojectId).stream()
                .mapToDouble(this::calculateTotalHoursForTask)
                .sum();
    }

    // Beregner samlede timer for en task
    public double calculateTotalHoursForTask(Task t) {

        double taskHours = t.getActualHours() != null
                ? t.getActualHours()
                : t.getEstimatedHours();

        double subtaskHours = t.getSubtasks().stream()
                .mapToDouble(st ->
                        st.getActualHours() != null
                                ? st.getActualHours()
                                : st.getEstimatedHours()
                )
                .sum();

        return taskHours + subtaskHours;
    }

    // Returnerer timer for alle delprojekter
    public List<Double> getAllSubprojectHours(List<Subproject> subs) {
        return subs.stream()
                .map(sub -> getHoursForSubproject(sub.getId()))
                .toList();
    }

    // TASKS FOR ENTIRE PROJECT

    // Henter alle tasks for et projekt
    public List<Task> getByProjectId(int projectId) {

        List<Subproject> subs =
                subprojectService.getByProjectId(projectId);

        List<Task> tasks = subs.stream()
                .flatMap(sub ->
                        taskRepository.findBySubprojectId(sub.getId()).stream())
                .toList();

        loadSubtasksIntoTasks(tasks);
        return tasks;
    }

    // Matcher controllerens metode
    public List<Task> getTasksByProjectId(int projectId) {
        return getByProjectId(projectId);
    }

    // INTERNAL HELPERS

    // Indlæser subtasks og metadata i tasks
    private void loadSubtasksIntoTasks(List<Task> tasks) {

        for (Task t : tasks) {

            t.setSubtasks(subtaskService.getByTaskId(t.getId()));

            // Sikrer standard status
            if (t.getStatus() == null) {
                t.setStatus(TaskStatus.NOT_STARTED);
            }

            // Indlæser medarbejdernavn
            if (t.getUserId() != null) {
                var user = userService.getById(t.getUserId());
                t.setEmployeeName(user != null ? user.getUsername() : null);
            } else {
                t.setEmployeeName(null);
            }
        }
    }
}
