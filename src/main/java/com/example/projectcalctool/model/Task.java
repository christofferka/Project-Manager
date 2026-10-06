package com.example.projectcalctool.model;

import java.time.LocalDate;
import java.util.List;

public class Task {

    // Unik id for task
    private int id;

    // Reference til delprojekt
    private int subprojectId;

    // Navn på task
    private String name;

    // Beskrivelse af task
    private String description;

    // Estimerede timer
    private double estimatedHours;

    // Faktiske timer
    private Double actualHours;

    // Timepris
    private double rate;

    // Tildelt bruger
    private Integer userId;

    // Deadline
    private LocalDate deadline;

    // Status på task
    private TaskStatus status;

    // Liste af subtasks
    private List<Subtask> subtasks;

    // Navn på medarbejder
    private String employeeName;

    // Tom constructor
    public Task() {}

    // Constructor med alle felter
    public Task(int id,
                int subprojectId,
                String name,
                String description,
                double estimatedHours,
                Double actualHours,
                double rate,
                Integer userId,
                LocalDate deadline,
                TaskStatus status,
                List<Subtask> subtasks,
                String employeeName) {

        this.id = id;
        this.subprojectId = subprojectId;
        this.name = name;
        this.description = description;
        this.estimatedHours = estimatedHours;
        this.actualHours = actualHours;
        this.rate = rate;
        this.userId = userId;
        this.deadline = deadline;
        this.status = status;
        this.subtasks = subtasks;
        this.employeeName = employeeName;
    }

    // GETTERS + SETTERS

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSubprojectId() { return subprojectId; }
    public void setSubprojectId(int subprojectId) { this.subprojectId = subprojectId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(double estimatedHours) { this.estimatedHours = estimatedHours; }

    public Double getActualHours() { return actualHours; }
    public void setActualHours(Double actualHours) { this.actualHours = actualHours; }

    public double getRate() { return rate; }
    public void setRate(double rate) { this.rate = rate; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public List<Subtask> getSubtasks() { return subtasks; }
    public void setSubtasks(List<Subtask> subtasks) { this.subtasks = subtasks; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    // Hjælpemetoder

    // Returnerer faktiske timer hvis de findes
    public double getHours() {
        return actualHours != null ? actualHours : estimatedHours;
    }

    // Tjekker om task er færdig
    public boolean isDone() {
        return status == TaskStatus.DONE;
    }

    // Returnerer status som tekst
    public String getStatusDisplay() {
        if (status == null) return "Ukendt";

        return switch (status) {
            case NOT_STARTED -> "Ikke startet";
            case IN_PROGRESS -> "I gang";
            case DONE -> "Færdig";
        };
    }
}
