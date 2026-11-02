package com.campuspulse.controller;

import com.campuspulse.dao.CategoryDAO;
import com.campuspulse.dao.ComplaintDAO;
import com.campuspulse.dao.LocationDAO;
import com.campuspulse.model.Category;
import com.campuspulse.model.Complaint;
import com.campuspulse.model.Location;
import com.campuspulse.model.User;
import com.campuspulse.service.PriorityService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller for student complaint operations:
 * 1. Viewing student dashboard
 * 2. Viewing complaint submission form
 * 3. Submitting complaints with transactional safety
 * 4. Tracking status of submitted complaints
 */
@WebServlet(urlPatterns = {"/student/dashboard", "/student/submit-complaint", "/student/my-complaints", "/complaint/submit"})
public class ComplaintServlet extends HttpServlet {

    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final LocationDAO locationDAO = new LocationDAO();
    private final PriorityService priorityService = new PriorityService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");

        try {
            if ("/student/submit-complaint".equals(path)) {
                // Populate categories and locations for form dropdowns
                List<Category> categories = categoryDAO.findAll();
                List<Location> locations = locationDAO.findAll();
                request.setAttribute("categories", categories);
                request.setAttribute("locations", locations);
                request.getRequestDispatcher("/WEB-INF/views/submit-complaint.jsp").forward(request, response);

            } else if ("/student/my-complaints".equals(path)) {
                // Student complaint history
                List<Complaint> myComplaints = complaintDAO.findByUserId(currentUser.getId());
                request.setAttribute("complaints", myComplaints);
                request.getRequestDispatcher("/WEB-INF/views/my-complaints.jsp").forward(request, response);

            } else {
                // /student/dashboard
                List<Complaint> myComplaints = complaintDAO.findByUserId(currentUser.getId());
                request.setAttribute("recentComplaints", myComplaints);
                request.setAttribute("totalReported", myComplaints.size());
                long resolvedCount = myComplaints.stream().filter(c -> "RESOLVED".equalsIgnoreCase(c.getStatus())).count();
                request.setAttribute("resolvedCount", resolvedCount);
                request.getRequestDispatcher("/WEB-INF/views/student-dashboard.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");

        String categoryIdStr = request.getParameter("categoryId");
        String locationIdStr = request.getParameter("locationId");
        String description = request.getParameter("description");
        String anonymousStr = request.getParameter("anonymous");
        String joinIssueIdStr = request.getParameter("joinIssueId");
        String createNewIssueStr = request.getParameter("createNewIssue");
        String newIssueTitle = request.getParameter("newIssueTitle");

        if (categoryIdStr == null || locationIdStr == null || description == null || description.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/student/submit-complaint?error=Missing+fields");
            return;
        }

        try {
            int categoryId = Integer.parseInt(categoryIdStr);
            int locationId = Integer.parseInt(locationIdStr);
            boolean anonymous = "true".equalsIgnoreCase(anonymousStr) || "on".equalsIgnoreCase(anonymousStr);
            boolean createNewIssue = "true".equalsIgnoreCase(createNewIssueStr);

            Integer joinIssueId = null;
            if (joinIssueIdStr != null && !joinIssueIdStr.trim().isEmpty() && !joinIssueIdStr.equals("0")) {
                joinIssueId = Integer.parseInt(joinIssueIdStr);
            }

            Category cat = categoryDAO.findById(categoryId);

            Complaint complaint = new Complaint();
            complaint.setUserId(currentUser.getId());
            complaint.setCategoryId(categoryId);
            complaint.setLocationId(locationId);
            complaint.setDescription(description.trim());
            complaint.setAnonymous(anonymous);
            complaint.setIssueId(joinIssueId);

            // Compute priority recommendation
            String initialPriority = priorityService.calculatePriority(1, cat, 0);
            complaint.setPriority(initialPriority);

            // Execute ACID Transaction
            boolean saved = complaintDAO.saveComplaint(complaint, createNewIssue, newIssueTitle);

            if (saved) {
                // Post-Redirect-Get (PRG) Pattern
                response.sendRedirect(request.getContextPath() + "/student/my-complaints?success=Complaint+submitted+successfully");
            } else {
                response.sendRedirect(request.getContextPath() + "/student/submit-complaint?error=Failed+to+save");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error processing complaint: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
