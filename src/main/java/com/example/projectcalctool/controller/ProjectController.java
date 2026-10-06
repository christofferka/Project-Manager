package com.example.projectcalctool.controller;

import com.example.projectcalctool.model.Project;
import com.example.projectcalctool.model.ProjectRole;
import com.example.projectcalctool.model.ProjectUser;
import com.example.projectcalctool.model.User;
import com.example.projectcalctool.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/project")
public class ProjectController {

    // JDBC service til projekter
    private final ProjectService projectService;


    // JDBC service til projektbrugere og rettigheder
    private final ProjectUserService projectUserService;

    // JDBC service til beregninger
    private final CalculationService calculationService;

    // Constructor injection
    public ProjectController(ProjectService projectService,
                             ProjectUserService projectUserService,
                             CalculationService calculationService) {

        this.projectService = projectService;
        this.projectUserService = projectUserService;
        this.calculationService = calculationService;
    }

    // ============================================================
    // LISTE OVER PROJEKTER
    @GetMapping
    public String list(Model model, HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Henter projekter brugeren har adgang til
        model.addAttribute("project",
                projectUserService.getProjectsForUser(
                        current.getId(),
                        current.getRole()
                ));

        return "project/project-list";
    }

    // ============================================================
    // VIS OPRET PROJEKT
    @GetMapping("/create")
    public String showCreateForm(HttpSession session) {

        // Tjekker om brugeren er logget ind
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        return "project/project-create";
    }

    // OPRET PROJEKT
    @PostMapping("/create")
    public String createProject(@ModelAttribute Project project,
                                HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Opretter projekt i databasen
        int projectId = projectService.create(project);

        // Opretter relation mellem bruger og projekt
        ProjectUser pu = new ProjectUser();
        pu.setProjectId(projectId);
        pu.setUserId(current.getId());
        pu.setRole(ProjectRole.PROJECT_ADMIN);

        // Giver fulde rettigheder til projektopretteren
        pu.setCanEditProject(true);
        pu.setCanEditSubprojects(true);
        pu.setCanEditTasks(true);
        pu.setCanEditSubtasks(true);
        pu.setCanEditCostitems(true);

        // Gemmer relationen i databasen
        projectUserService.addUserToProject(pu);

        return "redirect:/project/" + projectId;
    }

    // ============================================================
    // PROJEKT OVERBLIK
    @GetMapping("/{id}")
    public String overview(@PathVariable int id,
                           Model model,
                           HttpSession session) {

        // Henter bruger fra session
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        // Henter projekt fra databasen
        Project project = projectService.getById(id);
        if (project == null) return "redirect:/project";

        // Tjekker om brugeren har adgang til projektet
        if (!hasProjectAccess(id, user)) {
            return "error/no-permission";
        }

        // Tjekker om brugeren er projektadministrator
        boolean isProjectAdmin =
                "ADMIN".equals(user.getRole())
                        || projectUserService.isProjectAdmin(id, user.getId());

        model.addAttribute("project", project);
        model.addAttribute("isProjectAdmin", isProjectAdmin);

        return "dashboard/project-overview";
    }

    // ============================================================
    // ROADMAP
    @GetMapping("/{id}/roadmap")
    public String roadmap(@PathVariable int id,
                          Model model,
                          HttpSession session) {

        // Henter bruger fra session
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        // Henter projekt
        Project project = projectService.getById(id);
        if (project == null) return "redirect:/project";

        // Tjekker adgang til projekt
        if (!hasProjectAccess(id, user)) {
            return "error/no-permission";
        }

        model.addAttribute("project", project);
        model.addAttribute("roadmap", projectService.getDefaultRoadmap());

        return "dashboard/roadmap";
    }

    // ============================================================
    // PROJEKT INFO
    @GetMapping("/{id}/info")
    public String info(@PathVariable int id,
                       Model model,
                       HttpSession session) {

        // Henter bruger fra session
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/login";

        // Henter projekt
        Project project = projectService.getById(id);
        if (project == null) return "redirect:/project";

        // Tjekker adgang til projekt
        if (!hasProjectAccess(id, user)) {
            return "error/no-permission";
        }

        model.addAttribute("project", project);
        model.addAttribute("totalCost",
                calculationService.calculateTotalCost(id));

        return "project/project-view";
    }

    // HJÆLPEMETODE
    private boolean hasProjectAccess(int projectId, User user) {

        // Global admin har altid adgang
        if ("ADMIN".equals(user.getRole())) {
            return true;
        }

        // Tjekker om brugeren er tilknyttet projektet
        return projectUserService.isUserInProject(projectId, user.getId());
    }
}