package com.example.projectcalctool.controller;

import com.example.projectcalctool.model.Subproject;
import com.example.projectcalctool.model.User;
import com.example.projectcalctool.service.ProjectService;
import com.example.projectcalctool.service.ProjectUserService;
import com.example.projectcalctool.service.SubprojectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/project/{projectId}/subprojects")
public class SubprojectController {

    // JDBC service til delprojekter
    private final SubprojectService subprojectService;

    // JDBC service til projekter
    private final ProjectService projectService;

    // JDBC service til projektbrugere og rettigheder
    private final ProjectUserService projectUserService;

    // Constructor injection
    public SubprojectController(SubprojectService subprojectService,
                                ProjectService projectService,
                                ProjectUserService projectUserService) {
        this.subprojectService = subprojectService;
        this.projectService = projectService;
        this.projectUserService = projectUserService;
    }

    // Tjekker om brugeren er global admin
    private boolean isGlobalAdmin(User user) {
        return user != null && "ADMIN".equals(user.getRole());
    }

    // Tjekker adgang til projektet
    private boolean hasProjectAccess(int projectId, User current) {
        return isGlobalAdmin(current)
                || projectUserService.getProjectUser(projectId, current.getId()) != null;
    }

    // Tjekker om brugeren må redigere delprojekter
    private boolean canEditSubprojects(int projectId, User current) {
        return isGlobalAdmin(current)
                || projectUserService.canEditSubprojects(projectId, current.getId());
    }

    // Viser liste over delprojekter
    @GetMapping
    public String list(@PathVariable int projectId,
                       Model model,
                       HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Henter projekt
        var project = projectService.getById(projectId);
        if (project == null) return "redirect:/project";

        // Tjekker adgang
        if (!hasProjectAccess(projectId, current)) {
            return "error/no-permission";
        }

        model.addAttribute("project", project);
        model.addAttribute("subprojects",
                subprojectService.getByProjectId(projectId));

        model.addAttribute("canEditSubprojects",
                canEditSubprojects(projectId, current));

        return "subproject/subproject-list";
    }

    // Viser formular til oprettelse
    @GetMapping("/create")
    public String createForm(@PathVariable int projectId,
                             Model model,
                             HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Henter projekt
        var project = projectService.getById(projectId);
        if (project == null) return "redirect:/project";

        // Tjekker adgang
        if (!hasProjectAccess(projectId, current)) {
            return "error/no-permission";
        }

        // Tjekker redigeringsrettighed
        if (!canEditSubprojects(projectId, current)) {
            return "error/no-permission";
        }

        // Opretter tomt delprojekt
        Subproject subproject = new Subproject();
        subproject.setProjectId(projectId);

        model.addAttribute("subproject", subproject);
        model.addAttribute("project", project);

        return "subproject/subproject-create";
    }

    // Opretter delprojekt
    @PostMapping("/create")
    public String create(@PathVariable int projectId,
                         @ModelAttribute Subproject subproject,
                         HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Henter projekt
        var project = projectService.getById(projectId);
        if (project == null) return "redirect:/project";

        // Tjekker adgang
        if (!hasProjectAccess(projectId, current)) {
            return "error/no-permission";
        }

        // Tjekker redigeringsrettighed
        if (!canEditSubprojects(projectId, current)) {
            return "error/no-permission";
        }

        subproject.setProjectId(projectId);
        subprojectService.create(subproject);

        return "redirect:/project/" + projectId + "/subprojects";
    }

    // Viser redigeringsformular
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int projectId,
                           @PathVariable int id,
                           Model model,
                           HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Henter projekt
        var project = projectService.getById(projectId);
        if (project == null) return "redirect:/project";

        // Tjekker adgang
        if (!hasProjectAccess(projectId, current)) {
            return "error/no-permission";
        }

        // Tjekker redigeringsrettighed
        if (!canEditSubprojects(projectId, current)) {
            return "error/no-permission";
        }

        // Henter delprojekt
        Subproject subproject = subprojectService.getById(id);
        if (subproject == null) {
            return "redirect:/project/" + projectId + "/subprojects";
        }

        // Sikrer at delprojekt tilhører projektet
        if (subproject.getProjectId() != projectId) {
            return "error/no-permission";
        }

        model.addAttribute("subproject", subproject);
        model.addAttribute("project", project);

        return "subproject/subproject-edit";
    }

    // Opdaterer delprojekt
    @PostMapping("/edit")
    public String update(@PathVariable int projectId,
                         @ModelAttribute Subproject subproject,
                         HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Henter projekt
        var project = projectService.getById(projectId);
        if (project == null) return "redirect:/project";

        // Tjekker adgang
        if (!hasProjectAccess(projectId, current)) {
            return "error/no-permission";
        }

        // Tjekker redigeringsrettighed
        if (!canEditSubprojects(projectId, current)) {
            return "error/no-permission";
        }

        subproject.setProjectId(projectId);
        subprojectService.update(subproject);

        return "redirect:/project/" + projectId + "/subprojects";
    }

    // Sletter delprojekt
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable int projectId,
                         @PathVariable int id,
                         HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Henter projekt
        var project = projectService.getById(projectId);
        if (project == null) return "redirect:/project";

        // Tjekker adgang
        if (!hasProjectAccess(projectId, current)) {
            return "error/no-permission";
        }

        // Tjekker redigeringsrettighed
        if (!canEditSubprojects(projectId, current)) {
            return "error/no-permission";
        }

        // Henter delprojekt
        Subproject subproject = subprojectService.getById(id);
        if (subproject == null) {
            return "redirect:/project/" + projectId + "/subprojects";
        }

        // Sikrer at delprojekt tilhører projektet
        if (subproject.getProjectId() != projectId) {
            return "error/no-permission";
        }

        subprojectService.delete(id);

        return "redirect:/project/" + projectId + "/subprojects";
    }
}
