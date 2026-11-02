package com.campuspulse.controller;

import com.campuspulse.dao.IssueDAO;
import com.campuspulse.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controller allowing administrators to update Issue status, assign priority,
 * and record administrative remarks. Cascades the update to all linked complaints.
 */
@WebServlet("/admin/update-status")
public class StatusUpdateServlet extends HttpServlet {

    private final IssueDAO issueDAO = new IssueDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentAdmin = (User) session.getAttribute("currentUser");

        String issueIdStr = request.getParameter("issueId");
        String newStatus = request.getParameter("status");
        String newPriority = request.getParameter("priority");
        String remarks = request.getParameter("remarks");

        if (issueIdStr == null || newStatus == null) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=Invalid+parameters");
            return;
        }

        try {
            int issueId = Integer.parseInt(issueIdStr);

            // Execute atomic update across issues, complaints, and audit logs
            issueDAO.updateStatusAndPriority(issueId, newStatus, newPriority, currentAdmin.getId(), remarks);

            response.sendRedirect(request.getContextPath() + "/admin/issue?id=" + issueId + "&success=Status+updated+successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to update status: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
