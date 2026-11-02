package com.campuspulse.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * JavaBean representing an Aggregated Issue (Cluster of related student complaints).
 */
public class Issue implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String issueCode; // e.g., "CP-001"
    private String title;
    private int categoryId;
    private String categoryName;
    private int locationId;
    private String locationName;
    private String priority; // "LOW", "MEDIUM", "HIGH"
    private String status;   // "OPEN", "UNDER_REVIEW", "IN_PROGRESS", "RESOLVED", "REJECTED"
    private int reportCount;
    private String assignedTo;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Issue() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getIssueCode() { return issueCode; }
    public void setIssueCode(String issueCode) { this.issueCode = issueCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public int getLocationId() { return locationId; }
    public void setLocationId(int locationId) { this.locationId = locationId; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getReportCount() { return reportCount; }
    public void setReportCount(int reportCount) { this.reportCount = reportCount; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
