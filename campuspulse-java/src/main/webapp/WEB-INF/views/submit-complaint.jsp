<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Submit Campus Complaint - CampusPulse</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        body { background-color: #f8fafc; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
        .navbar-brand-badge { background-color: #4f46e5; color: white; border-radius: 6px; padding: 2px 8px; font-weight: 700; }
        .card-custom { border-radius: 10px; border: 1px solid #e2e8f0; }
    </style>
</head>
<body>
    <!-- Navigation Bar -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark sticky-top shadow-sm">
        <div class="container">
            <a class="navbar-brand d-flex items-center gap-2 fw-bold" href="${pageContext.request.contextPath}/student/dashboard">
                <span class="navbar-brand-badge">CP</span> CampusPulse
            </a>
            <div class="navbar-nav ms-auto d-flex flex-row align-items-center gap-3">
                <a class="nav-link text-light" href="${pageContext.request.contextPath}/student/dashboard"><i class="bi bi-speedometer2"></i> Dashboard</a>
                <a class="nav-link text-light" href="${pageContext.request.contextPath}/student/my-complaints"><i class="bi bi-clock-history"></i> My Complaints</a>
                <span class="text-secondary">|</span>
                <span class="text-light small"><i class="bi bi-person-circle"></i> ${sessionScope.currentUser.name}</span>
                <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/logout"><i class="bi bi-box-arrow-right"></i></a>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container py-4">
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <div class="card card-custom bg-white p-4 shadow-sm">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <div>
                            <h1 class="h4 fw-bold text-dark mb-1">Report a Campus Problem</h1>
                            <p class="text-muted small mb-0">Provide details so college administration can detect and resolve the issue.</p>
                        </div>
                        <span class="badge bg-primary-subtle text-primary border border-primary-subtle">Student Portal</span>
                    </div>

                    <form id="complaintForm" action="${pageContext.request.contextPath}/complaint/submit" method="post">
                        <!-- Hidden inputs for joining similar issues -->
                        <input type="hidden" id="joinIssueId" name="joinIssueId" value="">
                        <input type="hidden" id="createNewIssue" name="createNewIssue" value="true">
                        <input type="hidden" id="newIssueTitle" name="newIssueTitle" value="">

                        <!-- Category -->
                        <div class="mb-3">
                            <label for="categoryId" class="form-label small fw-semibold text-secondary">Problem Category</label>
                            <select class="form-select" id="categoryId" name="categoryId" required onchange="checkSimilarIssue()">
                                <option value="">-- Select Category --</option>
                                <c:forEach var="cat" items="${categories}">
                                    <option value="${cat.id}">
                                        ${cat.name} <c:if test="${cat.critical}">(Priority Attention)</c:if>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <!-- Location -->
                        <div class="mb-3">
                            <label for="locationId" class="form-label small fw-semibold text-secondary">Campus Location</label>
                            <select class="form-select" id="locationId" name="locationId" required onchange="checkSimilarIssue()">
                                <option value="">-- Select Location --</option>
                                <c:forEach var="loc" items="${locations}">
                                    <option value="${loc.id}">${loc.name} (${loc.zone})</option>
                                </c:forEach>
                            </select>
                        </div>

                        <!-- Description -->
                        <div class="mb-3">
                            <label for="description" class="form-label small fw-semibold text-secondary">Problem Description</label>
                            <textarea class="form-control" id="description" name="description" rows="4" required 
                                placeholder="Describe what is wrong (e.g. WiFi is not working in Block A)..."
                                onkeyup="debounceSimilarCheck()"></textarea>
                            <div class="form-text small">Be specific about the symptoms to help our similarity detection algorithm.</div>
                        </div>

                        <!-- Anonymous Reporting Checkbox -->
                        <div class="form-check p-3 bg-light rounded-3 mb-4 border">
                            <input class="form-check-input ms-0 me-2" type="checkbox" id="anonymous" name="anonymous" value="true">
                            <label class="form-check-label small" for="anonymous">
                                <strong class="text-dark d-block">Report Anonymously to Administrator UI</strong>
                                <span class="text-muted">Your name and email will be hidden on public administrative dashboards. The system internally records your user account for system integrity.</span>
                            </label>
                        </div>

                        <!-- Dynamic Similar Issue Detection Alert Box -->
                        <div id="similarityAlert" class="alert alert-warning border-warning d-none mb-4" role="alert">
                            <div class="d-flex align-items-start gap-2">
                                <i class="bi bi-info-circle-fill fs-5 text-warning flex-shrink-0 mt-1"></i>
                                <div>
                                    <div class="fw-bold text-dark mb-1">A Similar Campus Issue Already Exists!</div>
                                    <div class="small text-secondary mb-2" id="similarityMessage"></div>
                                    <div class="d-flex gap-2">
                                        <button type="button" class="btn btn-sm btn-success fw-semibold" onclick="joinExistingIssue()">
                                            <i class="bi bi-link-45deg"></i> Join Existing Issue (Recommended)
                                        </button>
                                        <button type="button" class="btn btn-sm btn-outline-secondary" onclick="dismissSimilarAlert()">
                                            Submit as Separate Problem
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Action Buttons -->
                        <div class="d-flex justify-content-between align-items-center">
                            <a href="${pageContext.request.contextPath}/student/dashboard" class="btn btn-outline-secondary">Cancel</a>
                            <button type="submit" class="btn btn-primary px-4 py-2 fw-semibold shadow-sm">
                                <i class="bi bi-send me-1"></i> Submit Complaint
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <!-- Client-Side Similarity AJAX Script -->
    <script>
        let checkTimeout = null;
        let detectedIssue = null;

        function debounceSimilarCheck() {
            clearTimeout(checkTimeout);
            checkTimeout = setTimeout(checkSimilarIssue, 500);
        }

        function checkSimilarIssue() {
            const cat = document.getElementById('categoryId').value;
            const loc = document.getElementById('locationId').value;
            const desc = document.getElementById('description').value;

            if (!cat || !loc || desc.trim().length < 5) {
                document.getElementById('similarityAlert').classList.add('d-none');
                return;
            }

            fetch('${pageContext.request.contextPath}/api/check-similarity?categoryId=' + cat + '&locationId=' + loc + '&description=' + encodeURIComponent(desc))
                .then(res => res.json())
                .then(data => {
                    if (data.hasMatch) {
                        detectedIssue = data;
                        document.getElementById('similarityMessage').innerHTML = 
                            '<strong>Issue ' + data.issueCode + ': ' + data.issueTitle + '</strong><br>' +
                            'This issue already has <strong>' + data.reportCount + '</strong> student report(s).<br>' +
                            '<span class="text-muted">Calculated Similarity Score: ' + data.score + '/100</span>';
                        document.getElementById('similarityAlert').classList.remove('d-none');
                    } else {
                        document.getElementById('similarityAlert').classList.add('d-none');
                    }
                })
                .catch(() => {});
        }

        function joinExistingIssue() {
            if (detectedIssue) {
                document.getElementById('joinIssueId').value = detectedIssue.issueId;
                document.getElementById('createNewIssue').value = "false";
                document.getElementById('complaintForm').submit();
            }
        }

        function dismissSimilarAlert() {
            document.getElementById('joinIssueId').value = "";
            document.getElementById('createNewIssue').value = "true";
            document.getElementById('similarityAlert').classList.add('d-none');
        }
    </script>
</body>
</html>
