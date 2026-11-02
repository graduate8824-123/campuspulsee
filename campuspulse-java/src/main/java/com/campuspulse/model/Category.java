package com.campuspulse.model;

import java.io.Serializable;

/**
 * JavaBean representing a Complaint Category (e.g. Internet/WiFi, Electricity, Water).
 */
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private boolean critical;
    private String description;

    public Category() {}

    public Category(int id, String name, boolean critical, String description) {
        this.id = id;
        this.name = name;
        this.critical = critical;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isCritical() { return critical; }
    public void setCritical(boolean critical) { this.critical = critical; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
