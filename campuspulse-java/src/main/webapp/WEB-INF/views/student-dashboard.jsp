<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Dashboard - CampusPulse</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        body { background-color: #f8fafc; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
        .stat-card { border-radius: 12px; border: 1px solid #e2e8f0; }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm">
        <div class="container">
            <a class="navbar-brand d-flex items-center gap-2 fw-bold" href="${pageContext.request.contextPath}/student/dashboard">
                <span class="badge bg-primary">CP</span> CampusPulse
            </a>
            <div class="navbar-nav ms-auto d-flex flex-row align-items-center gap-3">
                <span class="text-light small"><i class="bi bi-person-fill"></i> ${sessionScope.currentUser.name}</span>
                <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/logout"><i class="bi bi-box-arrow-right"></i> Logout</a>
            </div>
        </div>
    </nav>

    <div class="container py-4">
        <!-- Welcome banner -->
        <div class="p-4 bg-primary text-white rounded-3 shadow-sm mb-4 d-flex justify-content-between align-items-center flex-wrap gap-3">
            <div>
                <h1 class="h4 fw-bold mb-1">Welcome back, ${sessionScope.currentUser.name}!</h1>
                <p class="mb-0 opacity-75 small">Department of ${sessionScope.currentUser.department} • College Campus Portal</p>
            </div>
            <a href="${pageContext.request.contextPath}/student/submit-complaint" class="btn btn-light text-primary fw-semibold shadow-sm">
                <i class="bi bi-plus-circle-fill me-1"></i> Report New Problem
            </a>
        </div>

        <!-- Metrics -->
        <div class="row g-3 mb-4">
            <div class="col-sm-6 col-md-4">
                <div class="card stat-card bg-white p-3 shadow-xs">
                    <span class="text-secondary small fw-semibold">Total Complaints Reported</span>
                    <div class="h3 fw-bold text-dark mt-1 mb-0">${totalReported}</div>
                </div>
            </div>
            <div class="col-sm-6 col-md-4">
                <div class="card stat-card bg-white p-3 shadow-xs">
                    <span class="text-secondary small fw-semibold">Issues Resolved</span>
                    <div class="h3 fw-bold text-success mt-1 mb-0">${resolvedCount}</div>
                </div>
            </div>
            <div class="col-sm-6 col-md-4">
                <div class="card stat-card bg-white p-3 shadow-xs">
                    <span class="text-secondary small fw-semibold">Pending Review</span>
                    <div class="h3 fw-bold text-warning mt-1 mb-0">${totalReported - resolvedCount}</div>
                </div>
            </div>
        </div>

        <!-- Recent submissions -->
        <div class="card stat-card bg-white shadow-xs p-4">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h2 class="h6 fw-bold text-dark mb-0">My Recent Reports</h2>
                <a href="${pageContext.request.contextPath}/student/my-complaints" class="text-decoration-none small fw-semibold">View All</a>
            </div>

            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr class="small text-secondary">
                            <th>ID</th>
                            <th>Category</th>
                            <th>Location</th>
                            <th>Problem</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="c" items="${recentComplaints}">
                            <tr>
                                <td class="font-monospace text-primary fw-semibold">${c.complaintCode}</td>
                                <td><span class="badge bg-light text-dark border">${c.categoryName}</span></td>
                                <td>${c.locationName}</td>
                                <td class="text-truncate" style="max-width: 300px;">${c.description}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${c.status == 'RESOLVED'}">
                                            <span class="badge bg-success-subtle text-success">Resolved</span>
                                        </c:when>
                                        <c:when test="${c.status == 'IN_PROGRESS'}">
                                            <span class="badge bg-warning-subtle text-warning-emphasis">In Progress</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-info-subtle text-info-emphasis">Under Review</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>
