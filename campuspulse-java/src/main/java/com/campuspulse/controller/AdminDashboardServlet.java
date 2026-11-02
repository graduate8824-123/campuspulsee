package com.campuspulse.controller;

import com.campuspulse.dao.CategoryDAO;
import com.campuspulse.dao.ComplaintDAO;
import com.campuspulse.dao.IssueDAO;
import com.campuspulse.dao.LocationDAO;
import com.campuspulse.model.Category;
import com.campuspulse.model.Complaint;
import com.campuspulse.model.Issue;
import com.campuspulse.model.Location;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Controller for Administrator Dashboard.
 * Serves summary metric cards, filter controls, issue groups, and complaints table.
 */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final IssueDAO issueDAO = new IssueDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final LocationDAO locationDAO = new LocationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String catParam = request.getParameter("categoryId");
        String locParam = request.getParameter("locationId");
        String status = request.getParameter("status");
        String priority = request.getParameter("priority");
        String keyword = request.getParameter("keyword");

        Integer categoryId = (catParam != null && !catParam.isEmpty()) ? Integer.parseInt(catParam) : null;
        Integer locationId = (locParam != null && !locParam.isEmpty()) ? Integer.parseInt(locParam) : null;

        try {
            // Summary metrics
            Map<String, Integer> stats = complaintDAO.getDashboardStatistics();
            request.setAttribute("stats", stats);

            // Open aggregated issue groups
            List<Issue> openIssues = issueDAO.findOpenIssues();
            request.setAttribute("openIssues", openIssues);

            // Filtered complaints
            List<Complaint> complaints = complaintDAO.findAll(categoryId, locationId, status, priority, keyword);
            request.setAttribute("complaints", complaints);

            // Form lookup data
            List<Category> categories = categoryDAO.findAll();
            List<Location> locations = locationDAO.findAll();
            request.setAttribute("categories", categories);
            request.setAttribute("locations", locations);

            // Retain filter state
            request.setAttribute("selectedCategory", categoryId);
            request.setAttribute("selectedLocation", locationId);
            request.setAttribute("selectedStatus", status);
            request.setAttribute("selectedPriority", priority);
            request.setAttribute("keyword", keyword);

            request.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database query failure: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
