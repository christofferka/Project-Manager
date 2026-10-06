package com.example.projectcalctool.controller;

import com.example.projectcalctool.model.User;
import com.example.projectcalctool.service.ProjectService;
import com.example.projectcalctool.service.ProjectUserService;
import com.example.projectcalctool.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    // Service til projekter via JDBC
    private final ProjectService projectService;

    // Service til brugere via JDBC
    private final UserService userService;

    // Service der finder projekter for en bruger via JDBC
    private final ProjectUserService projectUserService;

    // Constructor injection
    public DashboardController(ProjectService projectService,
                               UserService userService,
                               ProjectUserService projectUserService) {

        this.projectService = projectService;
        this.userService = userService;
        this.projectUserService = projectUserService;
    }

    // Viser hoved dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {

        // Henter nuværende bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Gør bruger tilgængelig i view
        model.addAttribute("user", current);

        // Henter projekter brugeren har adgang til
        var projects = projectUserService.getProjectsForUser(
                current.getId(),
                current.getRole()
        );

        // Beregner samlede estimerede timer for hvert projekt
        for (var p : projects) {
            double total = projectService.getTotalTaskHours(p.getId());
            p.setEstimatedHours(total);
        }

        // Sender projekter til view
        model.addAttribute("projects", projects);

        return "dashboard/dashboard";
    }

    // Viser projektoverblik i dashboard
    @GetMapping("/dashboard/projects")
    public String dashboardProjects(Model model, HttpSession session) {

        // Tjekker login
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        model.addAttribute("user", current);

        return "dashboard/dashboard-projects";
    }

    // Viser brugeroversigt for admin
    @GetMapping("/dashboard/members")
    public String dashboardMembers(Model model, HttpSession session) {

        // Tjekker login
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Kun admin har adgang
        if (!"ADMIN".equals(current.getRole())) {
            return "error/no-permission";
        }

        // Henter alle brugere fra databasen
        model.addAttribute("members", userService.getAll());
        model.addAttribute("user", current);

        return "dashboard/dashboard-members";
    }

    // Viser roadmap siden
    @GetMapping("/dashboard/roadmap")
    public String dashboardRoadmap(HttpSession session, Model model) {

        // Tjekker login
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        model.addAttribute("user", current);

        return "dashboard/roadmap";
    }
}
