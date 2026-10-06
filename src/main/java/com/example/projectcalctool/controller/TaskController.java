package com.example.projectcalctool.controller;

import com.example.projectcalctool.model.Project;
import com.example.projectcalctool.model.Subproject;
import com.example.projectcalctool.model.Task;
import com.example.projectcalctool.model.TaskStatus;
import com.example.projectcalctool.model.User;
import com.example.projectcalctool.service.ProjectService;
import com.example.projectcalctool.service.ProjectUserService;
import com.example.projectcalctool.service.SubprojectService;
import com.example.projectcalctool.service.SubtaskService;
import com.example.projectcalctool.service.TaskService;
import com.example.projectcalctool.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/project/{projectId}/tasks")
public class TaskController {

    // JDBC service til tasks
    private final TaskService taskService;

    // JDBC service til delprojekter
    private final SubprojectService subprojectService;

    // JDBC service til projekter
    private final ProjectService projectService;

    // JDBC service til brugere
    private final UserService userService;

    // JDBC service til subtasks
    private final SubtaskService subtaskService;

    // JDBC service til projektbrugere og rettigheder
    private final ProjectUserService projectUserService;

    // Constructor injection
    public TaskController(TaskService taskService,
                          SubprojectService subprojectService,
                          ProjectService projectService,
                          UserService userService,
                          SubtaskService subtaskService,
                          ProjectUserService projectUserService) {

        this.taskService = taskService;
        this.subprojectService = subprojectService;
        this.projectService = projectService;
        this.userService = userService;
        this.subtaskService = subtaskService;
        this.projectUserService = projectUserService;
    }

    // Tjekker om brugeren er global admin
    private boolean isGlobalAdmin(User user) {
        return user != null && "ADMIN".equals(user.getRole());
    }

    // Tjekker adgang til projekt
    private boolean hasProjectAccess(int projectId, User current) {
        return isGlobalAdmin(current)
                || projectUserService.getProjectUser(projectId, current.getId()) != null;
    }

    // Tjekker redigeringsrettighed for tasks
    private boolean canEditTasks(int projectId, User current) {
        return isGlobalAdmin(current)
                || projectUserService.canEditTasks(projectId, current.getId());
    }

    // Tjekker om delprojekt tilhører projekt
    private boolean isValidProjectSubproject(int projectId, int subprojectId) {
        Subproject subproject = subprojectService.getById(subprojectId);
        return subproject != null && subproject.getProjectId() == projectId;
    }

    // Viser liste over tasks
    @GetMapping
    public String list(@PathVariable int projectId,
                       @RequestParam int subprojectId,
                       Model model,
                       HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Henter projekt og delprojekt
        Project project = projectService.getById(projectId);
        Subproject subproject = subprojectService.getById(subprojectId);

        // Tjekker gyldige relationer
        if (project == null || subproject == null || subproject.getProjectId() != projectId) {
            return "redirect:/project/" + projectId;
        }

        // Tjekker adgang
        if (!hasProjectAccess(projectId, current)) {
            return "error/no-permission";
        }

        model.addAttribute("project", project);
        model.addAttribute("subproject", subproject);
        model.addAttribute("tasks",
                taskService.getTasksBySubprojectId(subprojectId));

        model.addAttribute("canEditTasks",
                canEditTasks(projectId, current));

        return "task/task-list";
    }

    // Viser formular til oprettelse
    @GetMapping("/create")
    public String createForm(@PathVariable int projectId,
                             @RequestParam int subprojectId,
                             Model model,
                             HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Henter projekt og delprojekt
        Project project = projectService.getById(projectId);
        Subproject subproject = subprojectService.getById(subprojectId);

        // Tjekker gyldige relationer
        if (project == null || subproject == null || subproject.getProjectId() != projectId) {
            return "redirect:/project/" + projectId;
        }

        // Tjekker adgang
        if (!hasProjectAccess(projectId, current)) {
            return "error/no-permission";
        }

        // Tjekker redigeringsrettighed
        if (!canEditTasks(projectId, current)) {
            return "error/no-permission";
        }

        // Opretter tom task
        Task task = new Task();
        task.setSubprojectId(subprojectId);
        task.setStatus(TaskStatus.NOT_STARTED);

        model.addAttribute("project", project);
        model.addAttribute("subproject", subproject);
        model.addAttribute("task", task);
        model.addAttribute("users", userService.getAll());

        return "task/task-create";
    }

    // Opretter task
    @PostMapping("/create")
    public String create(@PathVariable int projectId,
                         @ModelAttribute Task task,
                         HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Tjekker gyldigt delprojekt
        if (!isValidProjectSubproject(projectId, task.getSubprojectId())) {
            return "redirect:/project/" + projectId;
        }

        // Tjekker adgang
        if (!hasProjectAccess(projectId, current)) {
            return "error/no-permission";
        }

        // Tjekker redigeringsrettighed
        if (!canEditTasks(projectId, current)) {
            return "error/no-permission";
        }

        taskService.create(task);

        return "redirect:/project/" + projectId +
                "/tasks?subprojectId=" + task.getSubprojectId();
    }

    // Viser redigeringsformular
    @GetMapping("/edit/{taskId}")
    public String editForm(@PathVariable int projectId,
                           @PathVariable int taskId,
                           Model model,
                           HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Henter task
        Task task = taskService.getById(taskId);
        if (task == null) return "redirect:/project/" + projectId;

        // Henter delprojekt
        Subproject subproject = subprojectService.getById(task.getSubprojectId());
        if (subproject == null || subproject.getProjectId() != projectId) {
            return "redirect:/project/" + projectId;
        }

        // Tjekker adgang
        if (!hasProjectAccess(projectId, current)) {
            return "error/no-permission";
        }

        // Tjekker redigeringsrettighed
        if (!canEditTasks(projectId, current)) {
            return "error/no-permission";
        }

        model.addAttribute("project", projectService.getById(projectId));
        model.addAttribute("task", task);
        model.addAttribute("subproject", subproject);
        model.addAttribute("users", userService.getAll());
        model.addAttribute("taskStatusList", TaskStatus.values());
        model.addAttribute("subtasks", subtaskService.getByTaskId(taskId));

        return "task/task-edit";
    }

    // Opdaterer task
    @PostMapping("/update/{taskId}")
    public String update(@PathVariable int projectId,
                         @PathVariable int taskId,
                         @ModelAttribute Task task,
                         HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Tjekker gyldigt delprojekt
        if (!isValidProjectSubproject(projectId, task.getSubprojectId())) {
            return "redirect:/project/" + projectId;
        }

        // Kontrollerer adgang
        if (!hasProjectAccess(projectId, current)) {
            return "error/no-permission";
        }

        // Kontrollerer redigeringsrettighed
        if (!canEditTasks(projectId, current)) {
            return "error/no-permission";
        }

        task.setId(taskId);
        taskService.update(task);

        return "redirect:/project/" + projectId +
                "/tasks?subprojectId=" + task.getSubprojectId();
    }

    // Sletter task
    @PostMapping("/delete/{taskId}")
    public String delete(@PathVariable int projectId,
                         @PathVariable int taskId,
                         @RequestParam int subprojectId,
                         HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Kontrollerer gyldigt delprojekt
        if (!isValidProjectSubproject(projectId, subprojectId)) {
            return "redirect:/project/" + projectId;
        }

        // Kontrollerer adgang
        if (!hasProjectAccess(projectId, current)) {
            return "error/no-permission";
        }

        // Kontrollerer redigeringsrettighed
        if (!canEditTasks(projectId, current)) {
            return "error/no-permission";
        }

        taskService.delete(taskId);

        return "redirect:/project/" + projectId +
                "/tasks?subprojectId=" + subprojectId;
    }
}
