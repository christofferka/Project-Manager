package com.example.projectcalctool.controller;

import com.example.projectcalctool.model.User;
import com.example.projectcalctool.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // Alle admin-endpoints kraever ADMIN-rolle.
    // Hvis ikke logget ind: /login. Hvis logget ind som ikke-admin: 403-side.
    private String guard(HttpSession session) {
        User current = (User) session.getAttribute("user");
        if (current == null) return "redirect:/login";
        if (!"ADMIN".equals(current.getRole())) return "error/no-permission";
        return null;
    }

    @GetMapping("/users")
    public String listUsers(Model model, HttpSession session) {
        String deny = guard(session);
        if (deny != null) return deny;

        model.addAttribute("users", userService.getAll());
        return "admin/user-list";
    }

    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable int id, Model model, HttpSession session) {
        String deny = guard(session);
        if (deny != null) return deny;

        User user = userService.getById(id);
        if (user == null) return "redirect:/admin/users";
        model.addAttribute("user", user);
        return "admin/user-edit";
    }

    @PostMapping("/users/{id}/role")
    public String updateUserRole(@PathVariable int id,
                                 @RequestParam String role,
                                 HttpSession session) {
        String deny = guard(session);
        if (deny != null) return deny;

        // Hvidliste af roller for at undgaa at man kan sende vilkaarlige strenge
        if (!("ADMIN".equals(role) || "EDITOR".equals(role) || "USER".equals(role))) {
            return "redirect:/admin/users?error=role";
        }

        User user = userService.getById(id);
        if (user == null) return "redirect:/admin/users";

        // En admin maa ikke degradere sig selv (ellers kan vi laase systemet ude af admin-rollen)
        User current = (User) session.getAttribute("user");
        if (current != null && current.getId() == id && !"ADMIN".equals(role)) {
            return "redirect:/admin/users?error=self";
        }

        user.setRole(role);
        userService.update(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/create")
    public String createUserForm(Model model, HttpSession session) {
        String deny = guard(session);
        if (deny != null) return deny;

        model.addAttribute("user", new User());
        return "admin/user-create";
    }

    @PostMapping("/users/create")
    public String createUser(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam(defaultValue = "USER") String role,
                             HttpSession session) {
        String deny = guard(session);
        if (deny != null) return deny;

        if (username == null || username.isBlank()
                || email == null || email.isBlank()
                || password == null || password.length() < 4) {
            return "redirect:/admin/users/create?error=invalid";
        }

        if (userService.getByUsername(username) != null) {
            return "redirect:/admin/users/create?error=exists";
        }

        String safeRole = ("ADMIN".equals(role) || "EDITOR".equals(role)) ? role : "USER";

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(safeRole);

        userService.create(user);
        return "redirect:/admin/users";
    }
}
