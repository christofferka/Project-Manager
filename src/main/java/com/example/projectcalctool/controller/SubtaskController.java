package com.example.projectcalctool.controller;

import com.example.projectcalctool.model.Project;
import com.example.projectcalctool.model.Subproject;
import com.example.projectcalctool.model.Subtask;
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
@RequestMapping("/subtasks")
public class SubtaskController {

    // JDBC service til subtasks
    private final SubtaskService subtaskService;

    // JDBC service til tasks
    private final TaskService taskService;

    // JDBC service til brugere
    private final UserService userService;

    // JDBC service til projektbrugere og rettigheder
    private final ProjectUserService projectUserService;

    // JDBC service til delprojekter
    private final SubprojectService subprojectService;

    // JDBC service til projekter
    private final ProjectService projectService;

    // Constructor injection
    public SubtaskController(SubtaskService subtaskService,
                             TaskService taskService,
                             UserService userService,
                             ProjectUserService projectUserService,
                             SubprojectService subprojectService,
                             ProjectService projectService) {

        this.subtaskService = subtaskService;
        this.taskService = taskService;
        this.userService = userService;
        this.projectUserService = projectUserService;
        this.subprojectService = subprojectService;
        this.projectService = projectService;
    }

    // Tjekker om brugeren er global admin
    private boolean isGlobalAdmin(User user) {
        return user != null && "ADMIN".equals(user.getRole());
    }

    // Finder projekt ud fra task
    private Project resolveProjectFromTask(Task task) {
        Subproject sp = subprojectService.getById(task.getSubprojectId());
        return sp == null ? null : projectService.getById(sp.getProjectId());
    }

    // Tjekker adgang til projekt
    private boolean hasProjectAccess(Project project, User user) {
        return isGlobalAdmin(user)
                || projectUserService.getProjectUser(project.getId(), user.getId()) != null;
    }

    // Tjekker redigeringsrettighed for subtasks
    private boolean canEdit(Project project, User user) {
        return isGlobalAdmin(user)
                || projectUserService.canEditSubtasks(project.getId(), user.getId());
    }

    // Viser liste over subtasks
    @GetMapping("/{taskId}")
    public String list(@PathVariable int taskId,
                       Model model,
                       HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Henter task
        Task task = taskService.getById(taskId);
        if (task == null) return "redirect:/project";

        // Finder projekt
        Project project = resolveProjectFromTask(task);
        if (project == null || !hasProjectAccess(project, current)) {
            return "error/no-permission";
        }

        model.addAttribute("task", task);
        model.addAttribute("subproject",
                subprojectService.getById(task.getSubprojectId()));
        model.addAttribute("project", project);
        model.addAttribute("subtasks",
                subtaskService.getByTaskId(taskId));
        model.addAttribute("canEdit",
                canEdit(project, current));

        return "subtask/subtask-list";
    }

    // Viser formular til oprettelse
    @GetMapping("/create/{taskId}")
    public String createForm(@PathVariable int taskId,
                             Model model,
                             HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Henter task
        Task task = taskService.getById(taskId);
        if (task == null) return "redirect:/project";

        // Finder projekt
        Project project = resolveProjectFromTask(task);
        if (project == null || !hasProjectAccess(project, current)) {
            return "error/no-permission";
        }

        // Tjekker redigeringsrettighed
        if (!canEdit(project, current)) {
            return "error/no-permission";
        }

        // Opretter tom subtask
        Subtask subtask = new Subtask();
        subtask.setTaskId(taskId);
        subtask.setStatus(TaskStatus.NOT_STARTED);

        model.addAttribute("subtask", subtask);
        model.addAttribute("task", task);
        model.addAttribute("project", project);
        model.addAttribute("users", userService.getAll());
        model.addAttribute("taskStatusList", TaskStatus.values());

        return "subtask/subtask-create";
    }

    // Opretter subtask
    @PostMapping("/create")
    public String create(@ModelAttribute Subtask subtask,
                         HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Henter task
        Task task = taskService.getById(subtask.getTaskId());
        if (task == null) return "redirect:/project";

        // Finder projekt
        Project project = resolveProjectFromTask(task);
        if (project == null || !hasProjectAccess(project, current)) {
            return "error/no-permission";
        }

        // Tjekker redigeringsrettighed
        if (!canEdit(project, current)) {
            return "error/no-permission";
        }

        subtaskService.create(subtask);
        return "redirect:/subtasks/" + task.getId();
    }

    // Viser redigeringsformular
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id,
                           Model model,
                           HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Henter subtask
        Subtask subtask = subtaskService.getById(id);
        if (subtask == null) return "redirect:/project";

        // Henter task og projekt
        Task task = taskService.getById(subtask.getTaskId());
        Project project = resolveProjectFromTask(task);

        // Tjekker adgang
        if (project == null || !hasProjectAccess(project, current)) {
            return "error/no-permission";
        }

        // Tjekker redigeringsrettighed
        if (!canEdit(project, current)) {
            return "error/no-permission";
        }

        model.addAttribute("subtask", subtask);
        model.addAttribute("task", task);
        model.addAttribute("project", project);
        model.addAttribute("users", userService.getAll());
        model.addAttribute("taskStatusList", TaskStatus.values());

        return "subtask/subtask-edit";
    }

    // Opdaterer subtask
    @PostMapping("/edit")
    public String update(@ModelAttribute Subtask subtask,
                         HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Henter task
        Task task = taskService.getById(subtask.getTaskId());
        if (task == null) return "redirect:/project";

        // Finder projekt
        Project project = resolveProjectFromTask(task);
        if (project == null || !hasProjectAccess(project, current)) {
            return "error/no-permission";
        }

        // Tjekker redigeringsrettighed
        if (!canEdit(project, current)) {
            return "error/no-permission";
        }

        subtaskService.update(subtask);
        return "redirect:/subtasks/" + task.getId();
    }

    // Sletter subtask
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable int id,
                         HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Henter subtask
        Subtask subtask = subtaskService.getById(id);
        if (subtask == null) return "redirect:/project";

        // Henter task og projekt
        Task task = taskService.getById(subtask.getTaskId());
        Project project = resolveProjectFromTask(task);

        // Tjekker adgang
        if (project == null || !hasProjectAccess(project, current)) {
            return "error/no-permission";
        }

        // Tjekker redigeringsrettighed
        if (!canEdit(project, current)) {
            return "error/no-permission";
        }

        subtaskService.delete(id);
        return "redirect:/subtasks/" + task.getId();
    }
}
