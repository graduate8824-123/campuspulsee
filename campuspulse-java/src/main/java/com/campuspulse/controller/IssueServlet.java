package com.campuspulse.controller;

import com.campuspulse.dao.ComplaintDAO;
import com.campuspulse.dao.IssueDAO;
import com.campuspulse.model.Complaint;
import com.campuspulse.model.Issue;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller displaying details of an aggregated campus Issue group,
 * including all clustered student complaints.
 */
@WebServlet("/admin/issue")
public class IssueServlet extends HttpServlet {

    private final IssueDAO issueDAO = new IssueDAO();
    private final ComplaintDAO complaintDAO = new ComplaintDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        try {
            int issueId = Integer.parseInt(idStr);
            Issue issue = issueDAO.findById(issueId);

            if (issue == null) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard?error=Issue+not+found");
                return;
            }

            // Fetch all student complaints grouped under this issue
            List<Complaint> groupedComplaints = complaintDAO.findByIssueId(issueId);

            request.setAttribute("issue", issue);
            request.setAttribute("groupedComplaints", groupedComplaints);
            request.getRequestDispatcher("/WEB-INF/views/issue-details.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
