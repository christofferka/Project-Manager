package com.example.projectcalctool.model;

import java.time.LocalDate;

public class Subtask {

    // Unik id for subtask
    private int id;

    // Reference til task
    private int taskId;

    // Navn på subtask
    private String name;

    // Beskrivelse af subtask
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

    // Status på subtask
    private TaskStatus status;

    // Navn på medarbejder
    private String employeeName;

    // Tom constructor
    public Subtask() {}

    // GETTERS / SETTERS

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }

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

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    // Hjælpemetoder

    // Returnerer faktiske timer hvis de findes
    public double getHours() {
        return actualHours != null ? actualHours : estimatedHours;
    }

    // Tjekker om subtask er færdig
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
