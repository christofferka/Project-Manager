package com.example.projectcalctool.model;

public class CostItem {

    // Unik id fra databasen
    private int id;

    // Reference til projektet
    private int projectId;

    // Navn på omkostningen
    private String name;

    // Antal enheder
    private double quantity;

    // Pris pr enhed
    private double unitPrice;

    // Tom constructor (bruges af Spring / JDBC mapping)
    public CostItem() {}

    // Constructor med alle felter
    public CostItem(int id,
                    int projectId,
                    String name,
                    double quantity,
                    double unitPrice) {

        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
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

    // Getter og setter for antal
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    // Getter og setter for pris pr enhed
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    // Beregner samlet pris
    public double getTotal() {
        return quantity * unitPrice;
    }
}
