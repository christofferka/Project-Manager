package com.example.projectcalctool.controller;

import com.example.projectcalctool.model.User;
import com.example.projectcalctool.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService,
                          PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // Viser login siden
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        // Hvis allerede logget ind: gaa direkte til dashboard
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    // Haandterer login
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletRequest request) {

        // Fejlbesked skal vaere identisk uanset om brugernavn eller password er forkert
        // (undgaar user-enumeration via timing/svartekst).
        final String failure = "redirect:/login?error=true";

        if (username == null || username.isBlank()
                || password == null || password.isEmpty()) {
            return failure;
        }

        User user = userService.getByUsername(username);
        if (user == null) {
            // Kald encoder alligevel saa timing er den samme som ved en gyldig brugerkonto
            passwordEncoder.matches(password, "$2a$10$abcdefghijklmnopqrstuv");
            return failure;
        }

        String stored = user.getPassword();
        boolean looksHashed = stored != null && (
                stored.startsWith("$2a$")
                        || stored.startsWith("$2b$")
                        || stored.startsWith("$2y$"));

        boolean passwordMatches;
        if (looksHashed) {
            passwordMatches = passwordEncoder.matches(password, stored);
        } else {
            // Legacy: plaintext i DB (migreres til hash ved foerste vellykkede login)
            passwordMatches = stored != null && stored.equals(password);
            if (passwordMatches) {
                user.setPassword(password);
                userService.update(user);
            }
        }

        if (!passwordMatches) {
            return failure;
        }

        // Session fixation protection: invalider gammel session og lav en ny.
        // Beskytter mod en angreber der har plantet et session-id i en brugers browser
        // for at kapre sessionen efter login.
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession newSession = request.getSession(true);
        // Gemm ikke selve password-hashen i sessionen
        user.setPassword(null);
        newSession.setAttribute("user", user);

        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model, HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        // isSetupMode styrer om "Opret som admin"-boksen vises paa register-siden.
        // Fjernet i produktion for ikke at tillade vilkaarlige admin-oprettelser.
        model.addAttribute("isSetupMode", userService.isUserTableEmpty());
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           @RequestParam(required = false) String createAdmin) {

        if (username == null || username.isBlank()
                || email == null || email.isBlank()
                || password == null || password.length() < 4) {
            return "redirect:/register?error=invalid";
        }

        if (!password.equals(confirmPassword)) {
            return "redirect:/register?error=password";
        }

        if (userService.getByUsername(username) != null) {
            return "redirect:/register?error=exists";
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);

        // Admin-rolle kan kun saettes hvis bruger-tabellen er tom (foerste opstart).
        // Ellers er alt nye brugere USER som default.
        boolean allowAdminFlag = userService.isUserTableEmpty();
        user.setRole(allowAdminFlag && "true".equals(createAdmin) ? "ADMIN" : "USER");

        userService.create(user);
        return "redirect:/login?registered=true";
    }
}
