<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Application Error - CampusPulse</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        body { background-color: #f8fafc; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
    </style>
</head>
<body>
    <div class="container min-vh-100 d-flex flex-column justify-content-center py-5">
        <div class="row justify-content-center">
            <div class="col-md-6 col-lg-5 text-center">
                <div class="d-inline-flex p-3 bg-danger-subtle text-danger rounded-circle mb-3">
                    <i class="bi bi-exclamation-triangle fs-1"></i>
                </div>
                <h1 class="h4 fw-bold text-dark mb-2">Something went wrong</h1>
                <p class="text-secondary small mb-4">
                    <c:choose>
                        <c:when test="${not empty errorMessage}">
                            ${errorMessage}
                        </c:when>
                        <c:otherwise>
                            An unexpected error occurred while processing your request. If MySQL is not running, please verify your database connection in MySQL Workbench.
                        </c:otherwise>
                    </c:choose>
                </p>

                <div class="d-flex justify-content-center gap-2">
                    <a href="javascript:history.back()" class="btn btn-outline-secondary btn-sm">
                        <i class="bi bi-arrow-left me-1"></i> Go Back
                    </a>
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-sm">
                        <i class="bi bi-house me-1"></i> Return to Home
                    </a>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
