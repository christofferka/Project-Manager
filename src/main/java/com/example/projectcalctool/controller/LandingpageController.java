package com.example.projectcalctool.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")   // Alle requests til root håndteres her
public class LandingpageController {

    @GetMapping
    public String landing() {

        // Log til konsollen så man kan se at controlleren rammes
        System.out.println("### LANDING CONTROLLER KØRER ###");

        // Returnerer landing.html view
        return "landing";
    }
}
