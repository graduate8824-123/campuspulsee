package com.campuspulse.listener;

import com.campuspulse.util.DBConnection;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Connection;

/**
 * Lifecycle Listener for CampusPulse application.
 * Demonstrates ServletContextListener:
 * 1. Testing database connectivity upon container startup
 * 2. Pre-caching categories or configuration parameters in ServletContext
 * 3. Logging initialization details
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=================================================");
        System.out.println("  CampusPulse Web Application Starting Up...     ");
        System.out.println("=================================================");

        // Test database connectivity
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("[CampusPulse] Database connection verified successfully!");
                sce.getServletContext().setAttribute("dbStatus", "ONLINE");
            }
        } catch (Exception e) {
            System.err.println("[CampusPulse WARNING] Could not verify database connection on startup: " + e.getMessage());
            System.err.println("Ensure MySQL Server is running and schema.sql has been executed.");
            sce.getServletContext().setAttribute("dbStatus", "OFFLINE: " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[CampusPulse] Application shutting down cleanly.");
    }
}
