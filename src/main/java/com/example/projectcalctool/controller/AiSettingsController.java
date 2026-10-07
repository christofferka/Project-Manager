package com.example.projectcalctool.controller;

import com.example.projectcalctool.model.User;
import com.example.projectcalctool.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/settings/ai")
public class AiSettingsController {

    private final UserRepository userRepository;

    public AiSettingsController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String show(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/login";

        // Hent frisk fra DB (session kan vaere forældet)
        User fresh = userRepository.findById(sessionUser.getId());
        if (fresh == null) return "redirect:/login";

        model.addAttribute("user", fresh);
        return "settings/ai";
    }

    @PostMapping
    public String save(@RequestParam String provider,
                       @RequestParam(required = false) String apiKey,
                       @RequestParam(required = false) String model,
                       HttpSession session) {

        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) return "redirect:/login";

        // Hvidliste af udbydere
        if (!("anthropic".equals(provider) || "openai".equals(provider) || "off".equals(provider))) {
            return "redirect:/settings/ai?error=provider";
        }

        if ("off".equals(provider)) {
            userRepository.updateAiSettings(sessionUser.getId(), null, null, null);
            return "redirect:/settings/ai?saved=1";
        }

        if (apiKey == null || apiKey.isBlank()) {
            // Behold eksisterende key hvis bruger kun opdaterer provider/model
            User existing = userRepository.findById(sessionUser.getId());
            if (existing == null || existing.getAiApiKey() == null || existing.getAiApiKey().isBlank()) {
                return "redirect:/settings/ai?error=key";
            }
            apiKey = existing.getAiApiKey();
        }

        String safeModel = (model == null || model.isBlank()) ? null : model.trim();
        userRepository.updateAiSettings(sessionUser.getId(), provider, apiKey.trim(), safeModel);

        return "redirect:/settings/ai?saved=1";
    }
}
