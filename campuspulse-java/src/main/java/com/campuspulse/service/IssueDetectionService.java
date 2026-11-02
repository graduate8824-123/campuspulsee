package com.campuspulse.service;

import com.campuspulse.dao.IssueDAO;
import com.campuspulse.model.Issue;
import com.campuspulse.util.TextSimilarityUtil;
import com.campuspulse.util.TextSimilarityUtil.SimilarityResult;

import java.sql.SQLException;
import java.util.List;

/**
 * Service to detect if a student's newly typed complaint is similar
 * to any currently open issue on campus.
 */
public class IssueDetectionService {

    private final IssueDAO issueDAO = new IssueDAO();

    public static class CandidateMatch {
        private final Issue issue;
        private final SimilarityResult result;

        public CandidateMatch(Issue issue, SimilarityResult result) {
            this.issue = issue;
            this.result = result;
        }

        public Issue getIssue() { return issue; }
        public SimilarityResult getResult() { return result; }
    }

    /**
     * Compares the input against all open issues in the database.
     * Returns the highest matching candidate if score >= 60.
     */
    public CandidateMatch findSimilarIssue(int categoryId, int locationId, String description) {
        try {
            List<Issue> openIssues = issueDAO.findOpenIssues();
            CandidateMatch bestMatch = null;
            int highestScore = 0;

            for (Issue issue : openIssues) {
                SimilarityResult res = TextSimilarityUtil.calculate(
                        categoryId, issue.getCategoryId(),
                        locationId, issue.getLocationId(),
                        description, issue.getTitle()
                );

                if (res.isSimilar() && res.getTotalScore() > highestScore) {
                    highestScore = res.getTotalScore();
                    bestMatch = new CandidateMatch(issue, res);
                }
            }

            return bestMatch;
        } catch (SQLException e) {
            System.err.println("[CampusPulse] Similarity search error: " + e.getMessage());
            return null;
        }
    }
}
