package com.example.projectcalctool.controller;

import com.example.projectcalctool.model.User;
import com.example.projectcalctool.service.AiAdviceService;
import com.example.projectcalctool.service.AiAdviceService.ChatMessage;
import com.example.projectcalctool.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/ai")
public class AiAdviceController {

    // Session-noegle til chat historik
    private static final String HISTORY_KEY = "ai_chat_history";
    // Max antal beskeder vi gemmer (bruger + assistant)
    private static final int MAX_HISTORY = 20;
    // Rate limit: max 30 kald pr. bruger pr. time
    private static final int RATE_LIMIT_PER_HOUR = 30;

    // In-memory rate limit (nulstilles ved restart, fint til demo)
    private static final Map<Integer, Deque<Instant>> callLog = new ConcurrentHashMap<>();

    private final AiAdviceService aiService;
    private final UserService userService;

    public AiAdviceController(AiAdviceService aiService, UserService userService) {
        this.aiService = aiService;
        this.userService = userService;
    }

    public static class ChatRequest {
        public String message;
    }

    public static class ChatResponse {
        public String reply;
        public String error;
        public int remaining;
        public ChatResponse() {}
        public static ChatResponse ok(String reply, int remaining) {
            ChatResponse r = new ChatResponse();
            r.reply = reply;
            r.remaining = remaining;
            return r;
        }
        public static ChatResponse err(String error) {
            ChatResponse r = new ChatResponse();
            r.error = error;
            return r;
        }
    }

    @PostMapping("/chat")
    @ResponseBody
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest req, HttpSession session) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return ResponseEntity.status(401).body(ChatResponse.err("Du skal være logget ind."));
        }

        // Hent frisk bruger (session kan vaere gammel, AI-key kan vaere opdateret)
        User user = userService.getById(sessionUser.getId());
        if (user == null) {
            return ResponseEntity.status(401).body(ChatResponse.err("Brugerprofil kunne ikke findes."));
        }

        if (!user.hasAiConfigured()) {
            return ResponseEntity.status(400).body(ChatResponse.err(
                    "Du har ikke tilføjet en AI-nøgle endnu. Gå til Indstillinger > AI."));
        }

        if (req.message == null || req.message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ChatResponse.err("Spørgsmål må ikke være tomt."));
        }
        if (req.message.length() > 2000) {
            return ResponseEntity.badRequest().body(ChatResponse.err("Spørgsmål må højst være 2000 tegn."));
        }

        // Rate limit
        int remaining = recordCall(user.getId());
        if (remaining < 0) {
            return ResponseEntity.status(429).body(ChatResponse.err(
                    "Du har brugt din time-kvote (" + RATE_LIMIT_PER_HOUR + " beskeder). Prøv igen om lidt."));
        }

        @SuppressWarnings("unchecked")
        List<ChatMessage> history = (List<ChatMessage>) session.getAttribute(HISTORY_KEY);
        if (history == null) history = new ArrayList<>();

        try {
            String reply = aiService.ask(user, history, req.message);

            // Opdater historik
            history.add(new ChatMessage("user", req.message));
            history.add(new ChatMessage("assistant", reply));
            // Trim til MAX_HISTORY seneste
            while (history.size() > MAX_HISTORY) history.remove(0);
            session.setAttribute(HISTORY_KEY, history);

            return ResponseEntity.ok(ChatResponse.ok(reply, remaining));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(ChatResponse.err(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ChatResponse.err(
                    "Noget gik galt da jeg kontaktede AI-tjenesten. " +
                    "Tjek at din API-nøgle er gyldig."));
        }
    }

    @PostMapping("/reset")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> reset(HttpSession session) {
        session.removeAttribute(HISTORY_KEY);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    private int recordCall(int userId) {
        Instant now = Instant.now();
        Instant oneHourAgo = now.minusSeconds(3600);

        Deque<Instant> log = callLog.computeIfAbsent(userId, k -> new ArrayDeque<>());
        synchronized (log) {
            // Fjern gamle entries
            while (!log.isEmpty() && log.peekFirst().isBefore(oneHourAgo)) {
                log.pollFirst();
            }
            if (log.size() >= RATE_LIMIT_PER_HOUR) {
                return -1;
            }
            log.addLast(now);
            return RATE_LIMIT_PER_HOUR - log.size();
        }
    }
}
