package com.campuspulse.model;

import java.io.Serializable;

/**
 * JavaBean representing a predefined Campus Location (e.g. Block A, Library, Computer Lab).
 */
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String zone;

    public Location() {}

    public Location(int id, String name, String zone) {
        this.id = id;
        this.name = name;
        this.zone = zone;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }
}
