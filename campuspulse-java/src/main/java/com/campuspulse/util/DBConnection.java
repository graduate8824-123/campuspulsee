package com.campuspulse.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Database Connection Utility.
 * Manages JDBC connections using credentials from db.properties.
 * Demonstrates Type-4 JDBC Driver usage, PreparedStatement resource safety,
 * and externalized configuration for MySQL Workbench compatibility.
 */
public class DBConnection {

    private static String url;
    private static String username;
    private static String password;
    private static String driver;
    private static boolean initialized = false;

    // Static initializer block to load properties and register JDBC driver once
    static {
        init();
    }

    private static synchronized void init() {
        if (initialized) return;

        try {
            Properties props = new Properties();
            InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
            
            if (in != null) {
                props.load(in);
                driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
                url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/campuspulse_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
                username = props.getProperty("db.username", "root");
                password = props.getProperty("db.password", "root");
                in.close();
            } else {
                // Fallback default credentials if db.properties is missing
                driver = "com.mysql.cj.jdbc.Driver";
                url = "jdbc:mysql://localhost:3306/campuspulse_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
                username = "root";
                password = "root";
            }

            // Register Driver
            Class.forName(driver);
            initialized = true;
            System.out.println("[CampusPulse] MySQL JDBC Driver registered successfully.");
        } catch (Exception e) {
            System.err.println("[CampusPulse Error] Failed to load JDBC driver or db.properties: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obtains a new database connection.
     * @return java.sql.Connection
     * @throws SQLException if database connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            init();
        }
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Closes ResultSet cleanly without throwing checked exceptions.
     */
    public static void close(ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException ignored) {}
        }
    }

    /**
     * Closes Statement/PreparedStatement cleanly.
     */
    public static void close(Statement stmt) {
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException ignored) {}
        }
    }

    /**
     * Closes Connection cleanly.
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }

    /**
     * Convenience method to close all three resources together.
     */
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        close(rs);
        close(stmt);
        close(conn);
    }
}
