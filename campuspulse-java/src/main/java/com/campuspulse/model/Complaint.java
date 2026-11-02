package com.campuspulse.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * JavaBean representing an individual student complaint report.
 */
public class Complaint implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String complaintCode; // e.g. "CMP-101"
    private int userId;
    private String studentName;
    private String studentEmail;
    private int categoryId;
    private String categoryName;
    private int locationId;
    private String locationName;
    private Integer issueId; // Nullable
    private String issueTitle;
    private String description;
    private boolean anonymous;
    private String priority; // "LOW", "MEDIUM", "HIGH"
    private String status;   // "SUBMITTED", "UNDER_REVIEW", "IN_PROGRESS", "RESOLVED", "REJECTED"
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Complaint() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getComplaintCode() { return complaintCode; }
    public void setComplaintCode(String complaintCode) { this.complaintCode = complaintCode; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public int getLocationId() { return locationId; }
    public void setLocationId(int locationId) { this.locationId = locationId; }

    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }

    public Integer getIssueId() { return issueId; }
    public void setIssueId(Integer issueId) { this.issueId = issueId; }

    public String getIssueTitle() { return issueTitle; }
    public void setIssueTitle(String issueTitle) { this.issueTitle = issueTitle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAnonymous() { return anonymous; }
    public void setAnonymous(boolean anonymous) { this.anonymous = anonymous; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    /**
     * Display name helper for Admin UI adhering to anonymous reporting rule.
     */
    public String getDisplayName() {
        if (this.anonymous) {
            return "Anonymous Student";
        }
        return this.studentName != null ? this.studentName : "Student #" + this.userId;
    }
}
