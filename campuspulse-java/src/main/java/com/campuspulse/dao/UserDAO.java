package com.campuspulse.dao;

import com.campuspulse.model.User;
import com.campuspulse.util.DBConnection;
import com.campuspulse.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for User authentication and registration.
 * Demonstrates PreparedStatement against SQL injection and password hashing.
 */
public class UserDAO {

    /**
     * Authenticates a user by email and plaintext password.
     */
    public User authenticate(String email, String plaintextPassword) throws SQLException {
        String sql = "SELECT id, name, email, password_hash, role, department, created_at FROM users WHERE email = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, email.trim().toLowerCase());
            rs = ps.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                if (PasswordUtil.verify(plaintextPassword, storedHash)) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPasswordHash(storedHash);
                    user.setRole(rs.getString("role"));
                    user.setDepartment(rs.getString("department"));
                    user.setCreatedAt(rs.getTimestamp("created_at"));
                    return user;
                }
            }
            return null; // Invalid credentials
        } finally {
            DBConnection.close(conn, ps, rs);
        }
    }

    /**
     * Registers a new student user.
     */
    public boolean register(String name, String email, String plaintextPassword, String department) throws SQLException {
        String sql = "INSERT INTO users (name, email, password_hash, role, department) VALUES (?, ?, ?, 'STUDENT', ?)";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, name.trim());
            ps.setString(2, email.trim().toLowerCase());
            ps.setString(3, PasswordUtil.hashPassword(plaintextPassword));
            ps.setString(4, department != null ? department.trim() : "General");

            int rows = ps.executeUpdate();
            return rows > 0;
        } finally {
            DBConnection.close(conn, ps, null);
        }
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT id FROM users WHERE email = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, email.trim().toLowerCase());
            rs = ps.executeQuery();
            return rs.next();
        } finally {
            DBConnection.close(conn, ps, rs);
        }
    }
}
