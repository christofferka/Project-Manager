package com.example.projectcalctool.controller;

import com.example.projectcalctool.service.ProjectService;
import com.example.projectcalctool.service.SubprojectService;
import com.example.projectcalctool.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/project/view")
public class ProjectViewController {

    // Service til projekter via JDBC
    private final ProjectService projectService;

    // Service til delprojekter via JDBC
    private final SubprojectService subprojectService;

    // Service til tasks via JDBC
    private final TaskService taskService;

    // Constructor injection
    public ProjectViewController(ProjectService projectService,
                                 SubprojectService subprojectService,
                                 TaskService taskService) {
        this.projectService = projectService;
        this.subprojectService = subprojectService;
        this.taskService = taskService;
    }

    // Viser projektets hovedoverblik
    @GetMapping("/{id}")
    public String projectOverview(@PathVariable int id, Model model) {

        // Henter projekt fra databasen
        model.addAttribute("project", projectService.getById(id));

        return "dashboard/project-overview";
    }

    // Viser projektets roadmap
    @GetMapping("/{id}/roadmap")
    public String projectRoadmap(@PathVariable int id, Model model) {

        // Henter projekt og standard roadmap
        model.addAttribute("project", projectService.getById(id));
        model.addAttribute("roadmap", projectService.getDefaultRoadmap());

        return "dashboard/roadmap";
    }

    // Viser alle delprojekter for projektet
    @GetMapping("/{id}/subprojects")
    public String projectSubprojects(@PathVariable int id, Model model) {

        // Henter projekt og tilhørende delprojekter
        model.addAttribute("project", projectService.getById(id));
        model.addAttribute("subprojects",
                subprojectService.getByProjectId(id));

        return "subproject/subproject-list";
    }

    // Viser alle tasks for projektet
    @GetMapping("/{id}/tasks")
    public String projectTasks(@PathVariable int id, Model model) {

        // Henter projekt og tilhørende tasks
        model.addAttribute("project", projectService.getById(id));
        model.addAttribute("tasks",
                taskService.getByProjectId(id));

        return "task/task-list";
    }

    // Viser detaljeret projektvisning
    @GetMapping("/{id}/info")
    public String projectView(@PathVariable int id, Model model) {

        // Samler alle relevante data til detaljeret visning
        model.addAttribute("project", projectService.getById(id));
        model.addAttribute("subprojects",
                subprojectService.getByProjectId(id));
        model.addAttribute("tasks",
                taskService.getByProjectId(id));

        return "dashboard/project-view";
    }
}
