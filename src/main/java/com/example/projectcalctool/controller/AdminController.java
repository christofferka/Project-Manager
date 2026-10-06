package com.example.projectcalctool.controller;

import com.example.projectcalctool.model.User;
import com.example.projectcalctool.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // Service til at hente og gemme brugere
    private final UserService userService;

    // Constructor injection
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    // Viser liste med alle brugere
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAll());
        return "admin/user-list";
    }

    // Viser formular til at redigere en bruger
    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable int id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        return "admin/user-edit";
    }

    // Opdaterer brugerens rolle
    @PostMapping("/users/{id}/role")
    public String updateUserRole(@PathVariable int id,
                                 @RequestParam String role) {

        User user = userService.getById(id);
        user.setRole(role);
        userService.update(user);

        return "redirect:/admin/users";
    }

    // Viser formular til at oprette ny bruger
    @GetMapping("/users/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/user-create";
    }

    // Opretter ny bruger ud fra formularen
    @PostMapping("/users/create")
    public String createUser(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String password,
                             @RequestParam(defaultValue = "USER") String role) {

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);

        userService.create(user);

        return "redirect:/admin/users";
    }
}
