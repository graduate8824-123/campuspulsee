<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - CampusPulse</title>
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
            <div class="col-md-5 col-lg-4">
                <div class="text-center mb-4">
                    <div class="d-inline-flex p-3 bg-primary text-white rounded-3 shadow-sm mb-2">
                        <i class="bi bi-shield-check fs-2"></i>
                    </div>
                    <h2 class="h4 fw-bold text-dark mb-1">CampusPulse</h2>
                    <p class="text-muted small">Campus Issue Management &amp; Detection System</p>
                </div>

                <div class="card auth-card bg-white p-4">
                    <h1 class="h5 fw-bold text-dark mb-3">Sign in to your account</h1>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger py-2 small" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-1"></i> ${errorMessage}
                        </div>
                    </c:if>

                    <c:if test="${not empty param.msg}">
                        <div class="alert alert-success py-2 small" role="alert">
                            <i class="bi bi-check-circle-fill me-1"></i> ${param.msg}
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/login" method="post">
                        <div class="mb-3">
                            <label for="email" class="form-label small fw-semibold text-secondary">Campus Email</label>
                            <input type="email" class="form-control" id="email" name="email" required placeholder="name@college.edu" value="${param.email}">
                        </div>

                        <div class="mb-3">
                            <label for="password" class="form-label small fw-semibold text-secondary">Password</label>
                            <input type="password" class="form-control" id="password" name="password" required placeholder="••••••••">
                        </div>

                        <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold shadow-sm">
                            <i class="bi bi-box-arrow-in-right me-1"></i> Sign In
                        </button>
                    </form>

                    <hr class="my-3 text-muted">

                    <div class="p-3 bg-light rounded-2 small text-secondary">
                        <div class="fw-bold mb-1"><i class="bi bi-key-fill text-primary"></i> Lab Demo Credentials:</div>
                        <div><strong>Admin:</strong> admin@college.edu / Password@123</div>
                        <div><strong>Student:</strong> rahul@college.edu / Password@123</div>
                    </div>

                    <div class="mt-3 text-center small text-muted">
                        New student? <a href="${pageContext.request.contextPath}/register" class="text-decoration-none fw-semibold">Register account</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
