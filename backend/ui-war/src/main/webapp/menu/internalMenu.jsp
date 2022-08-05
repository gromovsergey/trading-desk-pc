<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ include file="/auditLog/logHighlightMenu.jsp" %>
<c:choose>
    <c:when test="${_context == '/advertiser'}" >
        <jsp:include page="/menu/advertiserMenu.jsp"/>
    </c:when>
    <c:when test="${_context == '/publisher'}" >
        <jsp:include page="/menu/publisherMenu.jsp"/>
    </c:when>
    <c:when test="${_context == '/isp'}" >
        <jsp:include page="/menu/ispMenu.jsp"/>
    </c:when>
    <c:when test="${_context == '/cmp'}" >
        <jsp:include page="/menu/cmpMenu.jsp"/>
    </c:when>
    <c:when test="${_context == '/admin'}" >
        <jsp:include page="/menu/internalMenuBody.jsp"/>
    </c:when>
</c:choose>
