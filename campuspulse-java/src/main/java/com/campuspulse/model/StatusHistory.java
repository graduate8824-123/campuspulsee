package com.campuspulse.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * JavaBean representing an audit log entry for status changes.
 */
public class StatusHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int complaintId;
    private int changedBy;
    private String adminName;
    private String oldStatus;
    private String newStatus;
    private String remarks;
    private Timestamp changedAt;

    public StatusHistory() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getComplaintId() { return complaintId; }
    public void setComplaintId(int complaintId) { this.complaintId = complaintId; }

    public int getChangedBy() { return changedBy; }
    public void setChangedBy(int changedBy) { this.changedBy = changedBy; }

    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }

    public String getOldStatus() { return oldStatus; }
    public void setOldStatus(String oldStatus) { this.oldStatus = oldStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Timestamp getChangedAt() { return changedAt; }
    public void setChangedAt(Timestamp changedAt) { this.changedAt = changedAt; }
}
