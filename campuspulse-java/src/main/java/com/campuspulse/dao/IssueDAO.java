package com.campuspulse.dao;

import com.campuspulse.model.Issue;
import com.campuspulse.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IssueDAO {

    public List<Issue> findOpenIssues() throws SQLException {
        List<Issue> list = new ArrayList<>();
        String sql = "SELECT i.id, i.issue_code, i.title, i.category_id, c.name AS category_name, " +
                     "i.location_id, l.name AS location_name, i.priority, i.status, i.report_count, " +
                     "i.assigned_to, i.created_at, i.updated_at " +
                     "FROM issues i " +
                     "JOIN categories c ON i.category_id = c.id " +
                     "JOIN locations l ON i.location_id = l.id " +
                     "WHERE i.status != 'RESOLVED' " +
                     "ORDER BY i.report_count DESC, i.created_at DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapIssue(rs));
            }
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        return list;
    }

    public List<Issue> findAllIssues() throws SQLException {
        List<Issue> list = new ArrayList<>();
        String sql = "SELECT i.id, i.issue_code, i.title, i.category_id, c.name AS category_name, " +
                     "i.location_id, l.name AS location_name, i.priority, i.status, i.report_count, " +
                     "i.assigned_to, i.created_at, i.updated_at " +
                     "FROM issues i " +
                     "JOIN categories c ON i.category_id = c.id " +
                     "JOIN locations l ON i.location_id = l.id " +
                     "ORDER BY i.report_count DESC, i.created_at DESC";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapIssue(rs));
            }
        } finally {
            DBConnection.close(conn, ps, rs);
        }
        return list;
    }

    public Issue findById(int id) throws SQLException {
        String sql = "SELECT i.id, i.issue_code, i.title, i.category_id, c.name AS category_name, " +
                     "i.location_id, l.name AS location_name, i.priority, i.status, i.report_count, " +
                     "i.assigned_to, i.created_at, i.updated_at " +
                     "FROM issues i " +
                     "JOIN categories c ON i.category_id = c.id " +
                     "JOIN locations l ON i.location_id = l.id " +
                     "WHERE i.id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapIssue(rs);
            }
            return null;
        } finally {
            DBConnection.close(conn, ps, rs);
        }
    }

    public int createIssue(Issue issue, Connection conn) throws SQLException {
        String countSql = "SELECT COUNT(*) FROM issues";
        Statement stmt = conn.createStatement();
        ResultSet rsCount = stmt.executeQuery(countSql);
        int nextNum = 1;
        if (rsCount.next()) {
            nextNum = rsCount.getInt(1) + 1;
        }
        rsCount.close();
        stmt.close();

        String code = String.format("CP-%03d", nextNum);
        issue.setIssueCode(code);

        String sql = "INSERT INTO issues (issue_code, title, category_id, location_id, priority, status, report_count, assigned_to) " +
                     "VALUES (?, ?, ?, ?, ?, 'OPEN', 1, ?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, code);
        ps.setString(2, issue.getTitle());
        ps.setInt(3, issue.getCategoryId());
        ps.setInt(4, issue.getLocationId());
        ps.setString(5, issue.getPriority() != null ? issue.getPriority() : "MEDIUM");
        ps.setString(6, issue.getAssignedTo() != null ? issue.getAssignedTo() : "Campus Facilities / IT Team");

        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys();
        int issueId = 0;
        if (rs.next()) {
            issueId = rs.getInt(1);
            issue.setId(issueId);
        }
        rs.close();
        ps.close();
        return issueId;
    }

    public void incrementReportCount(int issueId, Connection conn) throws SQLException {
        String sql = "UPDATE issues SET report_count = report_count + 1 WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, issueId);
        ps.executeUpdate();
        ps.close();
    }

    public void updateStatusAndPriority(int issueId, String status, String priority, int adminId, String remarks) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        PreparedStatement psHist = null;
        PreparedStatement psCascade = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin Transaction

            // 1. Update issue
            String sql = "UPDATE issues SET status = ?, priority = ? WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, priority);
            ps.setInt(3, issueId);
            ps.executeUpdate();

            // 2. Cascade status to all linked complaints
            String cascadeSql = "UPDATE complaints SET status = ?, priority = ? WHERE issue_id = ?";
            psCascade = conn.prepareStatement(cascadeSql);
            psCascade.setString(1, status);
            psCascade.setString(2, priority);
            psCascade.setInt(3, issueId);
            psCascade.executeUpdate();

            // 3. Log into issue_updates table
            String logSql = "INSERT INTO issue_updates (issue_id, admin_id, status_update, remarks) VALUES (?, ?, ?, ?)";
            psHist = conn.prepareStatement(logSql);
            psHist.setInt(1, issueId);
            psHist.setInt(2, adminId);
            psHist.setString(3, status);
            psHist.setString(4, remarks != null ? remarks : "Status updated by administrator.");
            psHist.executeUpdate();

            conn.commit(); // Commit Transaction
        } catch (SQLException ex) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw ex;
        } finally {
            if (psCascade != null) psCascade.close();
            if (psHist != null) psHist.close();
            if (ps != null) ps.close();
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    private Issue mapIssue(ResultSet rs) throws SQLException {
        Issue issue = new Issue();
        issue.setId(rs.getInt("id"));
        issue.setIssueCode(rs.getString("issue_code"));
        issue.setTitle(rs.getString("title"));
        issue.setCategoryId(rs.getInt("category_id"));
        issue.setCategoryName(rs.getString("category_name"));
        issue.setLocationId(rs.getInt("location_id"));
        issue.setLocationName(rs.getString("location_name"));
        issue.setPriority(rs.getString("priority"));
        issue.setStatus(rs.getString("status"));
        issue.setReportCount(rs.getInt("report_count"));
        issue.setAssignedTo(rs.getString("assigned_to"));
        issue.setCreatedAt(rs.getTimestamp("created_at"));
        issue.setUpdatedAt(rs.getTimestamp("updated_at"));
        return issue;
    }
}
