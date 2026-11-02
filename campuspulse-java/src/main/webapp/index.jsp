<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:choose>
    <c:when test="${not empty sessionScope.currentUser}">
        <c:choose>
            <c:when test="${sessionScope.currentUser.admin}">
                <c:redirect url="/admin/dashboard"/>
            </c:when>
            <c:otherwise>
                <c:redirect url="/student/dashboard"/>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <c:redirect url="/login.jsp"/>
    </c:otherwise>
</c:choose>
