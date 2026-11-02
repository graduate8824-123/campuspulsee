<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Administrator Dashboard - CampusPulse</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        body { background-color: #f8fafc; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
        .stat-box { border-radius: 10px; border: 1px solid #e2e8f0; transition: transform 0.2s; }
        .stat-box:hover { transform: translateY(-2px); }
        .table-card { border-radius: 10px; border: 1px solid #e2e8f0; }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm">
        <div class="container-fluid px-4">
            <a class="navbar-brand d-flex items-center gap-2 fw-bold" href="${pageContext.request.contextPath}/admin/dashboard">
                <span class="badge bg-danger">ADMIN</span> CampusPulse
            </a>
            <div class="navbar-nav ms-auto d-flex flex-row align-items-center gap-3">
                <span class="text-light small"><i class="bi bi-shield-lock-fill text-danger me-1"></i> ${sessionScope.currentUser.name}</span>
                <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/logout">
                    <i class="bi bi-box-arrow-right"></i> Logout
                </a>
            </div>
        </div>
    </nav>

    <div class="container-fluid px-4 py-4">
        <!-- Top Title -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h1 class="h4 fw-bold text-dark mb-1">Campus Administration Dashboard</h1>
                <p class="text-muted small mb-0">Centralized detection, grouping, prioritization, and resolution tracking of campus issues.</p>
            </div>
            <div class="badge bg-success-subtle text-success border border-success-subtle px-3 py-2">
                <i class="bi bi-check2-circle me-1"></i> System Online • MySQL 8.x Connected
            </div>
        </div>

        <!-- 4 Metric Cards -->
        <div class="row g-3 mb-4">
            <div class="col-sm-6 col-xl-3">
                <div class="card stat-box bg-white p-3 shadow-xs">
                    <span class="text-secondary small fw-semibold">Total Complaints Reported</span>
                    <div class="h3 fw-bold text-primary mt-1 mb-0">${stats.totalComplaints}</div>
                    <span class="text-muted small mt-1">Individual student submissions</span>
                </div>
            </div>
            <div class="col-sm-6 col-xl-3">
                <div class="card stat-box bg-white p-3 shadow-xs">
                    <span class="text-secondary small fw-semibold">Active Issue Groups</span>
                    <div class="h3 fw-bold text-dark mt-1 mb-0">${stats.openIssues}</div>
                    <span class="text-muted small mt-1">Clustered common problems</span>
                </div>
            </div>
            <div class="col-sm-6 col-xl-3">
                <div class="card stat-box bg-white p-3 shadow-xs">
                    <span class="text-secondary small fw-semibold">High Priority Attention</span>
                    <div class="h3 fw-bold text-danger mt-1 mb-0">${stats.highPriorityIssues}</div>
                    <span class="text-muted small mt-1">Requiring immediate action</span>
                </div>
            </div>
            <div class="col-sm-6 col-xl-3">
                <div class="card stat-box bg-white p-3 shadow-xs">
                    <span class="text-secondary small fw-semibold">Resolved Issues</span>
                    <div class="h3 fw-bold text-success mt-1 mb-0">${stats.resolvedComplaints}</div>
                    <span class="text-muted small mt-1">Fully closed and verified</span>
                </div>
            </div>
        </div>

        <!-- Active Aggregated Issue Groups (Core Problem-Solving Feature) -->
        <div class="card table-card bg-white shadow-xs p-4 mb-4">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <div>
                    <h2 class="h5 fw-bold text-dark mb-1">
                        <i class="bi bi-diagram-3 text-primary me-1"></i> Aggregated Campus Issues (Issue Clusters)
                    </h2>
                    <p class="text-muted small mb-0">Multiple student reports clustered into single manageable campus issues by the similarity engine.</p>
                </div>
            </div>

            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr class="small text-secondary">
                            <th>Issue Code</th>
                            <th>Title &amp; Problem</th>
                            <th>Category</th>
                            <th>Location</th>
                            <th>Student Reports</th>
                            <th>Priority</th>
                            <th>Status</th>
                            <th>Assigned To</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="issue" items="${openIssues}">
                            <tr>
                                <td class="font-monospace fw-bold text-primary">${issue.issueCode}</td>
                                <td class="fw-semibold">${issue.title}</td>
                                <td><span class="badge bg-light text-dark border">${issue.categoryName}</span></td>
                                <td>${issue.locationName}</td>
                                <td>
                                    <span class="badge bg-primary text-white rounded-pill px-2.5 py-1">
                                        <i class="bi bi-people-fill me-1"></i> ${issue.reportCount} reports
                                    </span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${issue.priority == 'HIGH'}">
                                            <span class="badge bg-danger-subtle text-danger border border-danger-subtle">HIGH</span>
                                        </c:when>
                                        <c:when test="${issue.priority == 'MEDIUM'}">
                                            <span class="badge bg-warning-subtle text-warning-emphasis border border-warning-subtle">MEDIUM</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-success-subtle text-success border border-success-subtle">LOW</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <span class="badge bg-info-subtle text-info-emphasis border border-info-subtle">${issue.status}</span>
                                </td>
                                <td class="small text-muted">${issue.assignedTo}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/issue?id=${issue.id}" class="btn btn-sm btn-outline-primary">
                                        Manage Issue <i class="bi bi-arrow-right"></i>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Filter & Search Section for Individual Complaints -->
        <div class="card table-card bg-white shadow-xs p-4">
            <h2 class="h5 fw-bold text-dark mb-3">All Student Complaints</h2>

            <!-- Filter Controls Form -->
            <form action="${pageContext.request.contextPath}/admin/dashboard" method="get" class="row g-2 mb-3 bg-light p-3 rounded-3 border">
                <div class="col-md-2">
                    <label class="form-label small fw-semibold text-secondary">Category</label>
                    <select name="categoryId" class="form-select form-select-sm">
                        <option value="">All Categories</option>
                        <c:forEach var="c" items="${categories}">
                            <option value="${c.id}" ${selectedCategory == c.id ? 'selected' : ''}>${c.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label small fw-semibold text-secondary">Location</label>
                    <select name="locationId" class="form-select form-select-sm">
                        <option value="">All Locations</option>
                        <c:forEach var="l" items="${locations}">
                            <option value="${l.id}" ${selectedLocation == l.id ? 'selected' : ''}>${l.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label small fw-semibold text-secondary">Status</label>
                    <select name="status" class="form-select form-select-sm">
                        <option value="ALL">All Statuses</option>
                        <option value="SUBMITTED" ${selectedStatus == 'SUBMITTED' ? 'selected' : ''}>SUBMITTED</option>
                        <option value="UNDER_REVIEW" ${selectedStatus == 'UNDER_REVIEW' ? 'selected' : ''}>UNDER REVIEW</option>
                        <option value="IN_PROGRESS" ${selectedStatus == 'IN_PROGRESS' ? 'selected' : ''}>IN PROGRESS</option>
                        <option value="RESOLVED" ${selectedStatus == 'RESOLVED' ? 'selected' : ''}>RESOLVED</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <label class="form-label small fw-semibold text-secondary">Priority</label>
                    <select name="priority" class="form-select form-select-sm">
                        <option value="ALL">All Priorities</option>
                        <option value="HIGH" ${selectedPriority == 'HIGH' ? 'selected' : ''}>HIGH</option>
                        <option value="MEDIUM" ${selectedPriority == 'MEDIUM' ? 'selected' : ''}>MEDIUM</option>
                        <option value="LOW" ${selectedPriority == 'LOW' ? 'selected' : ''}>LOW</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <label class="form-label small fw-semibold text-secondary">Search Keyword</label>
                    <input type="text" name="keyword" class="form-control form-control-sm" placeholder="Search description..." value="${keyword}">
                </div>
                <div class="col-md-1 d-flex align-items-end">
                    <button type="submit" class="btn btn-sm btn-primary w-100">
                        <i class="bi bi-funnel"></i> Filter
                    </button>
                </div>
            </form>

            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="table-light">
                        <tr class="small text-secondary">
                            <th>ID</th>
                            <th>Student</th>
                            <th>Category</th>
                            <th>Location</th>
                            <th>Description</th>
                            <th>Linked Issue</th>
                            <th>Priority</th>
                            <th>Status</th>
                            <th>Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="cmp" items="${complaints}">
                            <tr>
                                <td class="font-monospace fw-bold text-primary">${cmp.complaintCode}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${cmp.anonymous}">
                                            <span class="badge bg-secondary-subtle text-secondary border">
                                                <i class="bi bi-eye-slash-fill me-1"></i> Anonymous
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="fw-semibold text-dark">${cmp.studentName}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td><span class="badge bg-light text-dark border">${cmp.categoryName}</span></td>
                                <td>${cmp.locationName}</td>
                                <td class="text-truncate" style="max-width: 250px;" title="${cmp.description}">${cmp.description}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty cmp.issueId}">
                                            <a href="${pageContext.request.contextPath}/admin/issue?id=${cmp.issueId}" class="badge bg-indigo-50 text-primary text-decoration-none border border-primary-subtle">
                                                Issue #${cmp.issueId}
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted small">None</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${cmp.priority == 'HIGH'}"><span class="badge bg-danger-subtle text-danger">HIGH</span></c:when>
                                        <c:when test="${cmp.priority == 'MEDIUM'}"><span class="badge bg-warning-subtle text-warning-emphasis">MEDIUM</span></c:when>
                                        <c:otherwise><span class="badge bg-success-subtle text-success">LOW</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td><span class="badge bg-light text-secondary border">${cmp.status}</span></td>
                                <td class="small text-muted">${cmp.createdAt}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</body>
</html>
