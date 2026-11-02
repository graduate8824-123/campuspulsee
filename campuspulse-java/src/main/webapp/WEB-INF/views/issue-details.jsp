<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Issue ${issue.issueCode} Details - CampusPulse</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        body { background-color: #f8fafc; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
        .detail-card { border-radius: 10px; border: 1px solid #e2e8f0; }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm">
        <div class="container-fluid px-4">
            <a class="navbar-brand d-flex items-center gap-2 fw-bold" href="${pageContext.request.contextPath}/admin/dashboard">
                <span class="badge bg-danger">ADMIN</span> CampusPulse
            </a>
            <div class="navbar-nav ms-auto d-flex flex-row align-items-center gap-3">
                <a class="nav-link text-light" href="${pageContext.request.contextPath}/admin/dashboard">
                    <i class="bi bi-arrow-left"></i> Back to Dashboard
                </a>
            </div>
        </div>
    </nav>

    <div class="container py-4">
        <c:if test="${not empty param.success}">
            <div class="alert alert-success py-2 small mb-3">
                <i class="bi bi-check-circle-fill me-1"></i> ${param.success}
            </div>
        </c:if>

        <div class="row g-4">
            <!-- Left Column: Issue Overview & Clustered Complaints -->
            <div class="col-lg-8">
                <div class="card detail-card bg-white p-4 shadow-xs mb-4">
                    <div class="d-flex justify-content-between align-items-start mb-2">
                        <div>
                            <span class="badge bg-primary-subtle text-primary font-monospace fw-bold mb-1">${issue.issueCode}</span>
                            <h1 class="h4 fw-bold text-dark mb-1">${issue.title}</h1>
                            <p class="text-muted small mb-0">
                                <i class="bi bi-geo-alt-fill text-danger me-1"></i> ${issue.locationName} &nbsp;•&nbsp; 
                                <i class="bi bi-tag-fill text-secondary me-1"></i> ${issue.categoryName}
                            </p>
                        </div>
                        <div class="text-end">
                            <span class="badge bg-primary fs-6 px-3 py-2 rounded-pill">
                                <i class="bi bi-people-fill me-1"></i> ${issue.reportCount} Student Reports
                            </span>
                        </div>
                    </div>
                </div>

                <!-- Clustered Student Complaints -->
                <div class="card detail-card bg-white p-4 shadow-xs">
                    <h2 class="h5 fw-bold text-dark mb-3">
                        <i class="bi bi-chat-left-text text-primary me-1"></i> Clustered Student Complaints (${groupedComplaints.size()})
                    </h2>
                    <p class="text-muted small mb-3">
                        These distinct student complaints were clustered under this campus issue by the CampusPulse similarity algorithm or direct student confirmation.
                    </p>

                    <div class="list-group list-group-flush">
                        <c:forEach var="c" items="${groupedComplaints}">
                            <div class="list-group-item px-0 py-3">
                                <div class="d-flex justify-content-between align-items-center mb-1">
                                    <span class="font-monospace fw-bold text-primary small">${c.complaintCode}</span>
                                    <span class="small text-muted">${c.createdAt}</span>
                                </div>
                                <p class="text-dark mb-2">${c.description}</p>
                                <div class="d-flex justify-content-between align-items-center small">
                                    <div>
                                        <c:choose>
                                            <c:when test="${c.anonymous}">
                                                <span class="badge bg-secondary-subtle text-secondary">Anonymous Student</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-secondary fw-semibold"><i class="bi bi-person"></i> ${c.studentName} (${c.studentEmail})</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <span class="badge bg-light text-secondary border">${c.status}</span>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>

            <!-- Right Column: Admin Status & Priority Update Box -->
            <div class="col-lg-4">
                <div class="card detail-card bg-white p-4 shadow-xs sticky-top" style="top: 80px;">
                    <h2 class="h5 fw-bold text-dark mb-3">Manage Resolution</h2>

                    <form action="${pageContext.request.contextPath}/admin/update-status" method="post">
                        <input type="hidden" name="issueId" value="${issue.id}">

                        <!-- Current Priority -->
                        <div class="mb-3">
                            <label class="form-label small fw-semibold text-secondary">Priority Level</label>
                            <select class="form-select" name="priority">
                                <option value="LOW" ${issue.priority == 'LOW' ? 'selected' : ''}>LOW (Minor problem)</option>
                                <option value="MEDIUM" ${issue.priority == 'MEDIUM' ? 'selected' : ''}>MEDIUM (Moderate impact)</option>
                                <option value="HIGH" ${issue.priority == 'HIGH' ? 'selected' : ''}>HIGH (Urgent campus attention)</option>
                            </select>
                        </div>

                        <!-- Status Workflow -->
                        <div class="mb-3">
                            <label class="form-label small fw-semibold text-secondary">Resolution Status</label>
                            <select class="form-select" name="status">
                                <option value="OPEN" ${issue.status == 'OPEN' ? 'selected' : ''}>OPEN</option>
                                <option value="UNDER_REVIEW" ${issue.status == 'UNDER_REVIEW' ? 'selected' : ''}>UNDER REVIEW</option>
                                <option value="IN_PROGRESS" ${issue.status == 'IN_PROGRESS' ? 'selected' : ''}>IN PROGRESS</option>
                                <option value="RESOLVED" ${issue.status == 'RESOLVED' ? 'selected' : ''}>RESOLVED</option>
                            </select>
                            <div class="form-text small">Updating this cascades status to all ${issue.reportCount} linked complaints.</div>
                        </div>

                        <!-- Remarks -->
                        <div class="mb-4">
                            <label class="form-label small fw-semibold text-secondary">Administrative Remarks / Action Taken</label>
                            <textarea class="form-control" name="remarks" rows="3" required placeholder="e.g. Network engineer dispatched to replace router in Block A 2nd floor."></textarea>
                        </div>

                        <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold shadow-sm">
                            <i class="bi bi-arrow-repeat me-1"></i> Update &amp; Cascade Status
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
