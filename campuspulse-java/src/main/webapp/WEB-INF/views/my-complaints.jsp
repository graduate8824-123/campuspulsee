<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Submitted Complaints - CampusPulse</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        body { background-color: #f8fafc; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
        .card-custom { border-radius: 10px; border: 1px solid #e2e8f0; }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm">
        <div class="container">
            <a class="navbar-brand d-flex items-center gap-2 fw-bold" href="${pageContext.request.contextPath}/student/dashboard">
                <span class="badge bg-primary">CP</span> CampusPulse
            </a>
            <div class="navbar-nav ms-auto d-flex flex-row align-items-center gap-3">
                <a class="nav-link text-light" href="${pageContext.request.contextPath}/student/dashboard">Dashboard</a>
                <a class="nav-link active text-white fw-semibold" href="${pageContext.request.contextPath}/student/my-complaints">My Complaints</a>
                <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/student/submit-complaint">
                    <i class="bi bi-plus-circle me-1"></i> New Complaint
                </a>
                <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/logout"><i class="bi bi-box-arrow-right"></i></a>
            </div>
        </div>
    </nav>

    <div class="container py-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h1 class="h4 fw-bold text-dark mb-1">My Submitted Complaints</h1>
                <p class="text-muted small mb-0">Track the review, investigation, and resolution progress of your reports.</p>
            </div>
            <a href="${pageContext.request.contextPath}/student/submit-complaint" class="btn btn-primary shadow-sm">
                <i class="bi bi-plus-lg me-1"></i> Report Another Issue
            </a>
        </div>

        <c:if test="${not empty param.success}">
            <div class="alert alert-success py-2 small mb-3">
                <i class="bi bi-check-circle-fill me-1"></i> ${param.success}
            </div>
        </c:if>

        <div class="card card-custom bg-white shadow-sm overflow-hidden">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr class="small text-secondary">
                            <th>Complaint ID</th>
                            <th>Category</th>
                            <th>Location</th>
                            <th>Description</th>
                            <th>Linked Issue</th>
                            <th>Type</th>
                            <th>Status</th>
                            <th>Submitted On</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty complaints}">
                                <c:forEach var="c" items="${complaints}">
                                    <tr>
                                        <td class="font-monospace fw-bold text-primary">${c.complaintCode}</td>
                                        <td><span class="badge bg-light text-dark border">${c.categoryName}</span></td>
                                        <td>${c.locationName}</td>
                                        <td class="text-truncate" style="max-width: 250px;" title="${c.description}">
                                            ${c.description}
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty c.issueId}">
                                                    <span class="badge bg-indigo-50 text-primary border border-primary-subtle">
                                                        <i class="bi bi-diagram-3-fill me-1"></i> Issue #${c.issueId}
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted small">Independent</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${c.anonymous}">
                                                    <span class="badge bg-secondary-subtle text-secondary border">Anonymous</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-light text-dark border">Standard</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${c.status == 'RESOLVED'}">
                                                    <span class="badge bg-success-subtle text-success border border-success-subtle">Resolved</span>
                                                </c:when>
                                                <c:when test="${c.status == 'IN_PROGRESS'}">
                                                    <span class="badge bg-warning-subtle text-warning-emphasis border border-warning-subtle">In Progress</span>
                                                </c:when>
                                                <c:when test="${c.status == 'UNDER_REVIEW'}">
                                                    <span class="badge bg-info-subtle text-info-emphasis border border-info-subtle">Under Review</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-light text-secondary border">Submitted</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="small text-muted">${c.createdAt}</td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="8" class="text-center py-5 text-muted">
                                        <i class="bi bi-inbox fs-1 d-block mb-2 text-secondary"></i>
                                        You have not submitted any campus complaints yet.
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>
