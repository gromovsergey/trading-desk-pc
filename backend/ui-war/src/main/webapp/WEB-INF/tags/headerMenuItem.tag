<%@ tag language="java" body-content="empty" description="Highlight Menu Item" %>
<%@ attribute name="menuKey" required="true" %>
<%@ attribute name="activeMenu" required="true" %>
<%@ attribute name="module" required="true" %>
<%@ attribute name="action" required="true" %>
<%@ attribute name="labelKey" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:if test="${empty pageScope.labelKey}">
    <c:set var="labelKey" value="${pageScope.menuKey}"/>
</c:if>

<c:choose>
    <c:when test="${pageScope.menuKey == pageScope.activeMenu}">
        <c:set var="className" value="current"/>
    </c:when>
    <c:otherwise>
        <c:set var="className" value=""/>
    </c:otherwise>
</c:choose>

<td class="${className}">
    <a href="${pageScope.module}${pageScope.action}">
        <span><fmt:message key="${pageScope.labelKey}"/></span></a>
</td>
