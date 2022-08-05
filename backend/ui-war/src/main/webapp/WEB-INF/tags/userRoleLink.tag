<%@ tag language="java" body-content="empty" description="Renders link or label for user role" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<%@ attribute name="roleField" required="true" type="com.foros.model.security.UserRole" %>

<c:choose>
    <c:when test="${ad:isPermitted0('UserRole.view')}">
        <a href="${_context}/UserRole/view.action?id=${roleField.id}"><c:out
                value="${roleField.name}"/></a>
    </c:when>
    <c:otherwise>
        <c:out value="${roleField.name}"/>
    </c:otherwise>
</c:choose>