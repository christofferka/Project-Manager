package com.example.projectcalctool.controller;

import com.example.projectcalctool.model.User;
import com.example.projectcalctool.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    // JDBC baseret service til brugere
    private final UserService userService;

    // Bruges til at tjekke krypterede passwords
    private final PasswordEncoder passwordEncoder;

    // Constructor injection af services
    public AuthController(UserService userService,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // Viser login siden
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Håndterer login formular
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session) {

        // Henter bruger fra databasen via JDBC
        User user = userService.getByUsername(username);

        // Hvis brugeren ikke findes
        if (user == null) {
            return "redirect:/login?error=true";
        }

        // Henter gemt password
        String storedPassword = user.getPassword();

        // Tjekker om password ligner et bcrypt hash
        boolean looksHashed =
                storedPassword.startsWith("$2a$")
                        || storedPassword.startsWith("$2b$")
                        || storedPassword.startsWith("$2y$");

        boolean passwordMatches;

        if (looksHashed) {
            // Sammenligner rå password med hash
            passwordMatches = passwordEncoder.matches(password, storedPassword);
        } else {
            // Sammenligner direkte hvis password ikke er krypteret
            passwordMatches = storedPassword.equals(password);

            // Opdaterer password hvis det matcher
            if (passwordMatches) {
                user.setPassword(password);
                userService.update(user);
            }
        }

        // Hvis password er forkert
        if (!passwordMatches) {
            return "redirect:/login?error=true";
        }

        // Gemmer bruger i session
        session.setAttribute("user", user);

        // Går til dashboard
        return "redirect:/dashboard";
    }

    // Logger brugeren ud
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // Viser registreringsside
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("isSetupMode", true);
        return "register";
    }

    // Håndterer registrering af ny bruger
    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           @RequestParam(required = false) String createAdmin) {

        // Tjekker at passwords matcher
        if (!password.equals(confirmPassword)) {
            return "redirect:/register?error=password";
        }

        // Tjekker om brugernavn allerede findes
        if (userService.getByUsername(username) != null) {
            return "redirect:/register?error=exists";
        }

        // Opretter ny bruger
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        // Sætter rolle baseret på formular valg
        user.setRole("true".equals(createAdmin) ? "ADMIN" : "USER");

        // Gemmer bruger i databasen via JDBC
        userService.create(user);

        // Sender videre til login
        return "redirect:/login?registered=true";
    }
}
