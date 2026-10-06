package com.example.projectcalctool.model;

public enum TaskStatus {

    // Task er ikke startet
    NOT_STARTED("Ikke startet"),

    // Task er i gang
    IN_PROGRESS("I gang"),

    // Task er afsluttet
    DONE("Afsluttet");

    // Visningsnavn til UI
    private final String displayName;

    // Constructor til enum
    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    // Returnerer visningsnavn
    public String getDisplayName() {
        return displayName;
    }
}
