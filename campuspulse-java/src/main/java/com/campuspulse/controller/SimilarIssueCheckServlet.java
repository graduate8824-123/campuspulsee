package com.campuspulse.controller;

import com.campuspulse.service.IssueDetectionService;
import com.campuspulse.service.IssueDetectionService.CandidateMatch;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Controller serving AJAX similarity checks.
 * Invoked by similarity-checker.js prior to form submission.
 */
@WebServlet("/api/check-similarity")
public class SimilarIssueCheckServlet extends HttpServlet {

    private final IssueDetectionService issueDetectionService = new IssueDetectionService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String catStr = request.getParameter("categoryId");
        String locStr = request.getParameter("locationId");
        String text = request.getParameter("description");

        if (catStr == null || locStr == null || text == null || text.trim().isEmpty()) {
            out.print("{\"hasMatch\": false, \"reason\": \"Insufficient data\"}");
            return;
        }

        try {
            int categoryId = Integer.parseInt(catStr);
            int locationId = Integer.parseInt(locStr);

            CandidateMatch match = issueDetectionService.findSimilarIssue(categoryId, locationId, text);

            if (match != null && match.getResult().isSimilar()) {
                StringBuilder json = new StringBuilder();
                json.append("{");
                json.append("\"hasMatch\": true,");
                json.append("\"score\": ").append(match.getResult().getTotalScore()).append(",");
                json.append("\"breakdown\": {");
                json.append("\"category\": ").append(match.getResult().getCategoryScore()).append(",");
                json.append("\"location\": ").append(match.getResult().getLocationScore()).append(",");
                json.append("\"keyword\": ").append(match.getResult().getKeywordScore()).append(",");
                json.append("\"text\": ").append(match.getResult().getTextScore());
                json.append("},");
                json.append("\"issueId\": ").append(match.getIssue().getId()).append(",");
                json.append("\"issueCode\": \"").append(match.getIssue().getIssueCode()).append("\",");
                json.append("\"issueTitle\": \"").append(escapeJson(match.getIssue().getTitle())).append("\",");
                json.append("\"reportCount\": ").append(match.getIssue().getReportCount()).append(",");
                json.append("\"priority\": \"").append(match.getIssue().getPriority()).append("\",");
                json.append("\"status\": \"").append(match.getIssue().getStatus()).append("\"");
                json.append("}");
                out.print(json.toString());
            } else {
                out.print("{\"hasMatch\": false}");
            }
        } catch (Exception e) {
            out.print("{\"hasMatch\": false, \"error\": \"" + escapeJson(e.getMessage()) + "\"}");
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\"").replace("\n", " ").replace("\r", "");
    }
}
