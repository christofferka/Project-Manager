package com.example.projectcalctool.model;

public class User {

    // Unik id for bruger
    private int id;

    // Brugernavn
    private String username;

    // Email adresse
    private String email;

    // Password
    private String password;

    // Rolle (USER eller ADMIN)
    private String role;

    // AI integration (brugeren tilfoejer selv sin API-noegle)
    // Provider: "anthropic", "openai", eller null hvis slaaet fra
    private String aiProvider;
    private String aiApiKey;
    private String aiModel;

    // Tom constructor
    public User() {
        this.role = "USER";
    }

    // Constructor med alle felter
    public User(int id, String username, String email, String password, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Bruges ved registrering af ny bruger
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = "USER";
    }

    // GETTERS / SETTERS
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getAiProvider() { return aiProvider; }
    public void setAiProvider(String aiProvider) { this.aiProvider = aiProvider; }

    public String getAiApiKey() { return aiApiKey; }
    public void setAiApiKey(String aiApiKey) { this.aiApiKey = aiApiKey; }

    public String getAiModel() { return aiModel; }
    public void setAiModel(String aiModel) { this.aiModel = aiModel; }

    public boolean hasAiConfigured() {
        return aiProvider != null && !aiProvider.isBlank()
                && aiApiKey != null && !aiApiKey.isBlank();
    }
}
