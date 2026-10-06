package com.example.projectcalctool.model;

public class Subproject {

    // Unik id for delprojektet
    private int id;

    // Reference til overordnet projekt
    private int projectId;

    // Navn på delprojektet
    private String name;

    // Beskrivelse af delprojektet
    private String description;

    // Fremdrift i procent (0–100)
    private int progressPercent;

    // Tom constructor (bruges af Spring / JDBC mapping)
    public Subproject() {}

    // Constructor uden progress som parameter
    public Subproject(int id,
                      int projectId,
                      String name,
                      String description) {

        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.description = description;

        // Standardværdi så views ikke fejler
        this.progressPercent = 0;
    }

    // Getter og setter for id
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // Getter og setter for projectId
    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    // Getter og setter for navn
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Getter og setter for beskrivelse
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Getter og setter for progress i procent
    public int getProgressPercent() { return progressPercent; }
    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
    }
}
