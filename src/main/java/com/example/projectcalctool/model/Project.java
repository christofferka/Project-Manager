package com.example.projectcalctool.model;

import java.time.LocalDate;

public class Project {

    // Unik id fra databasen
    private int id;

    // Projektnavn
    private String name;

    // Kundens navn
    private String customerName;

    // Projektets startdato
    private LocalDate startDate;

    // Projektets slutdato
    private LocalDate endDate;

    // Timepris for projektet
    private Double hourlyRate;

    // Estimerede timer for projektet
    private Double estimatedHours;

    // Tom constructor (bruges af Spring / JDBC mapping)
    public Project() {}

    // Constructor med alle felter
    public Project(int id,
                   String name,
                   String customerName,
                   LocalDate startDate,
                   LocalDate endDate,
                   Double hourlyRate,
                   Double estimatedHours) {

        this.id = id;
        this.name = name;
        this.customerName = customerName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.hourlyRate = hourlyRate;
        this.estimatedHours = estimatedHours;
    }

    // Getter og setter for id
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    // Getter og setter for navn
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Getter og setter for kundenavn
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    // Getter og setter for startdato
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    // Getter og setter for slutdato
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    // Getter og setter for timepris
    public Double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(Double hourlyRate) { this.hourlyRate = hourlyRate; }

    // Getter og setter for estimerede timer
    public Double getEstimatedHours() { return estimatedHours; }
    public void setEstimatedHours(Double estimatedHours) { this.estimatedHours = estimatedHours; }

    // Beregner samlet estimeret pris for projektet
    public double getCost() {
        if (hourlyRate == null || estimatedHours == null) return 0;
        return hourlyRate * estimatedHours;
    }
}
