<%@ tag language="java" body-content="empty" description="Renders link or label for the country" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ad" uri="/ad/serverUI" %>

<%@ attribute name="countryCode" required="true" %>

<c:choose>
    <c:when test="${ad:isPermitted0('Country.view')}">
        <a href="${_context}/Country/view.action?id=${countryCode}"><c:out
                value="${ad:resolveGlobal('country', countryCode, false)}"/></a>
    </c:when>
    <c:otherwise>
        <c:out value="${ad:resolveGlobal('country', countryCode, false)}"/>
    </c:otherwise>
</c:choose>