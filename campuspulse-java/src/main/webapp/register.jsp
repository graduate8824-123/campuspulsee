<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Registration - CampusPulse</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        body { background-color: #f8fafc; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
        .auth-card { border-radius: 12px; border: 1px solid #e2e8f0; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05); }
    </style>
</head>
<body>
    <div class="container min-vh-100 d-flex flex-column justify-content-center py-5">
        <div class="row justify-content-center">
            <div class="col-md-6 col-lg-5">
                <div class="text-center mb-4">
                    <h2 class="h4 fw-bold text-dark mb-1">CampusPulse</h2>
                    <p class="text-muted small">Student Account Registration</p>
                </div>

                <div class="card auth-card bg-white p-4">
                    <h1 class="h5 fw-bold text-dark mb-3">Create Student Account</h1>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger py-2 small" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-1"></i> ${errorMessage}
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/register" method="post">
                        <div class="mb-3">
                            <label for="name" class="form-label small fw-semibold text-secondary">Full Name</label>
                            <input type="text" class="form-control" id="name" name="name" required placeholder="e.g. Rahul Verma">
                        </div>

                        <div class="mb-3">
                            <label for="email" class="form-label small fw-semibold text-secondary">College Email</label>
                            <input type="email" class="form-control" id="email" name="email" required placeholder="student@college.edu">
                        </div>

                        <div class="mb-3">
                            <label for="department" class="form-label small fw-semibold text-secondary">Department / Branch</label>
                            <select class="form-select" id="department" name="department">
                                <option value="Computer Science">Computer Science &amp; Engg</option>
                                <option value="Information Technology">Information Technology</option>
                                <option value="Electronics">Electronics &amp; Communication</option>
                                <option value="Mechanical">Mechanical Engineering</option>
                                <option value="Civil">Civil Engineering</option>
                                <option value="Other">Other Branch</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label for="password" class="form-label small fw-semibold text-secondary">Password</label>
                            <input type="password" class="form-control" id="password" name="password" required minlength="6" placeholder="Minimum 6 characters">
                        </div>

                        <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold shadow-sm">
                            <i class="bi bi-person-plus me-1"></i> Complete Registration
                        </button>
                    </form>

                    <div class="mt-3 text-center small text-muted">
                        Already have an account? <a href="${pageContext.request.contextPath}/login" class="text-decoration-none fw-semibold">Sign in</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
