package com.example.projectcalctool.controller;

import com.example.projectcalctool.model.CostItem;
import com.example.projectcalctool.model.User;
import com.example.projectcalctool.service.CostItemService;
import com.example.projectcalctool.service.ProjectService;
import com.example.projectcalctool.service.ProjectUserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/projects/{projectId}/costitems")
public class CostItemController {

    // JDBC service til cost items
    private final CostItemService costItemService;

    // JDBC service til projekter
    private final ProjectService projectService;

    // JDBC service til projektbrugere og rettigheder
    private final ProjectUserService projectUserService;

    // Constructor injection
    public CostItemController(CostItemService costItemService,
                              ProjectService projectService,
                              ProjectUserService projectUserService) {
        this.costItemService = costItemService;
        this.projectService = projectService;
        this.projectUserService = projectUserService;
    }

    // Tjekker om brugeren er global administrator
    private boolean isGlobalAdmin(User user) {
        return user != null && "ADMIN".equals(user.getRole());
    }

    // Tjekker om brugeren har adgang til projektet
    private boolean hasAccess(int projectId, User user) {
        return isGlobalAdmin(user)
                || projectUserService.getProjectUser(projectId, user.getId()) != null;
    }

    // Tjekker om brugeren må redigere cost items
    private boolean canEdit(int projectId, User user) {
        return isGlobalAdmin(user)
                || projectUserService.canEditCostitems(projectId, user.getId());
    }

    // Viser liste over cost items for projekt
    @GetMapping
    public String list(@PathVariable int projectId,
                       Model model,
                       HttpSession session) {

        // Henter bruger fra session
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Tjekker adgang
        if (!hasAccess(projectId, current)) {
            return "error/no-permission";
        }

        // Henter data via JDBC services
        model.addAttribute("project", projectService.getById(projectId));
        model.addAttribute("items", costItemService.getByProjectId(projectId));
        model.addAttribute("canEditCostitems", canEdit(projectId, current));

        return "costitem/list";
    }

    // Viser formular til oprettelse af cost item
    @GetMapping("/create")
    public String createForm(@PathVariable int projectId,
                             Model model,
                             HttpSession session) {

        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        // Tjekker både adgang og redigeringsrettighed
        if (!hasAccess(projectId, current) || !canEdit(projectId, current)) {
            return "error/no-permission";
        }

        // Opretter tomt cost item objekt
        CostItem ci = new CostItem();
        ci.setProjectId(projectId);

        model.addAttribute("item", ci);
        model.addAttribute("project", projectService.getById(projectId));

        return "costitem/create";
    }

    // Gemmer nyt cost item
    @PostMapping("/create")
    public String create(@PathVariable int projectId,
                         CostItem item,
                         HttpSession session) {

        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        if (!hasAccess(projectId, current) || !canEdit(projectId, current)) {
            return "error/no-permission";
        }

        // Sætter projekt id og gemmer via JDBC
        item.setProjectId(projectId);
        costItemService.create(item);

        return "redirect:/projects/" + projectId + "/costitems";
    }

    // Viser redigeringsformular
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int projectId,
                           @PathVariable int id,
                           Model model,
                           HttpSession session) {

        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        if (!hasAccess(projectId, current) || !canEdit(projectId, current)) {
            return "error/no-permission";
        }

        // Henter cost item fra databasen
        model.addAttribute("item", costItemService.getById(id));
        model.addAttribute("projectId", projectId);

        return "costitem/edit";
    }

    // Opdaterer eksisterende cost item
    @PostMapping("/edit")
    public String update(@PathVariable int projectId,
                         CostItem item,
                         HttpSession session) {

        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        if (!hasAccess(projectId, current) || !canEdit(projectId, current)) {
            return "error/no-permission";
        }

        // Opdaterer via JDBC service
        costItemService.update(item);

        return "redirect:/projects/" + projectId + "/costitems";
    }

    // Sletter cost item
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable int projectId,
                         @PathVariable int id,
                         HttpSession session) {

        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";

        if (!hasAccess(projectId, current) || !canEdit(projectId, current)) {
            return "error/no-permission";
        }

        // Sletter fra databasen
        costItemService.delete(id);

        return "redirect:/projects/" + projectId + "/costitems";
    }
}
