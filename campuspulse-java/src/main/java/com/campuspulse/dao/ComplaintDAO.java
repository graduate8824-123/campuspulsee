package com.campuspulse.dao;

import com.campuspulse.model.Complaint;
import com.campuspulse.model.Issue;
import com.campuspulse.model.StatusHistory;
import com.campuspulse.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Complaint entities.
 * Demonstrates:
 * 1. ACID Transactions (setAutoCommit(false), commit(), rollback())
 * 2. PreparedStatement with parameter placeholders
 * 3. Dynamic search & filter queries
 * 4. Aggregation queries for Dashboard metrics
 */
public class ComplaintDAO {

    private final IssueDAO issueDAO = new IssueDAO();

    /**
     * Saves a complaint within an atomic database transaction.
     */
    public boolean saveComplaint(Complaint complaint, boolean createNewIssue, String issueTitle) throws SQLException {
        Connection conn = null;
        PreparedStatement psComplaint = null;
        PreparedStatement psHist = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin ACID Transaction

            // 1. If user elected to create a new Issue Group for this problem
            if (createNewIssue) {
                Issue issue = new Issue();
                issue.setTitle(issueTitle != null && !issueTitle.trim().isEmpty() ? issueTitle : "Campus Issue - " + complaint.getDescription());
                issue.setCategoryId(complaint.getCategoryId());
                issue.setLocationId(complaint.getLocationId());
                issue.setPriority(complaint.getPriority());

                int newIssueId = issueDAO.createIssue(issue, conn);
                complaint.setIssueId(newIssueId);
            } else if (complaint.getIssueId() != null && complaint.getIssueId() > 0) {
                // User joined an existing issue
                issueDAO.incrementReportCount(complaint.getIssueId(), conn);
            }

            // 2. Generate unique complaint code (e.g. CMP-101)
            String countSql = "SELECT COUNT(*) FROM complaints";
            Statement stmt = conn.createStatement();
            ResultSet rsCount = stmt.executeQuery(countSql);
            int nextNum = 101;
            if (rsCount.next()) {
                nextNum = 101 + rsCount.getInt(1);
            }
            rsCount.close();
            stmt.close();

            String code = "CMP-" + nextNum;
            complaint.setComplaintCode(code);

            // 3. Insert complaint
            String insertSql = "INSERT INTO complaints (complaint_code, user_id, category_id, location_id, issue_id, " +
                               "description, is_anonymous, priority, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'SUBMITTED')";
            psComplaint = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            psComplaint.setString(1, code);
            psComplaint.setInt(2, complaint.getUserId());
            psComplaint.setInt(3, complaint.getCategoryId());
            psComplaint.setInt(4, complaint.getLocationId());

            if (complaint.getIssueId() != null && complaint.getIssueId() > 0) {
                psComplaint.setInt(5, complaint.getIssueId());
            } else {
                psComplaint.setNull(5, java.sql.Types.INTEGER);
            }

            psComplaint.setString(6, complaint.getDescription());
            psComplaint.setBoolean(7, complaint.isAnonymous());
            psComplaint.setString(8, complaint.getPriority() != null ? complaint.getPriority() : "MEDIUM");

            psComplaint.executeUpdate();
            ResultSet rsKeys = psComplaint.getGeneratedKeys();
            int complaintId = 0;
            if (rsKeys.next()) {
                complaintId = rsKeys.getInt(1);
                complaint.setId(complaintId);
            }
            rsKeys.close();

            // 4. Record initial status history
            String histSql = "INSERT INTO complaint_status_history (complaint_id, changed_by, old_status, new_status, remarks) " +
                             "VALUES (?, ?, 'NONE', 'SUBMITTED', 'Complaint successfully logged by student.')";
            psHist = conn.prepareStatement(histSql);
            psHist.setInt(1, complaintId);
            psHist.setInt(2, complaint.getUserId());
            psHist.executeUpdate();

            // All steps succeeded -> Commit transaction
            conn.commit();
            return true;
        } catch (SQLException ex) {
            // Any step failed -> Rollback to preserve atomic integrity
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("[CampusPulse] Transaction rolled back due to error: " + ex.getMessage());
                } catch (SQLException ignored) {}
            }
            throw ex;
        } finally {
            if (psHist != null) psHist.close();
            if (psComplaint != null) psComplaint.close();
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    public List<Complaint> findByUserId(int userId) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        String sql = "SELECT c.id, c.complaint_code, c.user_id, u.name AS student_name, u.email AS student_email, " +
                     "c.category_id, cat.name AS category_name, c.location_id, loc.name AS location_name, " +
                     "c.issue_id, i.title AS issue_title, c.description, c.is_anonymous, c.priority, c.status, " +
                     "c.created_at, c.updated_at " +
                     "FROM complaints c " +
                     "JOIN users u ON c.user_id = u.id " +
                     "JOIN categories cat ON c.category_id = cat.id " +
                     "JOIN locations loc ON c.location_id = loc.id " +
                     "LEFT JOIN issues i ON c.issue_id = i.id " +
                     "WHERE c.user_id = ? " +
                     "ORDER BY c.created_at DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapComplaint(rs));
            }
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        return list;
    }

    public List<Complaint> findAll(Integer categoryId, Integer locationId, String status, String priority, String keyword) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT c.id, c.complaint_code, c.user_id, u.name AS student_name, u.email AS student_email, " +
            "c.category_id, cat.name AS category_name, c.location_id, loc.name AS location_name, " +
            "c.issue_id, i.title AS issue_title, c.description, c.is_anonymous, c.priority, c.status, " +
            "c.created_at, c.updated_at " +
            "FROM complaints c " +
            "JOIN users u ON c.user_id = u.id " +
            "JOIN categories cat ON c.category_id = cat.id " +
            "JOIN locations loc ON c.location_id = loc.id " +
            "LEFT JOIN issues i ON c.issue_id = i.id " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (categoryId != null && categoryId > 0) {
            sql.append("AND c.category_id = ? ");
            params.add(categoryId);
        }
        if (locationId != null && locationId > 0) {
            sql.append("AND c.location_id = ? ");
            params.add(locationId);
        }
        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
            sql.append("AND c.status = ? ");
            params.add(status.trim().toUpperCase());
        }
        if (priority != null && !priority.trim().isEmpty() && !priority.equalsIgnoreCase("ALL")) {
            sql.append("AND c.priority = ? ");
            params.add(priority.trim().toUpperCase());
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (c.description LIKE ? OR c.complaint_code LIKE ?) ");
            params.add("%" + keyword.trim() + "%");
            params.add("%" + keyword.trim() + "%");
        }

        sql.append("ORDER BY c.created_at DESC");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapComplaint(rs));
            }
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        return list;
    }

    public List<Complaint> findByIssueId(int issueId) throws SQLException {
        List<Complaint> list = new ArrayList<>();
        String sql = "SELECT c.id, c.complaint_code, c.user_id, u.name AS student_name, u.email AS student_email, " +
                     "c.category_id, cat.name AS category_name, c.location_id, loc.name AS location_name, " +
                     "c.issue_id, i.title AS issue_title, c.description, c.is_anonymous, c.priority, c.status, " +
                     "c.created_at, c.updated_at " +
                     "FROM complaints c " +
                     "JOIN users u ON c.user_id = u.id " +
                     "JOIN categories cat ON c.category_id = cat.id " +
                     "JOIN locations loc ON c.location_id = loc.id " +
                     "LEFT JOIN issues i ON c.issue_id = i.id " +
                     "WHERE c.issue_id = ? " +
                     "ORDER BY c.created_at ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, issueId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapComplaint(rs));
            }
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        return list;
    }

    public List<StatusHistory> getStatusHistory(int complaintId) throws SQLException {
        List<StatusHistory> list = new ArrayList<>();
        String sql = "SELECT h.id, h.complaint_id, h.changed_by, u.name AS admin_name, h.old_status, h.new_status, h.remarks, h.changed_at " +
                     "FROM complaint_status_history h " +
                     "JOIN users u ON h.changed_by = u.id " +
                     "WHERE h.complaint_id = ? " +
                     "ORDER BY h.changed_at ASC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, complaintId);
            rs = ps.executeQuery();
            while (rs.next()) {
                StatusHistory h = new StatusHistory();
                h.setId(rs.getInt("id"));
                h.setComplaintId(rs.getInt("complaint_id"));
                h.setChangedBy(rs.getInt("changed_by"));
                h.setAdminName(rs.getString("admin_name"));
                h.setOldStatus(rs.getString("old_status"));
                h.setNewStatus(rs.getString("new_status"));
                h.setRemarks(rs.getString("remarks"));
                h.setChangedAt(rs.getTimestamp("changed_at"));
                list.add(h);
            }
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        return list;
    }

    public Map<String, Integer> getDashboardStatistics() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();

            // Total Complaints
            rs = stmt.executeQuery("SELECT COUNT(*) FROM complaints");
            if (rs.next()) stats.put("totalComplaints", rs.getInt(1));
            rs.close();

            // Open Issues
            rs = stmt.executeQuery("SELECT COUNT(*) FROM issues WHERE status != 'RESOLVED'");
            if (rs.next()) stats.put("openIssues", rs.getInt(1));
            rs.close();

            // High Priority Issues
            rs = stmt.executeQuery("SELECT COUNT(*) FROM issues WHERE priority = 'HIGH' AND status != 'RESOLVED'");
            if (rs.next()) stats.put("highPriorityIssues", rs.getInt(1));
            rs.close();

            // Resolved Complaints
            rs = stmt.executeQuery("SELECT COUNT(*) FROM complaints WHERE status = 'RESOLVED'");
            if (rs.next()) stats.put("resolvedComplaints", rs.getInt(1));
            rs.close();

        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
        return stats;
    }

    private Complaint mapComplaint(ResultSet rs) throws SQLException {
        Complaint c = new Complaint();
        c.setId(rs.getInt("id"));
        c.setComplaintCode(rs.getString("complaint_code"));
        c.setUserId(rs.getInt("user_id"));
        c.setStudentName(rs.getString("student_name"));
        c.setStudentEmail(rs.getString("student_email"));
        c.setCategoryId(rs.getInt("category_id"));
        c.setCategoryName(rs.getString("category_name"));
        c.setLocationId(rs.getInt("location_id"));
        c.setLocationName(rs.getString("location_name"));

        int iId = rs.getInt("issue_id");
        if (!rs.wasNull()) {
            c.setIssueId(iId);
            c.setIssueTitle(rs.getString("issue_title"));
        }

        c.setDescription(rs.getString("description"));
        c.setAnonymous(rs.getBoolean("is_anonymous"));
        c.setPriority(rs.getString("priority"));
        c.setStatus(rs.getString("status"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setUpdatedAt(rs.getTimestamp("updated_at"));
        return c;
    }
}
