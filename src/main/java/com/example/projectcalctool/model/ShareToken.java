package com.example.projectcalctool.model;

import java.time.LocalDateTime;

public class ShareToken {

    // Unik id for tokenet i databasen
    private int id;

    // Reference til projektet der deles
    private int projectId;

    // Selve delingstokenet
    private String token;

    // Tidspunkt hvor tokenet udløber
    private LocalDateTime expiresAt;

    // Tom constructor (bruges af Spring / JDBC mapping)
    public ShareToken() {
    }

    // Constructor uden id (bruges ved oprettelse)
    public ShareToken(int projectId,
                      String token,
                      LocalDateTime expiresAt) {

        this.projectId = projectId;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    // Constructor med alle felter
    public ShareToken(int id,
                      int projectId,
                      String token,
                      LocalDateTime expiresAt) {

        this.id = id;
        this.projectId = projectId;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    // Getter og setter for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter og setter for projectId
    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    // Getter og setter for token
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // Getter og setter for udløbstidspunkt
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
