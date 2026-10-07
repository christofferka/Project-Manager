package com.example.projectcalctool.service;

import com.example.projectcalctool.model.*;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

/**
 * Rådgiver-service.
 * Samler brugerens projektkontekst og kalder brugerens valgte LLM-udbyder.
 * Nøgle og udbyder kommer fra brugerens egen profil (BYO key).
 */
@Service
public class AiAdviceService {

    private static final int MAX_PROJECTS_IN_CONTEXT = 10;
    private static final int MAX_SUBPROJECTS_PER_PROJECT = 10;
    private static final int MAX_TOKENS_RESPONSE = 1024;

    private final ProjectService projectService;
    private final SubprojectService subprojectService;
    private final TaskService taskService;
    private final ProjectUserService projectUserService;
    private final UserService userService;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public AiAdviceService(ProjectService projectService,
                           SubprojectService subprojectService,
                           TaskService taskService,
                           ProjectUserService projectUserService,
                           UserService userService) {
        this.projectService = projectService;
        this.subprojectService = subprojectService;
        this.taskService = taskService;
        this.projectUserService = projectUserService;
        this.userService = userService;
    }

    public static class ChatMessage {
        public String role; // "user" eller "assistant"
        public String content;
        public ChatMessage() {}
        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    /**
     * Kalder LLM og returnerer svaret. Smider IllegalStateException hvis
     * brugeren ikke har sat AI op, eller RuntimeException ved API-fejl.
     */
    public String ask(User user, List<ChatMessage> history, String question) {
        if (user == null || !user.hasAiConfigured()) {
            throw new IllegalStateException("Brugeren har ikke tilføjet en AI-nøgle endnu.");
        }

        String systemPrompt = buildSystemPrompt(user);

        List<ChatMessage> messages = new ArrayList<>();
        if (history != null) messages.addAll(history);
        messages.add(new ChatMessage("user", question));

        String provider = user.getAiProvider();
        if ("anthropic".equalsIgnoreCase(provider)) {
            return callAnthropic(user, systemPrompt, messages);
        } else if ("openai".equalsIgnoreCase(provider)) {
            return callOpenAi(user, systemPrompt, messages);
        } else {
            throw new IllegalStateException("Ukendt AI-udbyder: " + provider);
        }
    }

    // =============================================================
    // KONTEKST
    // =============================================================

    private String buildSystemPrompt(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("Du er en erfaren projektrådgiver i et dansk projektstyringsværktøj. ")
          .append("Du hjælper brugere med Scrum, user stories, estimering, risikoanalyse, kravhåndtering, retrospektiver og generel projektudvikling. ")
          .append("Svar altid på dansk. Vær konkret, kortfattet og brug punktopstillinger når det passer. ")
          .append("Henvis gerne til brugerens egne projekter når det er relevant. ")
          .append("Hvis brugeren spørger om noget der ikke er projektrelateret, så minder du venligt om at din rolle er at hjælpe med projekter.\n\n");

        sb.append("## Nuværende bruger\n");
        sb.append("Navn: ").append(user.getUsername()).append("\n");
        sb.append("Rolle: ").append(user.getRole()).append("\n");
        if (user.getEmail() != null) sb.append("Email: ").append(user.getEmail()).append("\n");
        sb.append("\n");

        List<Project> projects = projectUserService.getProjectsForUser(user.getId(), user.getRole());
        if (projects == null || projects.isEmpty()) {
            sb.append("## Projekter\nBrugeren har ikke adgang til nogen projekter endnu.\n");
            return sb.toString();
        }

        sb.append("## Projekter brugeren har adgang til\n");
        int count = 0;
        for (Project p : projects) {
            if (count++ >= MAX_PROJECTS_IN_CONTEXT) {
                sb.append("...og flere der er udeladt af hensyn til længden.\n");
                break;
            }
            sb.append("### ").append(p.getName()).append("\n");
            if (p.getCustomerName() != null) sb.append("Kunde: ").append(p.getCustomerName()).append("\n");
            if (p.getStartDate() != null) sb.append("Start: ").append(p.getStartDate()).append("\n");
            if (p.getEndDate() != null) sb.append("Slut: ").append(p.getEndDate()).append("\n");
            if (p.getEstimatedHours() != null) sb.append("Estimerede timer: ").append(p.getEstimatedHours()).append("\n");
            if (p.getHourlyRate() != null) sb.append("Timepris: ").append(p.getHourlyRate()).append(" kr\n");

            // Delprojekter
            List<Subproject> subs = subprojectService.getByProjectId(p.getId());
            if (subs != null && !subs.isEmpty()) {
                sb.append("Delprojekter:\n");
                int si = 0;
                for (Subproject s : subs) {
                    if (si++ >= MAX_SUBPROJECTS_PER_PROJECT) break;
                    sb.append("  - ").append(s.getName())
                      .append(" (fremdrift: ").append(s.getProgressPercent()).append("%");
                    if (s.getDescription() != null && !s.getDescription().isBlank()) {
                        sb.append(", ").append(s.getDescription());
                    }
                    sb.append(")\n");
                }
            }

            // Medlemmer
            List<ProjectUser> members = projectUserService.getUsersForProject(p.getId());
            if (members != null && !members.isEmpty()) {
                sb.append("Medlemmer:\n");
                for (ProjectUser pu : members) {
                    User m = userService.getById(pu.getUserId());
                    if (m == null) continue;
                    sb.append("  - ").append(m.getUsername())
                      .append(" (").append(pu.getRole()).append(")\n");
                }
            }

            // Opgaver (sum)
            try {
                List<Task> tasks = taskService.getByProjectId(p.getId());
                if (tasks != null && !tasks.isEmpty()) {
                    long notStarted = tasks.stream().filter(t -> t.getStatus() == TaskStatus.NOT_STARTED).count();
                    long inProgress = tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
                    long done = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
                    sb.append("Opgavestatus: ").append(tasks.size()).append(" i alt (")
                      .append(notStarted).append(" ikke startet, ")
                      .append(inProgress).append(" i gang, ")
                      .append(done).append(" færdige)\n");
                }
            } catch (Exception ignored) {}

            sb.append("\n");
        }

        return sb.toString();
    }

    // =============================================================
    // ANTHROPIC
    // =============================================================

    private String callAnthropic(User user, String systemPrompt, List<ChatMessage> messages) {
        String model = (user.getAiModel() != null && !user.getAiModel().isBlank())
                ? user.getAiModel()
                : "claude-haiku-4-5-20251001";

        StringBuilder messagesJson = new StringBuilder("[");
        for (int i = 0; i < messages.size(); i++) {
            ChatMessage m = messages.get(i);
            if (i > 0) messagesJson.append(",");
            messagesJson.append("{\"role\":\"").append(m.role)
                        .append("\",\"content\":").append(toJsonString(m.content)).append("}");
        }
        messagesJson.append("]");

        String body = "{"
                + "\"model\":\"" + model + "\","
                + "\"max_tokens\":" + MAX_TOKENS_RESPONSE + ","
                + "\"system\":" + toJsonString(systemPrompt) + ","
                + "\"messages\":" + messagesJson
                + "}";

        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.anthropic.com/v1/messages"))
                    .header("x-api-key", user.getAiApiKey())
                    .header("anthropic-version", "2023-06-01")
                    .header("content-type", "application/json")
                    .timeout(Duration.ofSeconds(60))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 400) {
                throw new RuntimeException("Anthropic API fejl " + resp.statusCode() + ": " + resp.body());
            }
            return extractAnthropicText(resp.body());
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke kontakte Anthropic: " + e.getMessage(), e);
        }
    }

    private String extractAnthropicText(String json) {
        // Primitiv JSON-parse. Vi leder efter "text":"..." under content array.
        int contentIdx = json.indexOf("\"content\"");
        if (contentIdx < 0) return "(tomt svar)";
        int textIdx = json.indexOf("\"text\"", contentIdx);
        if (textIdx < 0) return "(tomt svar)";
        int colon = json.indexOf(':', textIdx);
        int start = json.indexOf('"', colon + 1);
        if (start < 0) return "(tomt svar)";
        return readJsonString(json, start);
    }

    // =============================================================
    // OPENAI
    // =============================================================

    private String callOpenAi(User user, String systemPrompt, List<ChatMessage> messages) {
        String model = (user.getAiModel() != null && !user.getAiModel().isBlank())
                ? user.getAiModel()
                : "gpt-4o-mini";

        StringBuilder messagesJson = new StringBuilder("[");
        messagesJson.append("{\"role\":\"system\",\"content\":").append(toJsonString(systemPrompt)).append("}");
        for (ChatMessage m : messages) {
            messagesJson.append(",{\"role\":\"").append(m.role)
                        .append("\",\"content\":").append(toJsonString(m.content)).append("}");
        }
        messagesJson.append("]");

        String body = "{"
                + "\"model\":\"" + model + "\","
                + "\"max_tokens\":" + MAX_TOKENS_RESPONSE + ","
                + "\"messages\":" + messagesJson
                + "}";

        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Authorization", "Bearer " + user.getAiApiKey())
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(60))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 400) {
                throw new RuntimeException("OpenAI API fejl " + resp.statusCode() + ": " + resp.body());
            }
            return extractOpenAiText(resp.body());
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke kontakte OpenAI: " + e.getMessage(), e);
        }
    }

    private String extractOpenAiText(String json) {
        int msgIdx = json.indexOf("\"message\"");
        if (msgIdx < 0) return "(tomt svar)";
        int contentIdx = json.indexOf("\"content\"", msgIdx);
        if (contentIdx < 0) return "(tomt svar)";
        int colon = json.indexOf(':', contentIdx);
        int start = json.indexOf('"', colon + 1);
        if (start < 0) return "(tomt svar)";
        return readJsonString(json, start);
    }

    // =============================================================
    // JSON helpers (minimum nødvendigt for ikke at pulle ind biblioteker)
    // =============================================================

    private String toJsonString(String s) {
        if (s == null) return "\"\"";
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    // Starten (openingQuoteIdx) peger paa det ' " ' der aabner strengen.
    private String readJsonString(String s, int openingQuoteIdx) {
        StringBuilder sb = new StringBuilder();
        int i = openingQuoteIdx + 1;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char n = s.charAt(i + 1);
                switch (n) {
                    case '"':  sb.append('"'); i += 2; break;
                    case '\\': sb.append('\\'); i += 2; break;
                    case 'n':  sb.append('\n'); i += 2; break;
                    case 'r':  sb.append('\r'); i += 2; break;
                    case 't':  sb.append('\t'); i += 2; break;
                    case 'u':
                        if (i + 5 < s.length()) {
                            int cp = Integer.parseInt(s.substring(i + 2, i + 6), 16);
                            sb.append((char) cp);
                            i += 6;
                        } else { i += 2; }
                        break;
                    default:   sb.append(n); i += 2; break;
                }
            } else if (c == '"') {
                return sb.toString();
            } else {
                sb.append(c);
                i++;
            }
        }
        return sb.toString();
    }
}
