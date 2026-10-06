package com.example.projectcalctool.controller;

import com.example.projectcalctool.model.Project;
import com.example.projectcalctool.model.ProjectRole;
import com.example.projectcalctool.model.ProjectUser;
import com.example.projectcalctool.model.User;
import com.example.projectcalctool.service.ProjectService;
import com.example.projectcalctool.service.ProjectUserService;
import com.example.projectcalctool.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/project")
public class ProjectUserController {

    // JDBC service til projekter
    private final ProjectService projectService;

    // JDBC service til projektbrugere
    private final ProjectUserService projectUserService;

    // JDBC service til brugere
    private final UserService userService;

    // Constructor injection
    public ProjectUserController(ProjectService projectService,
                                 ProjectUserService projectUserService,
                                 UserService userService) {
        this.projectService = projectService;
        this.projectUserService = projectUserService;
        this.userService = userService;
    }

    // ============================================================
    // VIS ADMINISTRATION AF MEDLEMMER
    @GetMapping("/{projectId}/members")
    public String manageMembers(@PathVariable int projectId,
                                HttpSession session,
                                Model model) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Tjekker rettighed til at administrere medlemmer
        if (!canManageMembers(projectId, current)) {
            return "error/no-permission";
        }

        // Henter projekt
        Project project = projectService.getById(projectId);
        if (project == null) return "redirect:/project";

        // Henter brugere tilknyttet projektet
        List<ProjectUser> projectUsers =
                projectUserService.getUsersForProject(projectId);

        // Henter alle brugere
        List<User> allUsers = userService.getAll();

        // Mapper brugere for hurtig opslag
        Map<Integer, User> userMap = new HashMap<>();
        for (User u : allUsers) {
            userMap.put(u.getId(), u);
        }

        model.addAttribute("project", project);
        model.addAttribute("projectUsers", projectUsers);
        model.addAttribute("userMap", userMap);
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("roles", ProjectRole.values());

        return "project/project-members";
    }

    // ============================================================
    // TILFØJ EKSISTERENDE BRUGER
    @PostMapping("/{projectId}/members/add-existing")
    public String addExistingUser(@PathVariable int projectId,
                                  @RequestParam String email,
                                  @RequestParam ProjectRole role,
                                  HttpSession session,
                                  RedirectAttributes ra) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Tjekker rettighed
        if (!canManageMembers(projectId, current)) {
            return "error/no-permission";
        }

        // Finder bruger via email
        User user = userService.getByEmail(email);
        if (user == null) {
            ra.addFlashAttribute("error",
                    "Ingen bruger fundet med email: " + email);
            return "redirect:/project/" + projectId + "/members";
        }

        // Opretter relation mellem bruger og projekt
        ProjectUser pu = new ProjectUser();
        pu.setProjectId(projectId);
        pu.setUserId(user.getId());
        pu.setRole(role);

        // Gemmer relationen i databasen
        projectUserService.addUserToProject(pu);

        return "redirect:/project/" + projectId + "/members";
    }

    // ============================================================
    // OPRET NY BRUGER OG TILFØJ
    @PostMapping("/{projectId}/members/create-and-add")
    public String createUserAndAdd(@PathVariable int projectId,
                                   @RequestParam String username,
                                   @RequestParam String email,
                                   @RequestParam String password,
                                   @RequestParam ProjectRole role,
                                   HttpSession session,
                                   RedirectAttributes ra) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Tjekker rettighed
        if (!canManageMembers(projectId, current)) {
            return "error/no-permission";
        }

        // Tjekker om brugeren allerede findes
        User employee = userService.getByUsername(username);

        if (employee == null) {
            // Opretter ny bruger
            employee = new User();
            employee.setUsername(username);
            employee.setEmail(email);
            employee.setPassword(password);
            employee.setRole("USER");

            userService.create(employee);

            // Henter brugeren igen efter oprettelse
            employee = userService.getByUsername(username);
            if (employee == null) {
                ra.addFlashAttribute("error",
                        "Kunne ikke oprette brugeren");
                return "redirect:/project/" + projectId + "/members";
            }
        }

        // Opretter relation mellem bruger og projekt
        ProjectUser pu = new ProjectUser();
        pu.setProjectId(projectId);
        pu.setUserId(employee.getId());
        pu.setRole(role);

        // Gemmer relationen i databasen
        projectUserService.addUserToProject(pu);

        return "redirect:/project/" + projectId + "/members";
    }

    // FJERN BRUGER
    @PostMapping("/{projectId}/members/{userId}/remove")
    public String removeMember(@PathVariable int projectId,
                               @PathVariable int userId,
                               HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Tjekker rettighed
        if (!canManageMembers(projectId, current)) {
            return "error/no-permission";
        }

        // Fjerner bruger fra projekt
        projectUserService.removeUserFromProject(projectId, userId);

        return "redirect:/project/" + projectId + "/members";
    }

    // HJÆLPER
    private boolean canManageMembers(int projectId, User current) {

        // Global admin eller projekt admin
        return "ADMIN".equals(current.getRole())
                || projectUserService.isProjectAdmin(projectId, current.getId());
    }
}
